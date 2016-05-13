package pl.jaca.ircsy.clientnode.connection.irc

import pl.jaca.ircsy.clientnode.connection.{ChatConnection, ChatConnectionFactory}

import scala.concurrent.ExecutionContext

/**
  * @author Jaca777
  *         Created 2016-05-02 at 13
  */
class IrcConnectionFactory extends ChatConnectionFactory {
  override def newConnection(executionContext: ExecutionContext): ChatConnection = new IrcConnection(executionContext)
}
