package pl.jaca.ircsy.clientnode.connection.irc

import java.util

import com.ircclouds.irc.api.IServerParameters
import com.ircclouds.irc.api.domain.IRCServer
import pl.jaca.ircsy.chat.ConnectionDesc

import scala.collection.JavaConverters._

/**
  * @author Jaca777
  *         Created 2016-05-12 at 21
  */
class IrcServerParameterAdapter(connection: ConnectionDesc) extends IServerParameters {

  val serverDesc = connection.getServer

  override def getNickname: String = connection.getNickname

  override def getServer: IRCServer = new IRCServer(serverDesc.getHost, serverDesc.getPort)

  override def getRealname: String = connection.getNickname

  override def getAlternativeNicknames: util.List[String] = List(connection.getNickname).asJava

  override def getIdent: String = connection.getNickname
}
