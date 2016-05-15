package pl.jaca.ircsy.clientnode.messagecollection

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

}
object MessageCollectorSupervisor {
  object Stop
}