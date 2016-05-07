package pl.jaca.ircsy.clientnode.sharding

import akka.actor.{ActorRef, Props, ActorSystem}
import akka.cluster.sharding.ClusterSharding
import akka.cluster.sharding.ShardRegion._

/**
  * @author Jaca777
  *         Created 2016-05-05 at 17
  */
trait RegionAwareClusterSharding {
  def findOrStartRegion(system: ActorSystem,
                        typeName: String,
                        entityProps: Props,
                        entityIdExtractor: ExtractEntityId,
                        shardIdExtractor: ExtractShardId,
                        stopMessage: Any
                       ): ActorRef
}
