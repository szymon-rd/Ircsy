package pl.jaca.ircsy.clientnode.connection

import java.security.MessageDigest

import akka.actor.{Actor, Props, ActorRef}
import akka.cluster.sharding.ShardCoordinator.LeastShardAllocationStrategy
import akka.cluster.sharding.ShardRegion.{EntityId, ShardId}
import akka.cluster.sharding.{ShardCoordinator, ShardRegion, ClusterSharding, ClusterShardingSettings}
import akka.persistence.PersistentActor
import pl.jaca.ircsy.clientnode.connection.ChatConnectionObservableProxySupervisor.Initialize
import pl.jaca.ircsy.clientnode.connection.ConnectionProxyRegionCoordinator.{StopProxy, ShardIdLength, ForwardToProxy, StartProxy}
import pl.jaca.ircsy.clientnode.connection.ChatConnectionObservableProxy.{Stop, Start}

/**
  * @author Jaca777
  *         Created 2016-05-01 at 23
  */
class ConnectionProxyRegionCoordinator(sharding: ClusterSharding, connectionFactory: ChatConnectionFactory) extends Actor {

  val extractEntityId: ShardRegion.ExtractEntityId = {
    case ForwardToProxy(desc, msg) =>
      (toListenerId(desc), msg)

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
    entityProps = Props[ChatConnectionObservableProxySupervisor],
    settings = settings,
    extractEntityId = extractEntityId,
    extractShardId = extractShardId,
    allocationStrategy = allocationStrategy,
    Start)

  override def receive: Receive = {
    case StartProxy(desc) =>
      listenerRegion ! ForwardToProxy(desc, Initialize(desc,connectionFactory))
      listenerRegion ! ForwardToProxy(desc, Start)
    case StopProxy(desc) =>
      listenerRegion ! ForwardToProxy(desc, Stop)
    case msg: ForwardToProxy =>
      listenerRegion ! msg
  }
}

object ConnectionProxyRegionCoordinator {
  private val ShardIdLength = 3

  case class StartProxy(desc: ChatConnectionDesc)

  case class StopProxy(desc: ChatConnectionDesc)

  case class ForwardToProxy(desc: ChatConnectionDesc, msg: Any)

}
