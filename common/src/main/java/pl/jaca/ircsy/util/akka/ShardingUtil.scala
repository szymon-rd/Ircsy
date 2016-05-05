package pl.jaca.ircsy.util.akka

import akka.actor.{ActorSystem, Props, ActorRef}
import akka.cluster.sharding.ShardRegion.{ExtractShardId, ExtractEntityId}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import akka.cluster.sharding.ShardCoordinator.{ShardAllocationStrategy, LeastShardAllocationStrategy}

import scala.util.Try

/**
  * @author Jaca777
  *         Created 2016-05-05 at 12
  */
object ShardingUtil {
  def findOrStartRegion(system: ActorSystem,
                        sharding: ClusterSharding,
                        typeName: String,
                        entityProps: Props,
                        entityIdExtractor: ExtractEntityId,
                        shardIdExtractor: ExtractShardId,
                        stopMessage: Any
                       ): ActorRef = {
    findRegion(sharding, typeName).recoverWith {
      case IllegalArgumentException => Try {
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
