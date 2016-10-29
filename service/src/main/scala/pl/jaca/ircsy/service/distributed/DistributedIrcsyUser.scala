package pl.jaca.ircsy.service.distributed

import java.util

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._
import pl.jaca.ircsy.chat.ServerDesc
import pl.jaca.ircsy.chat.messages.{ChannelMessage, Notification, PrivateMessage}
import pl.jaca.ircsy.service.distributed.IrcsyUserConnectionManager.{Channels, GetChannels, GetServers, Servers}
import pl.jaca.ircsy.service.{IrcsyUser, UserMessageRepository, UserMessageRepositoryFactory}
import rx.Observable
import rx.lang.scala.Subject

import scala.concurrent.Await
import scala.language.postfixOps
import scala.collection.JavaConverters._

/**
  * @author Jaca777
  *         Created 2016-05-31 at 23
  */
class DistributedIrcsyUser(name: String, system: ActorSystem, clientNodeProxy: ActorRef, repositoryFactory: UserMessageRepositoryFactory) extends IrcsyUser {

  private val channelMessages = Subject[ChannelMessage]
  private val privateMessages = Subject[PrivateMessage]
  private val notifications = Subject[Notification]

  private val messageRepository = repositoryFactory.newRepository(name)

  private val connectionManager = system.actorOf(Props(new IrcsyUserConnectionManager(name, privateMessages, channelMessages, notifications, clientNodeProxy)))

  override def getName: String = name

  implicit val timeout = Timeout(DistributedIrcsyUser.ManagerTimeout)
  override def getServers: util.Set[ServerDesc] =
    Await.result(connectionManager ? GetServers, DistributedIrcsyUser.ManagerTimeout)
      .asInstanceOf[Servers].servers
      .asJava


  override def getChannels(server: ServerDesc): util.Set[String] =
    Await.result(connectionManager ? GetChannels(server), DistributedIrcsyUser.ManagerTimeout)
      .asInstanceOf[Channels].channels
      .asJava

  override def getPrivateMessages: Observable[PrivateMessage] = privateMessages.asJavaObservable.asInstanceOf[Observable[PrivateMessage]]

  override def getMessages: Observable[ChannelMessage] = channelMessages.asJavaObservable.asInstanceOf[Observable[ChannelMessage]]

  override def getNotifications: Observable[Notification] = notifications.asJavaObservable.asInstanceOf[Observable[Notification]]

  override def joinChannel(server: ServerDesc, channelName: String): Unit =
    connectionManager ! IrcsyUserConnectionManager.JoinChannel(server, channelName)

  override def leaveChannel(server: ServerDesc, channelName: String): Unit =
    connectionManager ! IrcsyUserConnectionManager.LeaveChannel(server, channelName)

  override def sendChannelMessage(server: ServerDesc, channel: String, message: String): Unit =
    connectionManager ! IrcsyUserConnectionManager.SendChannelMessage(server, channel, message)

  override def sendPrivateMessage(server: ServerDesc, destUserName: String, message: String): Unit =
    connectionManager ! IrcsyUserConnectionManager.SendPrivateMessage(server, destUserName, message)

  override def getMessageRepository: UserMessageRepository = messageRepository

}

object DistributedIrcsyUser {
  implicit val ManagerTimeout = 2 seconds
}
