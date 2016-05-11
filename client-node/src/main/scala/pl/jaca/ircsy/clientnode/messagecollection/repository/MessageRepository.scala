package pl.jaca.ircsy.clientnode.messagecollection.repository

import pl.jaca.ircsy.clientnode.connection.{ConnectionDesc, ServerDesc}
import pl.jaca.ircsy.clientnode.connection.messages.{PrivateMessage, ChannelMessage}

import scala.util.Try

/**
  * @author Jaca777
  *         Created 2016-05-03 at 19
  */
trait MessageRepository {
  def addChannelMessage(server: ServerDesc, message: ChannelMessage): Try[Unit]
  def addPrivateMessage(server: ServerDesc, message: PrivateMessage): Try[Unit]
  def close()
}
