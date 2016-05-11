package pl.jaca.ircsy.clientnode.connection

import java.security.MessageDigest

import akka.actor.{PoisonPill, Actor, Props, ActorRef}
import akka.cluster.sharding.ShardCoordinator.LeastShardAllocationStrategy
import akka.cluster.sharding.ShardRegion.{EntityId, ShardId}
import akka.cluster.sharding.{ShardCoordinator, ShardRegion, ClusterSharding, ClusterShardingSettings}
import akka.persistence.PersistentActor
import pl.jaca.ircsy.clientnode.connection.ConnectionProxySupervisor.InitializeConnection
import pl.jaca.ircsy.clientnode.connection.ConnectionProxyRegionCoordinator.{StopProxy, ShardIdLength, ForwardToProxy, StartProxy}
import pl.jaca.ircsy.clientnode.connection.ConnectionObservableProxy.{Stop, Start}
import pl.jaca.ircsy.clientnode.sharding.{RegionAwareClusterSharding, RegionAwareClusterShardingImpl}

/**
  * @author Jaca777
  *         Created 2016-05-01 at 23
  */
class ConnectionProxyRegionCoordinator(sharding: RegionAwareClusterSharding, connectionFactory: ChatConnectionFactory) extends Actor {

  val extractEntityId: ShardRegion.ExtractEntityId = {
    case ForwardToProxy(desc, msg) =>
      (toListenerId(desc), msg)

  }

  private def toListenerId(desc: ConnectionDesc): EntityId = desc.toString

  val extractShardId: ShardRegion.ExtractShardId = {
    case ForwardToProxy(desc, msg) =>
      toShardId(desc)
  }

  private def toShardId(desc: ConnectionDesc): ShardId = {
    val md5Digest: MessageDigest = MessageDigest.getInstance("MD5")
    val listenerId: Array[Byte] = toListenerId(desc).getBytes
    val md5: Array[Byte] = md5Digest.digest(listenerId)
    new String(md5.take(ShardIdLength))
  }

  val listenerRegion = sharding.findOrStartRegion(
    system = context.system,
    typeName = "ConnectionProxy",
    entityProps = Props[ConnectionProxySupervisor],
    entityIdExtractor = extractEntityId,
    shardIdExtractor = extractShardId,
    stopMessage = Stop)

  override def receive: Receive = {
    case StartProxy(desc) =>
      listenerRegion ! ForwardToProxy(desc, InitializeConnection(desc,connectionFactory))
      listenerRegion ! ForwardToProxy(desc, Start)
    case StopProxy(desc) =>
      listenerRegion ! ForwardToProxy(desc, Stop)
    case msg: ForwardToProxy =>
      listenerRegion ! msg
    case Stop =>
      listenerRegion ! ShardRegion.GracefulShutdown
      context.stop(self)
  }
}

object ConnectionProxyRegionCoordinator {
  private val ShardIdLength = 3

  case class StartProxy(desc: ConnectionDesc)

  case class StopProxy(desc: ConnectionDesc)

  case class ForwardToProxy(desc: ConnectionDesc, msg: Any)

  object Stop

}
