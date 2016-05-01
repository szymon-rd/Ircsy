package pl.jaca.ircsy.clientnode

import akka.actor.Actor
import akka.cluster.Cluster

/**
  * @author Jaca777
  *         Created 2016-04-30 at 17
  */
class ClientNodeReceptionist extends Actor {

  val cluster = Cluster(context.system)

  override def receive: Actor.Receive = {
    case _ =>
  }

}

object ClientNodeReceptionist {

}
