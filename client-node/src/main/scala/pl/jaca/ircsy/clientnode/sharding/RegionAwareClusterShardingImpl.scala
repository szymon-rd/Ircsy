package pl.jaca.ircsy.clientnode.sharding

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.cluster.sharding.ShardCoordinator.{LeastShardAllocationStrategy, ShardAllocationStrategy}
import akka.cluster.sharding.ShardRegion.{ExtractEntityId, ExtractShardId}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}

import scala.util.Try

/**
  * @author Jaca777
  *         Created 2016-05-05 at 12
  */
class RegionAwareClusterShardingImpl(sharding: ClusterSharding) extends RegionAwareClusterSharding{
  def findOrStartRegion(system: ActorSystem,
                        typeName: String,
                        entityProps: Props,
                        entityIdExtractor: ExtractEntityId,
                        shardIdExtractor: ExtractShardId,
                        stopMessage: Any
                       ): ActorRef = {
    findRegion(sharding, typeName).recoverWith {
      case _: IllegalArgumentException => Try {
        startRegion(
          system,
          sharding = sharding,
          typeName = typeName,
          entityProps = entityProps,
          settings = ClusterShardingSettings(system),
          entityIdExtractor = entityIdExtractor,
          shardIdExtractor = shardIdExtractor,
          stopMessage = stopMessage
        )
      }
    }.get
  }

  private def findRegion(sharding: ClusterSharding, name: String): Try[ActorRef] = Try(sharding.shardRegion(name))

  private def startRegion(system: ActorSystem,
                          sharding: ClusterSharding,
                          typeName: String,
                          entityProps: Props,
                          settings: ClusterShardingSettings,
                          entityIdExtractor: ExtractEntityId,
                          shardIdExtractor: ExtractShardId,
                          stopMessage: Any
                         ): ActorRef = {
    sharding.start(
      typeName = typeName,
      entityProps = entityProps,
      settings = settings,
      extractEntityId = entityIdExtractor,
      extractShardId = shardIdExtractor,
      allocationStrategy = allocationStrategy(settings),
      stopMessage)
  }

  private def allocationStrategy(settings: ClusterShardingSettings): ShardAllocationStrategy = new LeastShardAllocationStrategy(
    settings.tuningParameters.leastShardAllocationRebalanceThreshold,
    settings.tuningParameters.leastShardAllocationMaxSimultaneousRebalance)


}
