package pl.jaca.ircsy.clientnode.messagecollection.repository

import pl.jaca.ircsy.clientnode.connection.ServerDesc
import pl.jaca.ircsy.clientnode.connection.messages.{PrivateMessage, ChannelMessage}

/**
  * @author Jaca777
  *         Created 2016-05-03 at 19
  */
trait MessageRepository {
  def addChannelMessage(server: ServerDesc, message: ChannelMessage)
  def addPrivateMessage(server: ServerDesc, message: PrivateMessage)
}
