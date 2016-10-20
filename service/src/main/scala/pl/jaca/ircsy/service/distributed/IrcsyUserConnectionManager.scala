package pl.jaca.ircsy.service.distributed

import java.time.LocalDate

import akka.actor.{Actor, ActorRef}
import akka.actor.Actor.Receive
import akka.contrib.pattern.ReliableProxy.Message
import pl.jaca.ircsy.chat.{ConnectionDesc, ServerDesc}
import pl.jaca.ircsy.chat.messages.{ChannelMessage, Notification, PrivateMessage}
import pl.jaca.ircsy.clientnode.ClientNodeReceptionist.{ObserveUser, RunCommand, RunConnectionCommand}
import pl.jaca.ircsy.clientnode.connection.ConnectionObservableProxy
import pl.jaca.ircsy.clientnode.connection.ConnectionObservableProxy.{ConnectedToServer, FailedToConnectToServer}
import pl.jaca.ircsy.clientnode.connection.ConnectionProxyRegionCoordinator.{ForwardToProxy, StartProxy}
import pl.jaca.ircsy.service.distributed.IrcsyUserConnectionManager._
import pl.jaca.ircsy.service.notifications.{ConnectedToServerNotification, FailedToConnectToServerNotification}
import rx.lang.scala.Subject

import scala.util.Try

/**
  * @author Jaca777
  *         Created 2016-06-05 at 13
  */
class IrcsyUserConnectionManager(nickname: String,
                                 privateMessages: Subject[PrivateMessage],
                                 channelMessages: Subject[ChannelMessage],
                                 notifications: Subject[Notification],
                                 clientNodeProxy: ActorRef) extends Actor {

  private var activeServers: Set[ServerDesc] = Set.empty

  override def receive: Receive = {
    case JoinChannel(server, channel) =>
      if (!activeServers.contains(server))
        connectToServer(server)
      joinChannel(server, channel)

    case LeaveChannel(server, channel) =>
      if (!activeServers.contains(server)) connectToServer(server)
      joinChannel(server, channel)

    case SendPrivateMessage(server, user, message) =>

    case SendChannelMessage(server, channel, message) =>

    case ConnectedToServer(connectionDesc) =>
      activeServers += connectionDesc.getServer
      notifications.onNext(new ConnectedToServerNotification(connectionDesc.getServer, connectionDesc.getNickname, LocalDate.now()))

    case failure @ FailedToConnectToServer(connectionDesc) =>
      notifications.onNext(new FailedToConnectToServerNotification(connectionDesc.getServer, connectionDesc.getNickname, failure.getCause, LocalDate.now()))
  }

  def connectToServer(serverDesc: ServerDesc) {
    val connection: ConnectionDesc = new ConnectionDesc(serverDesc, nickname)
    clientNodeProxy ! RunCommand(StartProxy(connection))
    clientNodeProxy ! ObserveUser(connection, self)
  }

  def joinChannel(server: ServerDesc, channelName: String) =
    clientNodeProxy ! RunConnectionCommand(new ConnectionDesc(server, nickname), ConnectionObservableProxy.JoinChannel(channelName))

}

object IrcsyUserConnectionManager {

  case class JoinChannel(server: ServerDesc, channelName: String)

  case class LeaveChannel(server: ServerDesc, channelName: String)

  case class SendPrivateMessage(server: ServerDesc, destUser: String, message: String)

  case class SendChannelMessage(serverDesc: ServerDesc, channelName: String, message: String)

}