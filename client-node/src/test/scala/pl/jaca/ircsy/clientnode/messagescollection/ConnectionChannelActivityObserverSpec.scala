package pl.jaca.ircsy.clientnode.messagescollection

import akka.actor._
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe}
import akka.testkit.{TestKitBase, TestProbe}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{Matchers, WordSpec}
import pl.jaca.ircsy.clientnode.connection.ConnectionObservableProxy.ProxyState
import pl.jaca.ircsy.clientnode.connection.{ChatConnectionFactory, ConnectionDesc, ServerDesc}
import pl.jaca.ircsy.clientnode.messagescollection.ConnectionActivityObserver.{ChannelConnectionFound, FindChannelConnection}
import pl.jaca.ircsy.clientnode.observableactor.ObservableActorProtocol.RegisterObserver

import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * @author Jaca777
  *         Created 2016-05-05 at 13
  */
class ConnectionChannelActivityObserverSpec extends {
  implicit val system = ActorSystem("ConnectionProxyRegionCoordinatorSpec")
} with WordSpec with TestKitBase with Matchers with MockFactory {

  class MockablePubSub extends DistributedPubSub(system.asInstanceOf[ExtendedActorSystem])

  val serverDesc = ServerDesc("foo", 42)
  val testDesc: ConnectionDesc = ConnectionDesc(serverDesc, "bar")
  val testState = ProxyState(true, testDesc, mock[ChatConnectionFactory], Set("foo", "bar"), Set.empty)

  "ConnectionChannelActivityObserver" should {
    "subscribe to server channels topic" in {
      val mediator = TestProbe()
      val observableProxy = TestProbe()
      val activityObserver = system.actorOf(Props(new ConnectionActivityObserver(observableProxy.ref, mediator.ref)))
      observableProxy.expectMsgType[RegisterObserver]
      observableProxy.reply(testState)
      mediator.expectMsg(Subscribe("channels-" + serverDesc, activityObserver))
    }

    "reply to broadcasted channel connection request if channel is available" in {
      val mediator = TestProbe()
      val observableProxy = TestProbe()
      val activityObserver = system.actorOf(Props(new ConnectionActivityObserver(observableProxy.ref, mediator.ref)))
      observableProxy.receiveOne(300 millis)
      observableProxy.reply(testState)
      activityObserver ! FindChannelConnection(serverDesc, "foo")
      mediator.receiveOne(300 millis)
      mediator.expectMsg(Publish("channels-foo:42", ChannelConnectionFound(serverDesc, "foo", observableProxy.ref)))
    }

    "not reply to broadcasted channel connection request if channel is not available" in {
      val mediator = TestProbe()
      val observableProxy = TestProbe()
      val activityObserver = system.actorOf(Props(new ConnectionActivityObserver(observableProxy.ref, mediator.ref)))
      observableProxy.receiveOne(300 millis)
      observableProxy.reply(testState)
      activityObserver ! FindChannelConnection(serverDesc, "miska")
      mediator.receiveOne(300 millis)
      mediator.expectNoMsg()
    }
  }
}
