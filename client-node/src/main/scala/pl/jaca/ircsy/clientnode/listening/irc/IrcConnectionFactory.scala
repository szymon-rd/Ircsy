package pl.jaca.ircsy.clientnode.listening.irc

import pl.jaca.ircsy.clientnode.listening.{ChatConnection, ChatConnectionFactory}

/**
  * @author Jaca777
  *         Created 2016-05-02 at 13
  */
class IrcConnectionFactory extends ChatConnectionFactory{
  override def newConnection(): ChatConnection = new IrcConnection
}
