package pl.jaca.ircsy.irc.cluster

import akka.actor.ActorRef
import akka.io.Tcp.Message
import rx.subjects.Subject

/**
  * @author Jaca777
  *         Created 2016-04-30 at 18
  */
class UserMessageDispatcher(toObserve: ActorRef) {
  val messageSubject = Subject[Message]

}
object UserMessageDispatcher {
  case class ChannelMessage()
}
