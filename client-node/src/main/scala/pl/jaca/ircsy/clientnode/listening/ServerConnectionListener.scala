package pl.jaca.ircsy.clientnode.listening

import akka.actor.ActorRef
import akka.persistence.{PersistentActor, SnapshotOffer}
import pl.jaca.ircsy.clientnode.listening.ServerConnectionListener._

import scala.util.Try

/**
  * @author Jaca777
  *         Created 2016-05-01 at 17
  */
class ServerConnectionListener(connectionFactory: ChatConnectionFactory) extends PersistentActor {

  var state: ListenerState = ListenerState(false, Set.empty, Set.empty)

  override def persistenceId: String = "Listener-" + self.path.name

  override def receiveRecover: Receive = {
    case SnapshotOffer(_, offeredState: ListenerState) =>
      setState(offeredState)
    case Start => updateState(Start)
    case Stop => updateState(Stop)
  }

  private def setState(state: ListenerState) {
    if (state.running) startListener()
    state.channels.foreach(joinChannel)
    this.state = state
  }

  override def receiveCommand: Receive = {
    case Start =>
      persist(Start)(updateState)
    case Stop =>
      persist(Stop)(updateState)
    case cmd: RegisterObserver =>
      persist(cmd)(updateState)
    case cmd: UnregisterObserver =>
      persist(cmd)(updateState)
    case cmd: JoinChannel =>
      persist(cmd)(updateState)
    case cmd: LeaveChannel =>
      persist(cmd)(updateState)
  }

  private def updateState(msg: Any) = msg match {
    case Start =>
      startListener()
      state = state.copy(running = true)
      saveSnapshot(state)

    case Stop =>
      stopListener()
      state = ListenerState(false, Set.empty, Set.empty)
      saveSnapshot(state)

    case RegisterObserver(ref) =>
      state = state.copy(observers = state.observers + ref)

    case UnregisterObserver(ref) =>
      state = state.copy(observers = state.observers - ref)

    case JoinChannel(name) =>
      joinChannel(name).foreach {
        _ => state = state.copy(channels = state.channels + name)
      }

    case LeaveChannel(name) =>
      leaveChannel(name).foreach {
        _ => state = state.copy(channels = state.channels - name)
      }
  }

  private def startListener() {
    //Start listener
  }

  private def stopListener() {
    //Stop listener
  }


  private def joinChannel(name: String): Try[Unit] = {
    val result = Try {
      // Join channel
    }
    notifyResult(result, JoinedChannel(name), FailedToJoinChannel(name))
    result
  }

  private def leaveChannel(name: String): Try[Unit] = {
    val result = Try {
      // Leave channel
    }
    notifyResult(result, LeftChannel(name), FailedToLeaveChannel(name))
    result
  }

  def notifyResult(result: Try[_], successMsg: Any, failureMsg: Any) {
    if(result.isSuccess) notifyObservers(successMsg)
    else notifyObservers(failureMsg)
  }

  private def notifyObservers(msg: Any) =
    state.observers
      .filter(_.subjects.contains(msg.getClass))
      .map(_.ref)
      .foreach(_ ! msg)

}

object ServerConnectionListener {

  private case class ListenerState(running: Boolean, channels: Set[String], observers: Set[Observer])

  object Start

  object Stop

  case class Observer(ref: ActorRef, subjects: Class*)

  case class RegisterObserver(observer: Observer)

  case class UnregisterObserver(observer: Observer)

  case class JoinChannel(name: String)

  case class LeaveChannel(name: String)

  case class JoinedChannel(name: String)

  case class FailedToJoinChannel(name: String)

  case class LeftChannel(name: String)

  case class FailedToLeaveChannel(name: String)

}