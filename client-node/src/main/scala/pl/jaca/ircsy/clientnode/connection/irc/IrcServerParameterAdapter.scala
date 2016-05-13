package pl.jaca.ircsy.clientnode.connection.irc

import java.util

import com.ircclouds.irc.api.IServerParameters
import com.ircclouds.irc.api.domain.IRCServer
import pl.jaca.ircsy.clientnode.connection.{ServerDesc, ConnectionDesc}
import scala.collection.JavaConverters._

import scala.collection.immutable

/**
  * @author Jaca777
  *         Created 2016-05-12 at 21
  */
class IrcServerParameterAdapter(connection: ConnectionDesc) extends IServerParameters {

  val serverDesc = connection.serverDesc

  override def getNickname: String = connection.username

  override def getServer: IRCServer = new IRCServer(serverDesc.host, serverDesc.port)

  override def getRealname: String = connection.username

  override def getAlternativeNicknames: util.List[String] = List(connection.username).asJava

  override def getIdent: String = connection.username
}
