package pl.jaca.ircsy.clientnode.connection

import akka.actor.{ActorLogging, ActorRef}
import akka.persistence.{AtLeastOnceDelivery, PersistentActor, Recovery, SnapshotOffer}
import pl.jaca.ircsy.chat.{ConnectionDesc, ServerDesc}
import pl.jaca.ircsy.chat.messages.{ChannelMessage, Notification, PrivateMessage}
import pl.jaca.ircsy.clientnode.connection.ConnectionObservableProxy._
import pl.jaca.ircsy.clientnode.observableactor.ObservableActorProtocol
import pl.jaca.ircsy.clientnode.observableactor.ObservableActorProtocol._
import rx.lang.scala.{Observable, Subject, Subscription}

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Try

/**
  * @author Jaca777
  *         Created 2016-05-01 at 17
  */
class ConnectionObservableProxy(connectionDesc: ConnectionDesc, connectionFactory: ChatConnectionFactory) extends PersistentActor with ActorLogging {

  implicit val executionContext = context.dispatcher

  var state: ProxyState = ProxyState(false, connectionDesc, connectionFactory, Set.empty, Set.empty)

  var connection: ChatConnection = connectionFactory.newConnection(executionContext)

  override def persistenceId: String = "ConnectionProxy-" + connectionDesc.toString

  override def receiveRecover: Receive = {
    case SnapshotOffer(_, offeredState: ProxyState) =>
      setState(offeredState)
    case Start => start()
    case RegisterObserver(observer) => registerObserver(observer)
    case UnregisterObserver(observer) => unregisterObserver(observer)
    case JoinChannel(channel) => joinChannel(channel)
  }

  private def setState(state: ProxyState) {
    if (state.running) connectToServer()
    state.channels.foreach(joinChannel)
  }

  override def receiveCommand: Receive = {
    case Start =>
      persist(Start)(_ => start())
    case Stop =>
      persist(Stop)(_ => stop())
    case cmd: RegisterObserver =>
      persist(cmd)(cmd => registerObserver(cmd.observer))
    case cmd: UnregisterObserver =>
      persist(cmd)(cmd => unregisterObserver(cmd.observer))
    case cmd: JoinChannel =>
      persist(cmd)(_ => joinChannel(cmd.name))
    case cmd: LeaveChannel =>
      persist(cmd)(_ => leaveChannel(cmd.name))
    case SendChannelMessage(channel, msg) =>
      connection.sendChannelMessage(channel, msg)
    case SendPrivateMessage(user, msg) =>
      connection.sendPrivateMessage(user, msg)
  }

  private def start() {
    log.debug(s"Starting connection proxy ($connectionDesc)...")
    val connectingResult = connectToServer()
    connectingResult.foreach {
      _ =>
        state = state.copy(running = true)
        log.debug(s"Connected to server: ${connectionDesc.getServer}")
        startNotifyingMessages()
        saveSnapshot(state)
        state.channels.foreach(joinChannel)
    }
    notifyResult(connectingResult, ConnectedToServer(state.connectionDesc), FailedToConnectToServer(state.connectionDesc))
  }

  private def connectToServer(): Try[Unit] = Try {
    connection.connectTo(state.connectionDesc, 3 seconds)
  }

  private def stop() {
    log.debug(s"Stopping connection proxy ($connectionDesc)...")
    val result = disconnectFromServer()
    notifyResult(result, DisconnectedFromServer(connectionDesc), FailedToDisconnectFromServer(connectionDesc))
    state = state.copy(running = false, observers = Set.empty)
    saveSnapshot(state)
    context.stop(self)
  }

  private def disconnectFromServer(): Try[Unit] = Try {
    connection.disconnect()
  }

  private def registerObserver(observer: Observer) {
    log.debug(s"Registering observer $observer for ($connectionDesc)...")
    state = state.copy(observers = state.observers + observer)
    if (observer.subjects.exists(_ isInterestedIn state))
      observer.ref ! state
  }

  private def unregisterObserver(observer: Observer) {
    log.debug(s"Unregistering observer $observer for ($connectionDesc)...")
    state = state.copy(observers = state.observers - observer)
  }


  private def joinChannel(name: String) {
    val result = Try {
      connection.joinChannel(name)
    }
    result.foreach {
      _ => state = state.copy(channels = state.channels + name)
    }
    notifyResult(result, JoinedChannel(connectionDesc.getServer, name), FailedToJoinChannel(connectionDesc.getServer,name))
  }

  private def leaveChannel(name: String) {
    val result = Try {
      connection.leaveChannel(name)
    }
    result.foreach {
      _ =>
        state = state.copy(channels = state.channels - name)
        saveSnapshot(state) // We don't want it to join and leave every channel.
    }
    notifyResult(result, LeftChannel(connectionDesc.getServer,name), FailedToLeaveChannel(connectionDesc.getServer,name))
  }

  private def startNotifyingMessages() = {
    val channelMessageNotifications = connection
      .channelMessages
      .map(ChannelMessageReceived)

    val privateMessageNotifications = connection
      .privateMessages
      .map(PrivateMessageReceived)

    val notificationNotifications = connection
      .notifications
      .map(NotificationReceived)

    (channelMessageNotifications merge privateMessageNotifications merge notificationNotifications)
      .foreach(notifyObservers)
  }

  def notifyResult(result: Try[_], successMsg: Any, failureMsg: FailureNotification) {
    if (result.isSuccess) notifyObservers(successMsg)
    else {
      failureMsg.cause = result.failed.get
      notifyObservers(failureMsg)
    }
  }

  private def notifyObservers(msg: Any) = {
    val interestedObservers = state.observers.filter(_ isInterestedIn msg)
    interestedObservers.foreach(_.ref ! msg)
  }

}

object ConnectionObservableProxy {

  trait ConnectionNotification

  abstract class FailureNotification extends ConnectionNotification{
    private[ConnectionObservableProxy] var cause: Throwable = null

    def getCause = cause
  }

  case class ProxyState(running: Boolean, connectionDesc: ConnectionDesc, connectionFactory: ChatConnectionFactory, channels: Set[String], observers: Set[Observer]) extends Serializable



  val OnRegisterStateSubject = ClassFilterSubject(classOf[ProxyState])

  trait ConnectionCmd

  object Start extends ConnectionCmd

  object Stop extends ConnectionCmd

  case class ConnectedToServer(connectionDesc: ConnectionDesc) extends ConnectionNotification

  case class FailedToConnectToServer(connectionDesc: ConnectionDesc) extends FailureNotification

  case class DisconnectedFromServer(connectionDesc: ConnectionDesc) extends ConnectionNotification

  case class FailedToDisconnectFromServer(connectionDesc: ConnectionDesc) extends FailureNotification

  case class JoinChannel(name: String) extends ConnectionCmd

  case class LeaveChannel(name: String) extends ConnectionCmd

  case class JoinedChannel(serverDesc: ServerDesc, name: String) extends ConnectionNotification

  case class FailedToJoinChannel(serverDesc: ServerDesc, name: String) extends FailureNotification

  case class LeftChannel(serverDesc: ServerDesc,name: String) extends ConnectionNotification

  case class FailedToLeaveChannel(serverDesc: ServerDesc, name: String) extends FailureNotification

  case class SendChannelMessage(channel: String, msg: String) extends ConnectionCmd

  case class SendPrivateMessage(user: String, msg: String) extends ConnectionCmd

  case class ChannelMessageReceived(channelMessage: ChannelMessage)

  case class PrivateMessageReceived(privateMessage: PrivateMessage)

  case class NotificationReceived(notification: Notification)

}