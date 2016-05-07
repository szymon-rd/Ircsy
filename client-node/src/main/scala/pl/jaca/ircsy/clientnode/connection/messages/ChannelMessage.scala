package pl.jaca.ircsy.clientnode.connection.messages

import java.time.LocalDate

/**
  * @author Jaca777
  *         Created 2016-05-07 at 21
  */
case class ChannelMessage(channelName: String, author: String, time: LocalDate, message: String) {

}
