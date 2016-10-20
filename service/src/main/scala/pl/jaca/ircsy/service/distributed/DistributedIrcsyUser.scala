package pl.jaca.ircsy.service.distributed

import akka.actor.{ActorRef, ActorSystem, Props}
import pl.jaca.ircsy.chat.{ConnectionDesc, ServerDesc}
import pl.jaca.ircsy.chat.messages.{ChannelMessage, Notification, PrivateMessage}
import pl.jaca.ircsy.clientnode.ClientNodeReceptionist.RunConnectionCommand
import pl.jaca.ircsy.clientnode.connection.ConnectionObservableProxy.{JoinChannel, SendChannelMessage, SendPrivateMessage}
import pl.jaca.ircsy.clientnode.connection.ConnectionProxyRegionCoordinator.ForwardToProxy
import pl.jaca.ircsy.service.distributed.ClientNodeProxy.ForwardToClientNode
import pl.jaca.ircsy.service.{IrcsyUser, UserMessageRepository, UserMessageRepositoryFactory}
import rx.Observable
import rx.lang.scala.Subject

/**
  * @author Jaca777
  *         Created 2016-05-31 at 23
  */
class DistributedIrcsyUser(name: String, system: ActorSystem, clientNodeProxy: ActorRef, repositoryFactory: UserMessageRepositoryFactory) extends IrcsyUser {

  private val channelMessages = Subject[ChannelMessage]
  private val privateMessages = Subject[PrivateMessage]
  private val notifications = Subject[Notification]

  private val messageRepository = repositoryFactory.newRepository()

  private val connectionManager =  system.actorOf(Props(new IrcsyUserConnectionManager(name, privateMessages, channelMessages, notifications, clientNodeProxy)))

  override def getName: String = name

  override def getMessages: Observable[ChannelMessage] = channelMessages.asJavaObservable.asInstanceOf[Observable[ChannelMessage]]

  override def getNotifications: Observable[Notification] = notifications.asJavaObservable.asInstanceOf[Observable[Notification]]

  override def joinChannel(server: ServerDesc, channelName: String): Unit =
    clientNodeProxy ! ForwardToClientNode(RunConnectionCommand(new ConnectionDesc(server, name), JoinChannel(channelName)))

  override def sendChannelMessage(server: ServerDesc, channel: String, message: String): Unit =
    clientNodeProxy ! ForwardToClientNode(RunConnectionCommand(new ConnectionDesc(server, name), SendChannelMessage(channel, message)))

  override def sendPrivateMessage(server: ServerDesc, destUserName: String, message: String): Unit =
    clientNodeProxy ! ForwardToClientNode(RunConnectionCommand(new ConnectionDesc(server, name), SendPrivateMessage(destUserName, message)))

  override def getMessageRepository: UserMessageRepository = messageRepository

  override def getPrivateMessages: Observable[PrivateMessage] = privateMessages.asJavaObservable.asInstanceOf[Observable[PrivateMessage]]
}
