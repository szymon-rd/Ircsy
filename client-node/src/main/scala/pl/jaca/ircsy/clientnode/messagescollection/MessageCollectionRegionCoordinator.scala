package pl.jaca.ircsy.clientnode.messagescollection

import java.security.MessageDigest

import akka.actor.Actor
import akka.actor.Actor.Receive
import akka.cluster.sharding.{ClusterSharding, ShardRegion}
import akka.cluster.sharding.ShardRegion.{ShardId, EntityId}
import pl.jaca.ircsy.clientnode.connection.ConnectionDesc
import pl.jaca.ircsy.clientnode.sharding.RegionAwareClusterSharding

/**
  * @author Jaca777
  *         Created 2016-05-01 at 23
  */
class MessageCollectionRegionCoordinator(sharding: RegionAwareClusterSharding, repositoryFactory: MessageRepositoryFactory) extends Actor {
  override def receive: Receive = {
    case _ =>
  }
}
