package pl.jaca.ircsy.clientnode.listening

import akka.actor.{ActorSystem, Props}
import akka.cluster.sharding.ShardCoordinator.ShardAllocationStrategy
import akka.cluster.sharding.{ShardRegion, ClusterShardingSettings, ClusterSharding}
import akka.testkit.{TestProbe, TestActorRef, TestKit}

import org.mockito.Matchers
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito._
import org.scalatest.WordSpecLike
import pl.jaca.ircsy.clientnode.listening.ChatConnectionObservableProxy.{Start, Stop, Initialize}
import pl.jaca.ircsy.clientnode.listening.ConnectionProxyRegionCoordinator.{ForwardToListener, StartListener}
import pl.jaca.ircsy.util.test.MoreMockitoSugar

/**
  * @author Jaca777
  *         Created 2016-05-02 at 00
  */
class ConnectionListenerRegionCoordinatorSpec extends TestKit(ActorSystem("ConnectionListenerRegionCoordinatorSpec")) with WordSpecLike with MockitoSugar with MoreMockitoSugar {
  "ConnectionListenerRegionCoordinator" should {

    "resolve region if exists" in {
      val sharding = mock[ClusterSharding]
      TestActorRef(new ConnectionProxyRegionCoordinator(sharding, null))

      verify(sharding).shardRegion(equal("Proxy"))
    }

    "start sharding region if doesn't exist " in {
      val sharding = mock[ClusterSharding]
      when(sharding.shardRegion(any[String])).thenThrow(new IllegalArgumentException)

      TestActorRef(new ConnectionProxyRegionCoordinator(sharding, null))

      verify(sharding).start(
        equal("Proxy"),
        equal(Props[ChatConnectionObservableProxy]),
        any[ClusterShardingSettings],
        any[ShardRegion.ExtractEntityId],
        any[ShardRegion.ExtractShardId],
        any[ShardAllocationStrategy],
        equal(Start))
    }

    "start new listeners" in {
      val shardingProbe = TestProbe()
      val sharding = mock[ClusterSharding]
      when(sharding.shardRegion("Proxy")).thenReturn(shardingProbe.ref)

      val factory = mock[ChatConnectionFactory]
      val coordinator = TestActorRef(new ConnectionProxyRegionCoordinator(sharding, factory))
      coordinator ! StartListener(ChatConnectionDesc("foo", 23, "bar"))

      shardingProbe.expectMsg(ForwardToListener(ChatConnectionDesc("foo", 23, "bar"), Initialize(ChatConnectionDesc("foo", 23, "bar"), factory)))
    }

    "forward message to listener" in {
      val shardingProbe = TestProbe()
      val sharding = mock[ClusterSharding]
      when(sharding.shardRegion("Proxy")).thenReturn(shardingProbe.ref)

      val coordinator = TestActorRef(new ConnectionProxyRegionCoordinator(sharding, mock[ChatConnectionFactory]))
      coordinator ! ForwardToListener(ChatConnectionDesc("foo", 23, "bar"), 2)

      shardingProbe.expectMsg(ForwardToListener(ChatConnectionDesc("foo", 23, "bar"), 2))
    }
  }
}
