package pl.jaca.ircsy.clientnode.connection.messages

import java.time.LocalDate

/**
  * @author Jaca777
  *         Created 2016-05-07 at 21
  */
case class PrivateMessage(firstParticipant: String, secondParticipant: String, sender: String, time: LocalDate, message: String) {

}
