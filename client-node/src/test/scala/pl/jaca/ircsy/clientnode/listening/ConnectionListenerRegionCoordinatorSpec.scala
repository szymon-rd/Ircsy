package pl.jaca.ircsy.clientnode.listening

import akka.actor.{ActorSystem, Props}
import akka.cluster.sharding.{ShardRegion, ClusterShardingSettings, ClusterSharding}
import akka.testkit.{TestActorRef, TestKit}

import org.mockito.Matchers
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito._
import org.scalatest.WordSpecLike
import pl.jaca.ircsy.util.test.MoreMockitoSugar

/**
  * @author Jaca777
  *         Created 2016-05-02 at 00
  */
class ConnectionListenerRegionCoordinatorSpec extends TestKit(ActorSystem("ConnectionListenerRegionCoordinatorSpec")) with WordSpecLike with MockitoSugar with MoreMockitoSugar {
  "ConnectionListenerRegionCoordinator" should {
    "start sharding region" in {
      val sharding = mock[ClusterSharding]
      TestActorRef(new ConnectionListenerRegionCoordinator(sharding))

      verify(sharding).start(
        equal("Listener"),
        equal(Props[ServerConnectionListener]),
        any[ClusterShardingSettings],
        any[ShardRegion.ExtractEntityId],
        any[ShardRegion.ExtractShardId])
    }
  }
}
