package pl.jaca.ircsy.clientnode.connection

import akka.actor._
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe}
import akka.testkit.{TestKitBase, TestProbe}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{Matchers, WordSpec}
import pl.jaca.ircsy.chat.{ConnectionDesc, ServerDesc}
import pl.jaca.ircsy.clientnode.connection.ConnectionObservableProxy.ProxyState
import pl.jaca.ircsy.clientnode.connection.ConnectionProxyPublisher.{ChannelConnectionFound, FindChannelConnection, FindUserConnection, UserConnectionFound}
import pl.jaca.ircsy.clientnode.connection.ConnectionProxyRegionCoordinator.ForwardToProxy
import pl.jaca.ircsy.clientnode.observableactor.ObservableActorProtocol.RegisterObserver

import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * @author Jaca777
  *         Created 2016-05-05 at 13
  */
class ConnectionProxyPublisherSpec extends {
  implicit val system = ActorSystem("ConnectionProxyPublisher")
} with WordSpec with TestKitBase with Matchers with MockFactory {

  class MockablePubSub extends DistributedPubSub(system.asInstanceOf[ExtendedActorSystem])

  val serverDesc = new ServerDesc("foo", 42)
  val testDesc: ConnectionDesc = new ConnectionDesc(serverDesc, "bar")
  val testState = ProxyState(true, testDesc, mock[ChatConnectionFactory], Set("foo", "bar"), Set.empty)

  "ConnectionProxyPublisher" should {
    "subscribe to server channels topic" in {
      val mediator = TestProbe()
      val proxy = TestProbe()
      val publisher = system.actorOf(Props(new ConnectionProxyPublisher(testDesc, proxy.ref, mediator.ref)))
      proxy.expectMsgType[RegisterObserver]
      proxy.reply(testState)
      mediator.expectMsg(Subscribe("channels-" + serverDesc, publisher))
    }

    "reply to broadcasted channel connection request if channel is available" in {
      val mediator = TestProbe()
      val proxy = TestProbe()
      val publisher = system.actorOf(Props(new ConnectionProxyPublisher(testDesc, proxy.ref, mediator.ref)))
      proxy.receiveOne(300 millis)
      proxy.reply(testState)
      publisher ! FindChannelConnection(serverDesc, "foo")
      mediator.receiveOne(300 millis)
      mediator.receiveOne(300 millis)
      mediator.expectMsg(Publish("channels-foo:42", ChannelConnectionFound("foo", testDesc, proxy.ref)))
    }

    "reply to broadcasted channel connection request if channel is not available" in {
      val mediator = TestProbe()
      val proxy = TestProbe()
      val publisher = system.actorOf(Props(new ConnectionProxyPublisher(testDesc, proxy.ref, mediator.ref)))
      proxy.receiveOne(300 millis)
      proxy.reply(testState)
      publisher ! FindUserConnection(testDesc)
      mediator.receiveOne(300 millis)
      mediator.receiveOne(300 millis)
      mediator.expectMsg(Publish("users-foo:42", UserConnectionFound(testDesc, proxy.ref)))
    }

    "not reply to broadcasted user connection request if available" in {
      val mediator = TestProbe()
      val proxy = TestProbe()
      val publisher = system.actorOf(Props(new ConnectionProxyPublisher(testDesc, proxy.ref, mediator.ref)))
      proxy.receiveOne(300 millis)
      proxy.reply(testState)
      publisher ! FindUserConnection(new ConnectionDesc(serverDesc, "miras"))
      mediator.receiveOne(300 millis)
      mediator.receiveOne(300 millis)
      mediator.expectNoMsg()
    }

  }
}
