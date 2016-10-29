package pl.jaca.ircsy.clientnode

import java.net.InetAddress

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorSystem, Props}
import akka.cluster.{Cluster, ClusterEvent}
import pl.jaca.ircsy.util.config.ConfigUtil._

/**
  * @author Jaca777
  *         Created 2016-10-29 at 21
  */
class ClientNodeLauncher extends Actor {

  val cluster = Cluster(context.system)
  val config = context.system.settings.config
  val cassandraContactPoints = config.stringsAt("cassandra-contact-points").get
    .map(InetAddress.getByName)
    .toSet
  val receptionist = context.actorOf(Props(new ClientNodeReceptionist(cassandraContactPoints)))

  override def receive: Receive = {
    case _ =>
  }
}
