package pl.jaca.ircsy.clientnode.connection.irc

import java.util.Observable

import pl.jaca.ircsy.clientnode.connection.messages.{Notification, PrivateMessage, ChannelMessage}
import pl.jaca.ircsy.clientnode.connection.{ConnectionDesc, ChatConnection}
import rx.lang.scala

/**
  * @author Jaca777
  *         Created 2016-05-02 at 13
  */
class IrcConnection extends ChatConnection{
  override def connectTo(connectionDesc: ConnectionDesc): Unit = ???

  override def disconnect(): Unit = ???

  override def sendPrivateMessage(user: String, msg: String): Unit = ???

  override def channelMessages: scala.Observable[ChannelMessage] = ???

  override def joinChannel(name: String): Unit = ???

  override def privateMessages: scala.Observable[PrivateMessage] = ???

  override def notifications: scala.Observable[Notification] = ???

  override def sendChannelMessage(channel: String, msg: String): Unit = ???

  override def leaveChannel(name: String): Unit = ???
}
