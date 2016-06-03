package pl.jaca.ircsy.clientnode.connection

import java.util.concurrent.ThreadFactory

import akka.actor.ActorSystem.Settings
import akka.actor._
import akka.cluster.sharding.ShardCoordinator.ShardAllocationStrategy
import akka.cluster.sharding.ShardRegion.{ExtractEntityId, ExtractShardId, MessageExtractor}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings, ShardRegion}
import akka.dispatch.{Dispatchers, Mailboxes}
import akka.event.{EventStream, LoggingAdapter, LoggingFilter}
import akka.testkit._
import org.scalamock.scalatest.MockFactory
import org.scalatest._
import pl.jaca.ircsy.chat.{ConnectionDesc, ServerDesc}
import pl.jaca.ircsy.clientnode.connection.ConnectionObservableProxy.{Start, Stop}
import pl.jaca.ircsy.clientnode.connection.ConnectionProxyRegionCoordinator.{ForwardToProxy, StartProxy, StopProxy}
import pl.jaca.ircsy.clientnode.connection.ConnectionProxySupervisor.Initialize
import pl.jaca.ircsy.clientnode.sharding.RegionAwareClusterSharding

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * @author Jaca777
  *         Created 2016-05-02 at 00
  */
class ConnectionProxyRegionCoordinatorSpec extends {
  implicit val system = ActorSystem("ConnectionProxyRegionCoordinatorSpec")
} with WordSpec with TestKitBase with Matchers with MockFactory {



  val testDesc: ConnectionDesc = new ConnectionDesc(new ServerDesc("foo", 42), "bar")
  val mediator = TestProbe().ref

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
      TestActorRef(new ConnectionProxyRegionCoordinator(sharding, null, mediator))
    }



    "start new proxy" in {
      val shardingProbe = TestProbe()
      val sharding = mock[RegionAwareClusterSharding]
      (sharding.findOrStartRegion _).expects(*,*,*,*,*,*).returns(shardingProbe.ref)

      val factory = mock[ChatConnectionFactory]
      val coordinator = TestActorRef(new ConnectionProxyRegionCoordinator(sharding, factory, mediator))
      coordinator ! StartProxy(testDesc)

      shardingProbe.expectMsg(ForwardToProxy(testDesc, Initialize(testDesc, factory, mediator)))
      shardingProbe.expectMsg(ForwardToProxy(testDesc, Start))
    }

    "forward message to proxy" in {
      val shardingProbe = TestProbe()
      val sharding = mock[RegionAwareClusterSharding]
      (sharding.findOrStartRegion _).expects(*,*,*,*,*,*).returns(shardingProbe.ref)

      val coordinator = TestActorRef(new ConnectionProxyRegionCoordinator(sharding, mock[ChatConnectionFactory], mediator))
      coordinator ! ForwardToProxy(testDesc, 2)

      shardingProbe.expectMsg(ForwardToProxy(testDesc, 2))
    }

    "stop proxy" in {
      val shardingProbe = TestProbe()
      val sharding = mock[RegionAwareClusterSharding]
      (sharding.findOrStartRegion _).expects(*,*,*,*,*,*).returns(shardingProbe.ref)

      val coordinator = TestActorRef(new ConnectionProxyRegionCoordinator(sharding, mock[ChatConnectionFactory], mediator))
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

      val coordinator = TestActorRef(new ConnectionProxyRegionCoordinator(sharding, mock[ChatConnectionFactory], mediator))
      coordinator ! Stop
      shardingProbe.expectMsg(ShardRegion.GracefulShutdown)
    }
  }

}
