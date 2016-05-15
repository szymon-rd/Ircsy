package pl.jaca.ircsy.clientnode.messagecollection

import akka.actor.{ActorRef, ExtendedActorSystem, Props, ActorSystem}
import akka.cluster.sharding.ShardCoordinator.ShardAllocationStrategy
import akka.cluster.sharding.ShardRegion.MessageExtractor
import akka.cluster.sharding.{ShardRegion, ClusterShardingSettings, ClusterSharding}
import akka.testkit.{TestKitBase, TestProbe, TestActorRef, TestKit}

import org.scalamock.scalatest.MockFactory
import org.scalatest.{Matchers, WordSpec, WordSpecLike}
import pl.jaca.ircsy.clientnode.connection.ConnectionObservableProxy.Start
import pl.jaca.ircsy.clientnode.connection.{ConnectionDesc, ServerDesc}
import pl.jaca.ircsy.clientnode.messagecollection.PrivateMessageCollectorSupervisor.Stop
import pl.jaca.ircsy.clientnode.sharding.RegionAwareClusterSharding

/**
  * @author Jaca777
  *         Created 2016-05-02 at 12
  */
class MessageCollectionRegionCoordinatorSpec extends {
  implicit val system = ActorSystem("ConnectionProxyRegionCoordinatorSpec")
} with WordSpec with TestKitBase with Matchers with MockFactory {


  val serverDesc = ServerDesc("foo", 42)
  val connectionDesc = ConnectionDesc(serverDesc, "nick")

  class MockableClusterSharding extends ClusterSharding(system.asInstanceOf[ExtendedActorSystem]) {
    override def start(typeName: String,
                       entityProps: Props,
                       settings: ClusterShardingSettings,
                       messageExtractor: MessageExtractor,
                       allocationStrategy: ShardAllocationStrategy,
                       handOffStopMessage: Any): ActorRef = started(typeName, entityProps, handOffStopMessage)
    def started(name: String, props: Props, handOffStopMessage: Any) = null
  }

  "MessageCollectionRegionCoordinator" should {

/*    "start sharding region" in {
      val sharding = mock[RegionAwareClusterSharding]
      (sharding.findOrStartRegion _).expects(system, "Collector", Props[MessageCollectorSupervisor], *, *, Stop)
      TestActorRef(new MessageCollectionRegionCoordinator(sharding, null))
    }*/

/*    "start channel messages collection" in {
      val shardingProbe = TestProbe()
      val sharding = mock[RegionAwareClusterSharding]
     (sharding.findOrStartRegion _).expects(*, *, *, *, *, *).returns(shardingProbe.ref)
      val coordinator = TestActorRef(new MessageCollectionRegionCoordinator(sharding, null))
      coordinator ! StartChannelMessageCollector(serverDesc, "channel")
      shardingProbe.expectMsg(ForwardToPrivateMessageCollector(connectionDesc, Start))
    }

    "start private messages collection" in {
      val shardingProbe = TestProbe()
      val sharding = mock[RegionAwareClusterSharding]
      (sharding.findOrStartRegion _).expects(*, *, *, *, *, *).returns(shardingProbe.ref)
      val coordinator = TestActorRef(new MessageCollectionRegionCoordinator(sharding, null))
      coordinator ! StartPrivateMessageCollector(serverDesc, "channel")
      shardingProbe.expectMsg(ForwardToChannelMessageCollector(serverDesc, "channel", InitializeConnection(testDesc, factory)))
    }*/

  }
}
