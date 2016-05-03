package pl.jaca.ircsy.clientnode.messagescollection

import java.security.MessageDigest

import akka.actor.{Props, ActorRef}
import akka.cluster.sharding.ShardCoordinator.LeastShardAllocationStrategy
import akka.cluster.sharding.{ClusterShardingSettings, ShardRegion}
import akka.cluster.sharding.ShardRegion._
/**
  * @author Jaca777
  *         Created 2016-05-03 at 18
  */
class MessageCollectorSupervisor {
/*  val extractEntityId: ShardRegion.ExtractEntityId = {
    case _ =>

  }

  private def toListenerId(desc: ChatConnectionDesc): EntityId = desc.toString

  val extractShardId: ShardRegion.ExtractShardId = {
    case ForwardToProxy(desc, msg) =>
      toShardId(desc)
  }

  private def toShardId(desc: ChatConnectionDesc): ShardId = {
    val md5Digest: MessageDigest = MessageDigest.getInstance("MD5")
    val listenerId: Array[Byte] = toListenerId(desc).getBytes
    val md5: Array[Byte] = md5Digest.digest(listenerId)
    new String(md5.take(ShardIdLength))
  }

  val listenerRegion: ActorRef = {
    try {
      resolveRegion()
    } catch {
      case _: IllegalArgumentException => startRegion()
    }
  }

  private def resolveRegion() = sharding.shardRegion("Proxy")

  val settings = ClusterShardingSettings(context.system)

  val allocationStrategy = new LeastShardAllocationStrategy(
    settings.tuningParameters.leastShardAllocationRebalanceThreshold,
    settings.tuningParameters.leastShardAllocationMaxSimultaneousRebalance)

  private def startRegion() = sharding.start(
    typeName = "Proxy",
    entityProps = Props[ChatConnectionObservableProxy],
    settings = settings,
    extractEntityId = extractEntityId,
    extractShardId = extractShardId,
    allocationStrategy = allocationStrategy,
    Start)*/
}
object MessageCollectorSupervisor {
  case class ForwardToSupervisor(collectionSubjectDesc: Any)
}