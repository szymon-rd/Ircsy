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
import pl.jaca.ircsy.clientnode.listening.ConnectionProxyRegionCoordinator.{StopProxy, ForwardToProxy, StartProxy}
import pl.jaca.ircsy.util.test.MoreMockitoSugar
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * @author Jaca777
  *         Created 2016-05-02 at 00
  */
class ConnectionProxyRegionCoordinatorSpec extends TestKit(ActorSystem("ConnectionProxyRegionCoordinatorSpec")) with WordSpecLike with MockitoSugar with MoreMockitoSugar {

  val testDesc: ChatConnectionDesc = ChatConnectionDesc("foo", 42, "bar")

  "ConnectionProxyRegionCoordinator" should {

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


    "start new proxy" in {
      val shardingProbe = TestProbe()
      val sharding = mock[ClusterSharding]
      when(sharding.shardRegion("Proxy")).thenReturn(shardingProbe.ref)

      val factory = mock[ChatConnectionFactory]
      val coordinator = TestActorRef(new ConnectionProxyRegionCoordinator(sharding, factory))
      coordinator ! StartProxy(testDesc)

      shardingProbe.expectMsg(ForwardToProxy(testDesc, Initialize(testDesc, factory)))
      shardingProbe.expectMsg(ForwardToProxy(testDesc, Start))
    }

    "forward message to proxy" in {
      val shardingProbe = TestProbe()
      val sharding = mock[ClusterSharding]
      when(sharding.shardRegion("Proxy")).thenReturn(shardingProbe.ref)

      val coordinator = TestActorRef(new ConnectionProxyRegionCoordinator(sharding, mock[ChatConnectionFactory]))
      coordinator ! ForwardToProxy(testDesc, 2)

      shardingProbe.expectMsg(ForwardToProxy(testDesc, 2))
    }

    "stop proxy" in {
      val shardingProbe = TestProbe()
      val sharding = mock[ClusterSharding]
      when(sharding.shardRegion("Proxy")).thenReturn(shardingProbe.ref)

      val coordinator = TestActorRef(new ConnectionProxyRegionCoordinator(sharding, mock[ChatConnectionFactory]))
      coordinator ! StartProxy(testDesc)
      shardingProbe.fishForMessage(max = 1 second) {case msg: ForwardToProxy => true} //Init
      shardingProbe.fishForMessage(max = 1 second) {case msg: ForwardToProxy => true}//Start
      coordinator ! StopProxy(testDesc)
      shardingProbe.expectMsg(ForwardToProxy(testDesc, Stop))
    }
  }

}
