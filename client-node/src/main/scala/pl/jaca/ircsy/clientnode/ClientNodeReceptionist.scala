package pl.jaca.ircsy.clientnode

import akka.actor.{Props, Actor}
import akka.cluster.Cluster
import akka.cluster.sharding.ClusterSharding
import pl.jaca.ircsy.clientnode.connection.irc.IrcConnectionFactory
import pl.jaca.ircsy.clientnode.connection.{ConnectionProxyRegionCoordinator, ChatConnectionObservableProxy}
import pl.jaca.ircsy.clientnode.messagescollection.MessageCollectionRegionCoordinator

/**
  * @author Jaca777
  *         Created 2016-04-30 at 17
  */
class ClientNodeReceptionist extends Actor {

  val sharding = ClusterSharding(context.system)

  val proxyCoordinator = context.actorOf(Props(new ConnectionProxyRegionCoordinator(sharding, new IrcConnectionFactory)))

  val messageCollectionCoordinator = context.actorOf(Props(new MessageCollectionRegionCoordinator))

  override def receive: Actor.Receive = {
    case _ =>
  }

}

object ClientNodeReceptionist {

}
