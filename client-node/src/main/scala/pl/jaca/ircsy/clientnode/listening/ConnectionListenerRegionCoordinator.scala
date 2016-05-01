package pl.jaca.ircsy.clientnode.listening

import java.security.MessageDigest

import akka.actor.{Actor, Props, ActorRef}
import akka.cluster.sharding.ShardRegion.{EntityId, ShardId}
import akka.cluster.sharding.{ShardRegion, ClusterSharding, ClusterShardingSettings}
import pl.jaca.ircsy.clientnode.listening.ConnectionListenerRegionCoordinator.{ShardIdLength, ForwardToListener, StartListener}
import pl.jaca.ircsy.clientnode.listening.ServerConnectionListener.Start

/**
  * @author Jaca777
  *         Created 2016-05-01 at 23
  */
class ConnectionListenerRegionCoordinator(sharding: ClusterSharding) extends Actor{

  val extractEntityId: ShardRegion.ExtractEntityId = {
    case StartListener(serverName, userName) =>
      (toListenerId(serverName, userName), Start)
    case ForwardToListener(serverName, userName, msg) =>
      (toListenerId(serverName, userName), msg)

  }

  private def toListenerId(serverName: String, userName: String): EntityId =
    serverName + "@" + userName

  val extractShardId: ShardRegion.ExtractShardId = {
    case StartListener(serverName, userName) =>
      toShardId(serverName, userName)
    case ForwardToListener(serverName, userName, msg) =>
      toShardId(serverName, userName)
  }

  private def toShardId(serverName: String, userName: String): ShardId = {
    val md5Digest: MessageDigest = MessageDigest.getInstance("MD5")
    val listenerId: Array[Byte] = toListenerId(serverName, userName).getBytes
    val md5: Array[Byte] = md5Digest.digest(listenerId)
    new String(md5.take(ShardIdLength))
  }

  val listenerRegion: ActorRef = sharding.start(
    typeName = "Listener",
    entityProps = Props[ServerConnectionListener],
    settings = ClusterShardingSettings(context.system),
    extractEntityId = extractEntityId,
    extractShardId = extractShardId)

  override def receive: Receive = {
    case msg: StartListener => listenerRegion ! msg
    case msg: ForwardToListener => listenerRegion ! msg
  }

}

object ConnectionListenerRegionCoordinator {
  private val ShardIdLength = 3

  case class StartListener(serverName: String, userName: String)
  case class ForwardToListener(serverName: String, userName: String, msg: Any)
}
