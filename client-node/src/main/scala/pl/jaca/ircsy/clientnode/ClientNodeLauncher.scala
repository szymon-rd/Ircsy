package pl.jaca.ircsy.clientnode

import java.net.{InetAddress, InetSocketAddress}

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
  val localClusterAddress = cluster.selfAddress
  println(localClusterAddress)
  cluster.join(localClusterAddress)
  val config = context.system.settings.config
  val cassandraContactPoints = config.stringsAt("app.cassandra-contact-points").get
    .map(toSocketAddress)
    .toSet
  val receptionist = context.actorOf(Props(new ClientNodeReceptionist(cassandraContactPoints)))

  override def receive: Receive = {
    case _ =>
  }

  private def toSocketAddress(address: String): InetSocketAddress = {
    val parts = address.split(':')
    new InetSocketAddress(parts(0), parts(1).toInt)
  }
}
