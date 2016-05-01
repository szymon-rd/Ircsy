package pl.jaca.ircsy.clientnode.listening

import akka.actor.Actor
import pl.jaca.ircsy.clientnode.listening.ServerConnectionListener.Start

/**
  * @author Jaca777
  *         Created 2016-05-01 at 17
  */
class ServerConnectionListener extends Actor{
  override def receive: Receive = {
    case Start =>
  }
}
object ServerConnectionListener {
  object Start
}