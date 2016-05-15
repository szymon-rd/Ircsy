package pl.jaca.ircsy.clientnode.messagecollection

import java.security.MessageDigest

import akka.actor.{ActorLogging, Actor}
import akka.actor.Actor.Receive
import akka.cluster.sharding.{ClusterSharding, ShardRegion}
import akka.cluster.sharding.ShardRegion.{ShardId, EntityId}
import pl.jaca.ircsy.clientnode.connection.{ServerDesc, ConnectionDesc}
import pl.jaca.ircsy.clientnode.messagecollection.repository.MessageRepositoryFactory
import pl.jaca.ircsy.clientnode.sharding.RegionAwareClusterSharding

/**
  * @author Jaca777
  *         Created 2016-05-01 at 23
  */
class MessageCollectionRegionCoordinator(sharding: RegionAwareClusterSharding, repositoryFactory: MessageRepositoryFactory) extends Actor with ActorLogging {

  log.info("Starting message collection region coordinator")

  override def receive: Receive = {
    case _ =>
  }
}

object MessageCollectionRegionCoordinator {

  case class StartChannelMessageCollector(serverDesc: ServerDesc, channelName: String)

  case class StartPrivateMessageCollector(connectionDesc: ConnectionDesc)

  case class StopChannelMessageCollector(serverDesc: ServerDesc, channelName: String)

  case class StopPrivateMessageCollector(connectionDesc: ConnectionDesc)

  object Stop

}
