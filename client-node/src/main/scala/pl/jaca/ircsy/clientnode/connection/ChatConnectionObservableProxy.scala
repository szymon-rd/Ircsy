package pl.jaca.ircsy.clientnode.connection

import akka.actor.ActorRef
import akka.persistence.{Recovery, AtLeastOnceDelivery, PersistentActor, SnapshotOffer}
import pl.jaca.ircsy.clientnode.connection.ChatConnection.{PrivateMessage, ChannelMessage}
import pl.jaca.ircsy.clientnode.connection.ChatConnectionObservableProxy._
import rx.lang.scala.{Subject, Subscription}

import scala.util.Try

/**
  * @author Jaca777
  *         Created 2016-05-01 at 17
  */
class ChatConnectionObservableProxy(connectionDesc: ChatConnectionDesc, connectionFactory: ChatConnectionFactory) extends PersistentActor {


  var state: ListenerState = ListenerState(false, connectionDesc, connectionFactory, Set.empty, Set.empty)

  var connection: ChatConnection = connectionFactory.newConnection()

  override def persistenceId: String = "Proxy-" + connectionDesc.toString

  override def receiveRecover: Receive = {
    case SnapshotOffer(_, offeredState: ListenerState) =>
      setState(offeredState)
    case Start => start()
    case Stop => stop()
    case RegisterObserver(observer) => registerObserver(observer)
    case UnregisterObserver(observer) => unregisterObserver(observer)
    case JoinChannel(channel) => joinChannel(channel)
  }

  private def setState(state: ListenerState) {
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
    startNotifying()
    saveSnapshot(state)
  }

  private def connectToServer(): Try[Unit] = Try {
    connection.connectTo(state.connectionDesc)
  }

  private def stop() {
    disconnectFromServer()
    state = state.copy(running = false, observers = Set.empty)
    saveSnapshot(state)
  }

  private def disconnectFromServer() {
    connection.disconnect()
  }

  private def registerObserver(observer: Observer) {
    state = state.copy(observers = state.observers + observer)
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

  private def startNotifying() = {
    connection.channelMessages.foreach {
      case ChannelMessage(channel, msg) =>
        notifyObservers(ChannelMessageReceived(channel, msg))
    }
    connection.privateMessages.foreach {
      case PrivateMessage(user, msg) =>
        notifyObservers(PrivateMessageReceived(user, msg))
    }
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
      .filter(_.subjects.contains(msg.getClass))
      .map(_.ref)
      .foreach(_ ! msg)

}

object ChatConnectionObservableProxy {

  abstract class FailureNotification {
    private[ChatConnectionObservableProxy] var cause: Throwable = null

    def getCause = cause
  }

  private[ChatConnectionObservableProxy] case class ListenerState(running: Boolean, connectionDesc: ChatConnectionDesc, connectionFactory: ChatConnectionFactory, channels: Set[String], observers: Set[Observer])

  case class ConnectedToServer(connectionDesc: ChatConnectionDesc)

  case class FailedToConnectToServer(chatConnectionDesc: ChatConnectionDesc) extends FailureNotification

  object Start

  object Stop

  case class Observer(ref: ActorRef, subjects: Class[_]*)

  case class RegisterObserver(observer: Observer)

  case class UnregisterObserver(observer: Observer)

  case class JoinChannel(name: String)

  case class LeaveChannel(name: String)

  case class JoinedChannel(name: String)

  case class FailedToJoinChannel(name: String) extends FailureNotification

  case class LeftChannel(name: String)

  case class FailedToLeaveChannel(name: String) extends FailureNotification

  case class SendChannelMessage(channel: String, msg: String)

  case class SendPrivateMessage(user: String, msg: String)

  case class ChannelMessageReceived(channel: String, msg: String)

  case class PrivateMessageReceived(user: String, msg: String)

}