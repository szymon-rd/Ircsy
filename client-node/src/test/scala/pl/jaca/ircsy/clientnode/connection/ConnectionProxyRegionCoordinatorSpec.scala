package pl.jaca.ircsy.clientnode.connection

import java.util.concurrent.ThreadFactory

import akka.actor.ActorSystem.Settings
import akka.actor._
import akka.cluster.sharding.ShardCoordinator.ShardAllocationStrategy
import akka.cluster.sharding.ShardRegion.{MessageExtractor, ExtractShardId, ExtractEntityId}
import akka.cluster.sharding.{ShardRegion, ClusterShardingSettings, ClusterSharding}
import akka.dispatch.{Dispatchers, Mailboxes}
import akka.event.{LoggingFilter, LoggingAdapter, EventStream}
import akka.testkit._
import org.scalamock.scalatest.MockFactory
import org.scalatest._
import pl.jaca.ircsy.clientnode.connection.ConnectionObservableProxy.{Start, Stop}
import pl.jaca.ircsy.clientnode.connection.ConnectionProxyRegionCoordinator.{ForwardToProxy, StartProxy, StopProxy}
import pl.jaca.ircsy.clientnode.connection.ConnectionProxySupervisor.InitializeConnection
import pl.jaca.ircsy.clientnode.sharding.RegionAwareClusterSharding

import scala.concurrent.{Future, ExecutionContextExecutor}
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * @author Jaca777
  *         Created 2016-05-02 at 00
  */
class ConnectionProxyRegionCoordinatorSpec extends {
  implicit val system = ActorSystem("ConnectionProxyRegionCoordinatorSpec")
} with WordSpec with TestKitBase with Matchers with MockFactory {



  val testDesc: ConnectionDesc = ConnectionDesc(ServerDesc("foo", 42), "bar")


  "ConnectionProxyRegionCoordinator" should {

    class MockableClusterSharding extends ClusterSharding(null) {
      override def start(typeName: String,
                         entityProps: Props,
                         settings: ClusterShardingSettings,
                         messageExtractor: MessageExtractor,
                         allocationStrategy: ShardAllocationStrategy,
                         handOffStopMessage: Any): ActorRef = started(typeName, entityProps, handOffStopMessage)

      def started(name: String, props: Props, handOffStopMessage: Any) = null
    }

    "start region" in {
      val sharding = mock[RegionAwareClusterSharding]
      (sharding.findOrStartRegion _).expects(system, "ConnectionProxy", *, *, *, Stop)
      TestActorRef(new ConnectionProxyRegionCoordinator(sharding, null))
    }



    "start new proxy" in {
      val shardingProbe = TestProbe()
      val sharding = mock[RegionAwareClusterSharding]
      (sharding.findOrStartRegion _).expects(*,*,*,*,*,*).returns(shardingProbe.ref)

      val factory = mock[ChatConnectionFactory]
      val coordinator = TestActorRef(new ConnectionProxyRegionCoordinator(sharding, factory))
      coordinator ! StartProxy(testDesc)

      shardingProbe.expectMsg(ForwardToProxy(testDesc, InitializeConnection(testDesc, factory)))
      shardingProbe.expectMsg(ForwardToProxy(testDesc, Start))
    }

    "forward message to proxy" in {
      val shardingProbe = TestProbe()
      val sharding = mock[RegionAwareClusterSharding]
      (sharding.findOrStartRegion _).expects(*,*,*,*,*,*).returns(shardingProbe.ref)

      val coordinator = TestActorRef(new ConnectionProxyRegionCoordinator(sharding, mock[ChatConnectionFactory]))
      coordinator ! ForwardToProxy(testDesc, 2)

      shardingProbe.expectMsg(ForwardToProxy(testDesc, 2))
    }

    "stop proxy" in {
      val shardingProbe = TestProbe()
      val sharding = mock[RegionAwareClusterSharding]
      (sharding.findOrStartRegion _).expects(*,*,*,*,*,*).returns(shardingProbe.ref)

      val coordinator = TestActorRef(new ConnectionProxyRegionCoordinator(sharding, mock[ChatConnectionFactory]))
      coordinator ! StartProxy(testDesc)
      shardingProbe.fishForMessage(max = 1 second) { case msg: ForwardToProxy => true } //Init
      shardingProbe.fishForMessage(max = 1 second) { case msg: ForwardToProxy => true } //Start
      coordinator ! StopProxy(testDesc)
      shardingProbe.expectMsg(ForwardToProxy(testDesc, Stop))
    }

    "stop shard" in {
      val shardingProbe = TestProbe()
      val sharding = mock[RegionAwareClusterSharding]
      (sharding.findOrStartRegion _).expects(*,*,*,*,*,*).returns(shardingProbe.ref)

      val coordinator = TestActorRef(new ConnectionProxyRegionCoordinator(sharding, mock[ChatConnectionFactory]))
      coordinator ! Stop
      shardingProbe.expectMsg(ShardRegion.GracefulShutdown)
    }
  }

}
