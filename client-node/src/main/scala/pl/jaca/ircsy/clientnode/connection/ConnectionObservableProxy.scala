package pl.jaca.ircsy.clientnode.connection

import akka.actor.ActorRef
import akka.persistence.{Recovery, AtLeastOnceDelivery, PersistentActor, SnapshotOffer}
import pl.jaca.ircsy.chat.messages.{ChannelMessage, PrivateMessage}
import pl.jaca.ircsy.clientnode.connection.ConnectionObservableProxy._
import pl.jaca.ircsy.clientnode.observableactor.ObservableActorProtocol._
import rx.lang.scala.{Observable, Subject, Subscription}
import scala.concurrent.duration._
import scala.language.postfixOps

import scala.util.Try

/**
  * @author Jaca777
  *         Created 2016-05-01 at 17
  */
class ConnectionObservableProxy(connectionDesc: ConnectionDesc, connectionFactory: ChatConnectionFactory) extends PersistentActor {

  implicit val executionContext = context.dispatcher

  var state: ProxyState = ProxyState(false, connectionDesc, connectionFactory, Set.empty, Set.empty)

  var connection: ChatConnection = connectionFactory.newConnection(executionContext)

  override def persistenceId: String = "ConnectionProxy-" + connectionDesc.toString

  override def receiveRecover: Receive = {
    case SnapshotOffer(_, offeredState: ProxyState) =>
      setState(offeredState)
    case RegisterObserver(observer) => registerObserver(observer)
    case UnregisterObserver(observer) => unregisterObserver(observer)
    case JoinChannel(channel) => joinChannel(channel)
  }

  private def setState(state: ProxyState) {
    if (state.running) connectToServer()
    state.channels.foreach(joinChannel)
    this.state = state
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
    val connectingResult = connectToServer()
    connectingResult.foreach {
      _ => state = state.copy(running = true)
    }
    notifyResult(connectingResult, ConnectedToServer(state.connectionDesc), FailedToConnectToServer(state.connectionDesc))
    state.channels.foreach(joinChannel)
    startNotifyingMessages()
    saveSnapshot(state)
  }

  private def connectToServer(): Try[Unit] = Try {
    connection.connectTo(state.connectionDesc, 3 seconds)
  }

  private def stop() {
    val disconnectingResult = disconnectFromServer()
    disconnectingResult foreach {
      _ =>
        state = state.copy(running = false, observers = Set.empty)
        saveSnapshot(state)
    }
    notifyResult(disconnectingResult, DisconnectedFromServer(connectionDesc), FailedToDisconnectFromServer(connectionDesc))
  }

  private def disconnectFromServer(): Try[Unit] = Try {
    connection.disconnect()
  }

  private def registerObserver(observer: Observer) {
    state = state.copy(observers = state.observers + observer)
    if (observer.subjects.exists(_ isInterestedIn state))
      observer.ref ! state
  }

  private def unregisterObserver(observer: Observer) {
    state = state.copy(observers = state.observers - observer)
  }


  private def joinChannel(name: String) {
    val result = Try {
      connection.joinChannel(name)
    }
    result.foreach {
      _ => state = state.copy(channels = state.channels + name)
    }
    println(result)
    notifyResult(result, JoinedChannel(name), FailedToJoinChannel(name))
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
    notifyResult(result, LeftChannel(name), FailedToLeaveChannel(name))
  }

  private def startNotifyingMessages() = {
    val channelMessageNotifications = connection
      .channelMessages
      .map(ChannelMessageReceived)

    val privateMessageNotifications = connection
      .privateMessages
      .map(PrivateMessageReceived)

    (channelMessageNotifications merge privateMessageNotifications)
      .foreach(notifyObservers)
  }

  def notifyResult(result: Try[_], successMsg: Any, failureMsg: FailureNotification) {
    if (result.isSuccess) notifyObservers(successMsg)
    else {
      failureMsg.cause = result.failed.get
      notifyObservers(failureMsg)
    }
  }

  private def notifyObservers(msg: Any) =
    state.observers
      .filter(_.subjects.exists(_ isInterestedIn msg))
      .map(_.ref)
      .foreach(_ ! msg)

}

object ConnectionObservableProxy {

  abstract class FailureNotification {
    private[ConnectionObservableProxy] var cause: Throwable = null

    def getCause = cause
  }

  case class ProxyState(running: Boolean, connectionDesc: ConnectionDesc, connectionFactory: ChatConnectionFactory, channels: Set[String], observers: Set[Observer]) extends Serializable

  case class ChannelSubject(channelName: String) extends ObserverSubject {
    override def isInterestedIn(notification: Any): Boolean = notification match {
      case ChannelMessageReceived(msg: ChannelMessage) if msg.getChannel == channelName => true
      case LeftChannel(`channelName`) => true
    }
  }

  val OnRegisterStateSubject = ClassFilterSubject(classOf[ProxyState])

  object Start

  object Stop

  case class ConnectedToServer(connectionDesc: ConnectionDesc)

  case class FailedToConnectToServer(chatConnectionDesc: ConnectionDesc) extends FailureNotification

  case class DisconnectedFromServer(connectionDesc: ConnectionDesc)

  case class FailedToDisconnectFromServer(connectionDesc: ConnectionDesc) extends FailureNotification

  case class JoinChannel(name: String)

  case class LeaveChannel(name: String)

  case class JoinedChannel(name: String)

  case class FailedToJoinChannel(name: String) extends FailureNotification

  case class LeftChannel(name: String)

  case class FailedToLeaveChannel(name: String) extends FailureNotification

  case class SendChannelMessage(channel: String, msg: String)

  case class SendPrivateMessage(user: String, msg: String)

  case class ChannelMessageReceived(channelMessage: ChannelMessage)

  case class PrivateMessageReceived(privateMessage: PrivateMessage)

}