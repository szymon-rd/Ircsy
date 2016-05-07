package pl.jaca.ircsy.clientnode

import akka.actor.{Actor, Props}
import akka.cluster.sharding.ClusterSharding
import pl.jaca.ircsy.clientnode.connection.ConnectionProxyRegionCoordinator
import pl.jaca.ircsy.clientnode.connection.irc.IrcConnectionFactory
import pl.jaca.ircsy.clientnode.messagescollection.MessageCollectionRegionCoordinator
import pl.jaca.ircsy.clientnode.sharding.RegionAwareClusterShardingImpl

/**
  * @author Jaca777
  *         Created 2016-04-30 at 17
  */
class ClientNodeReceptionist extends Actor {

  val sharding = ClusterSharding(context.system)

  val proxyCoordinator = context.actorOf(Props(new ConnectionProxyRegionCoordinator(new RegionAwareClusterShardingImpl(sharding), new IrcConnectionFactory)))

  val messageCollectionCoordinator = context.actorOf(Props(new MessageCollectionRegionCoordinator(new RegionAwareClusterShardingImpl(sharding), null)))

  override def receive: Actor.Receive = {
    case _ =>
  }

}

object ClientNodeReceptionist {

}
