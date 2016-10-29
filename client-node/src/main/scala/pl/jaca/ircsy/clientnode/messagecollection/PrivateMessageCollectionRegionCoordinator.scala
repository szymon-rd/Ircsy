package pl.jaca.ircsy.clientnode.messagecollection

import java.security.MessageDigest

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.cluster.sharding.ShardRegion
import akka.cluster.sharding.ShardRegion._
import pl.jaca.ircsy.chat.{ConnectionDesc, ServerDesc}
import pl.jaca.ircsy.clientnode.messagecollection.PrivateMessageCollectionRegionCoordinator._
import pl.jaca.ircsy.clientnode.messagecollection.PrivateMessageCollectorSupervisor.Initialize
import pl.jaca.ircsy.clientnode.messagecollection.repository.MessageRepositoryFactory
import pl.jaca.ircsy.clientnode.sharding.RegionAwareClusterSharding

/**
  * @author Jaca777
  *         Created 2016-05-01 at 23
  */
class PrivateMessageCollectionRegionCoordinator(sharding: RegionAwareClusterSharding,
                                                repositoryFactory: MessageRepositoryFactory,
                                                pubSubMediator: ActorRef)
  extends Actor with ActorLogging {

  log.info("Starting message collection region coordinator")

  val extractEntityId: ShardRegion.ExtractEntityId = {
    case ForwardToPrivateCollector(desc, cmd) =>
      (toCollectorId(desc), cmd)
  }

  private def toCollectorId(desc: ConnectionDesc): EntityId = desc.toString

  val extractShardId: ShardRegion.ExtractShardId = {
    case ForwardToPrivateCollector(desc, cmd) =>
      toShardId(desc)
  }

  private def toShardId(desc: ConnectionDesc): ShardId = {
    val md5Digest: MessageDigest = MessageDigest.getInstance("MD5")
    val listenerId: Array[Byte] = toCollectorId(desc).getBytes
    val md5: Array[Byte] = md5Digest.digest(listenerId)
    new String(md5.take(ShardIdLength))
  }

  val listenerRegion = sharding.findOrStartRegion(
    system = context.system,
    typeName = "ConnectionProxy",
    entityProps = Props[PrivateMessageCollectorSupervisor],
    entityIdExtractor = extractEntityId,
    shardIdExtractor = extractShardId,
    stopMessage = Stop)

  override def receive: Receive = {
    case StartPrivateMessageCollector(connection) =>
      listenerRegion ! ForwardToPrivateCollector(connection, Initialize(connection, pubSubMediator, repositoryFactory))
    case StopPrivateMessageCollector(connection) =>
      listenerRegion ! ForwardToPrivateCollector(connection, PrivateMessageCollector.Stop)
  }
}

object PrivateMessageCollectionRegionCoordinator {

  private val ShardIdLength = 3

  case class StartPrivateMessageCollector(connectionDesc: ConnectionDesc)

  case class StopPrivateMessageCollector(connectionDesc: ConnectionDesc)

  private[messagecollection] case class ForwardToPrivateCollector(desc: ConnectionDesc, cmd: Any)

  object Stop

}
