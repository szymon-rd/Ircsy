package pl.jaca.ircsy.clientnode.listening.irc

import java.util.Observable

import pl.jaca.ircsy.clientnode.listening.ChatConnection
import pl.jaca.ircsy.clientnode.listening.ChatConnection.{ChannelMessage, PrivateMessage}

/**
  * @author Jaca777
  *         Created 2016-05-02 at 13
  */
class IrcConnection extends ChatConnection{
  override def sendChannelMessage(channel: String, msg: String): Unit = ???

  override def sendPrivateMessage(user: String, msg: String): Unit = ???

  override def channelMessages: Observable[ChannelMessage] = ???

  override def privateMessages: Observable[PrivateMessage] = ???
}
