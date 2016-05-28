package pl.jaca.ircsy.clientnode.connection

import java.security.MessageDigest

import akka.actor._
import akka.cluster.sharding.ShardCoordinator.LeastShardAllocationStrategy
import akka.cluster.sharding.ShardRegion.{EntityId, ShardId}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings, ShardCoordinator, ShardRegion}
import akka.persistence.PersistentActor
import pl.jaca.ircsy.chat.ConnectionDesc
import pl.jaca.ircsy.clientnode.connection.ConnectionProxySupervisor.Initialize
import pl.jaca.ircsy.clientnode.connection.ConnectionProxyRegionCoordinator.{ForwardToProxy, ShardIdLength, StartProxy, StopProxy}
import pl.jaca.ircsy.clientnode.connection.ConnectionObservableProxy.{ConnectionCmd, Start, Stop}
import pl.jaca.ircsy.clientnode.sharding.{RegionAwareClusterSharding, RegionAwareClusterShardingImpl}

/**
  * @author Jaca777
  *         Created 2016-05-01 at 23
  */
class ConnectionProxyRegionCoordinator(sharding: RegionAwareClusterSharding, connectionFactory: ChatConnectionFactory, pubSubMediator: ActorRef) extends Actor with ActorLogging {

  log.info("Starting connection proxy region coordinator...")

  val extractEntityId: ShardRegion.ExtractEntityId = {
    case ForwardToProxy(desc, cmd) =>
      (toListenerId(desc), cmd)
  }

  private def toListenerId(desc: ConnectionDesc): EntityId = desc.toString

  val extractShardId: ShardRegion.ExtractShardId = {
    case ForwardToProxy(desc, cmd) =>
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
      log.debug(s"Starting connection proxy: $desc...")
      listenerRegion ! ForwardToProxy(desc, Initialize(desc, connectionFactory, pubSubMediator))
      listenerRegion ! ForwardToProxy(desc, Start)
    case StopProxy(desc) =>
      log.debug(s"Stopping connection proxy: $desc...")
      listenerRegion ! ForwardToProxy(desc, Stop)
    case msg: ForwardToProxy =>
      listenerRegion ! msg
    case Stop =>
      log.info("Stopping connection proxy region coordinator...")
      listenerRegion ! ShardRegion.GracefulShutdown
      context.stop(self)
  }
}

object ConnectionProxyRegionCoordinator {
  private val ShardIdLength = 3

  case class StartProxy(desc: ConnectionDesc)

  case class StopProxy(desc: ConnectionDesc)

  case class ForwardToProxy(desc: ConnectionDesc, cmd: Any)

  object Stop

}
