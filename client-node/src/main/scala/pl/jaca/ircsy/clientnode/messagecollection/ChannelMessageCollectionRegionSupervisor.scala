package pl.jaca.ircsy.clientnode.messagecollection

import java.security.MessageDigest

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.cluster.sharding.ShardRegion
import akka.cluster.sharding.ShardRegion._
import pl.jaca.ircsy.chat.{ConnectionDesc, ServerDesc}
import pl.jaca.ircsy.clientnode.messagecollection.ChannelMessageCollectionRegionSupervisor._
import pl.jaca.ircsy.clientnode.messagecollection.ChannelMessageCollectorSupervisor.Initialize

import pl.jaca.ircsy.clientnode.messagecollection.repository.MessageRepositoryFactory
import pl.jaca.ircsy.clientnode.sharding.RegionAwareClusterSharding

/**
  * @author Jaca777
  *         Created 2016-05-01 at 23
  */
class ChannelMessageCollectionRegionSupervisor(sharding: RegionAwareClusterSharding,
                                               repositoryFactory: MessageRepositoryFactory,
                                               pubSubMediator: ActorRef)
  extends Actor with ActorLogging {

  log.info("Starting channel message collection region coordinator")

  val extractEntityId: ShardRegion.ExtractEntityId = {
    case ForwardToChannelCollector(server, channel, cmd) =>
      (toCollectorId(server, channel), cmd)
  }

  private def toCollectorId(server: ServerDesc, channel: String): EntityId = s"$server/$channel"

  val extractShardId: ShardRegion.ExtractShardId = {
    case ForwardToChannelCollector(server, channel, cmd) =>
      toShardId(server, channel)
  }

  private def toShardId(server: ServerDesc, channel: String): ShardId = {
    val md5Digest: MessageDigest = MessageDigest.getInstance("MD5")
    val listenerId: Array[Byte] = toCollectorId(server, channel).getBytes
    val md5: Array[Byte] = md5Digest.digest(listenerId)
    new String(md5.take(ShardIdLength))
  }

  val listenerRegion = sharding.findOrStartRegion(
    system = context.system,
    typeName = "ChannelMessageCollector",
    entityProps = Props[ChannelMessageCollectorSupervisor],
    entityIdExtractor = extractEntityId,
    shardIdExtractor = extractShardId,
    stopMessage = Stop)

  override def receive: Receive = {
    case StartChannelMessageCollector(server, channel) =>
      listenerRegion ! ForwardToChannelCollector(server, channel, Initialize(server, channel, pubSubMediator, repositoryFactory))
    case StopChannelMessageCollector(server, channel) =>
      listenerRegion ! ForwardToChannelCollector(server, channel, ChannelMessageCollector.Stop)
  }
}

object ChannelMessageCollectionRegionSupervisor {

  private val ShardIdLength = 3

  case class StartChannelMessageCollector(server: ServerDesc, channelName: String)

  case class StopChannelMessageCollector(server: ServerDesc, channelName: String)

  private[messagecollection] case class ForwardToChannelCollector(server: ServerDesc, channelName: String, cmd: Any)

  object Stop

}
