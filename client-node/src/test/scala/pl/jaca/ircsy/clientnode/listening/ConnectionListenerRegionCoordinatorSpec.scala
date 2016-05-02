package pl.jaca.ircsy.clientnode.listening

import akka.actor.{ActorSystem, Props}
import akka.cluster.sharding.{ShardRegion, ClusterShardingSettings, ClusterSharding}
import akka.testkit.{TestProbe, TestActorRef, TestKit}

import org.mockito.Matchers
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito._
import org.scalatest.WordSpecLike
import pl.jaca.ircsy.clientnode.listening.ConnectionListenerRegionCoordinator.{ForwardToListener, StartListener}
import pl.jaca.ircsy.util.test.MoreMockitoSugar

/**
  * @author Jaca777
  *         Created 2016-05-02 at 00
  */
class ConnectionListenerRegionCoordinatorSpec extends TestKit(ActorSystem("ConnectionListenerRegionCoordinatorSpec")) with WordSpecLike with MockitoSugar with MoreMockitoSugar {
  "ConnectionListenerRegionCoordinator" should {

    "resolve region if exists" in {
      val sharding = mock[ClusterSharding]
      TestActorRef(new ConnectionListenerRegionCoordinator(sharding))

      verify(sharding).shardRegion(equal("Listener"))
    }

    "start sharding region if doesn't exist " in {
      val sharding = mock[ClusterSharding]
      when(sharding.shardRegion(any[String])).thenThrow(new IllegalArgumentException)

      TestActorRef(new ConnectionListenerRegionCoordinator(sharding))

      verify(sharding).start(
        equal("Listener"),
        equal(Props[ServerConnectionListener]),
        any[ClusterShardingSettings],
        any[ShardRegion.ExtractEntityId],
        any[ShardRegion.ExtractShardId])
    }

    "start new listeners" in {
      val shardingProbe = TestProbe()
      val sharding = mock[ClusterSharding]
      when(sharding.shardRegion("Listener")).thenReturn(shardingProbe.ref)

      val coordinator = TestActorRef(new ConnectionListenerRegionCoordinator(sharding))
      coordinator ! StartListener("foo", "bar")

      shardingProbe.expectMsg(StartListener("foo", "bar"))
    }

    "forward message to listener" in {
      val shardingProbe = TestProbe()
      val sharding = mock[ClusterSharding]
      when(sharding.shardRegion("Listener")).thenReturn(shardingProbe.ref)

      val coordinator = TestActorRef(new ConnectionListenerRegionCoordinator(sharding))
      coordinator ! ForwardToListener("foo", "bar", 2)

      shardingProbe.expectMsg(ForwardToListener("foo", "bar", 2))
    }
  }
}
