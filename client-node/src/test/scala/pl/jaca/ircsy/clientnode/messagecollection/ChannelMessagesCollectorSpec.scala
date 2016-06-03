package pl.jaca.ircsy.clientnode.messagecollection

import java.time.LocalDate

import akka.actor.{ActorSystem, Props}
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe}
import akka.testkit.{TestKitBase, TestProbe}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{Matchers, WordSpec}
import pl.jaca.ircsy.chat.{ConnectionDesc, ServerDesc}
import pl.jaca.ircsy.chat.messages.{ChannelMessage, ChatUser}
import pl.jaca.ircsy.clientnode.connection.ConnectionObservableProxy.{ChannelMessageReceived, ChannelSubject, LeftChannel}
import pl.jaca.ircsy.clientnode.connection.ConnectionProxyPublisher
import pl.jaca.ircsy.clientnode.connection.ConnectionProxyPublisher.{ChannelConnectionFound, FindChannelConnection}
import pl.jaca.ircsy.clientnode.messagecollection.ChannelMessageCollector.Stop
import pl.jaca.ircsy.clientnode.messagecollection.repository.{MessageRepository, MessageRepositoryFactory}
import pl.jaca.ircsy.clientnode.observableactor.ObservableActorProtocol.{Observer, RegisterObserver, UnregisterObserver}

import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * @author Jaca777
  *         Created 2016-05-02 at 12
  */
class ChannelMessagesCollectorSpec extends {
  implicit val system = ActorSystem("MessagesCollectorSpec")
} with WordSpec with TestKitBase with Matchers with MockFactory {

  val serverDesc = new ServerDesc("foo", 42)
  val connection = new ConnectionDesc(serverDesc, "bar")
  val testUser = new ChatUser("foo", "bar", "foo")

  "ChannelMessagesCollector" should {
    "subscribe to channels topic" in {
      val mediator = TestProbe()
      val repository = mock[MessageRepository]
      val factory = mock[MessageRepositoryFactory]
      (factory.newRepository _).expects().returns(repository)
      val collector = system.actorOf(Props(new ChannelMessageCollector(serverDesc, "bar", mediator.ref, factory)))
      mediator.expectMsg(Subscribe("channels-foo:42", collector))
    }

    "schedule messages broadcasting" in {
      val mediator = TestProbe()
      val repository = mock[MessageRepository]
      val factory = mock[MessageRepositoryFactory]
      (factory.newRepository _).expects().returns(repository)
      val collector = system.actorOf(Props(new ChannelMessageCollector(serverDesc, "bar", mediator.ref, factory)))
      mediator.expectMsg(Subscribe("channels-foo:42", collector))
      within(2 seconds) {
        mediator.receiveN(2).foreach {
          msg => msg should be(Publish("channels-foo:42", FindChannelConnection(serverDesc, "bar")))
        }
      }
    }

    "register observer" in {
      val mediator = TestProbe()
      val proxy = TestProbe()
      val repository = mock[MessageRepository]
      val factory = mock[MessageRepositoryFactory]
      (factory.newRepository _).expects().returns(repository)
      val collector = system.actorOf(Props(new ChannelMessageCollector(serverDesc, "bar", mediator.ref, factory)))
      mediator.receiveN(2) // Subscribe, publish
      collector ! ChannelConnectionFound("bar", connection, proxy.ref)
      proxy.expectMsg(RegisterObserver(Observer(collector, Set(ChannelSubject("bar")))))
    }


    "collect messages in" in {
      val mediator = TestProbe()
      val proxy = TestProbe()
      val repository = mock[MessageRepository]
      val factory = mock[MessageRepositoryFactory]
      (factory.newRepository _).expects().returns(repository)
      val message = new ChannelMessage("bar", LocalDate.now(), testUser, "message")
      (repository.addChannelMessage _).expects(serverDesc, message)

      val collector = system.actorOf(Props(new ChannelMessageCollector(serverDesc, "bar", mediator.ref, factory)))
      mediator.receiveN(2) // Subscribe, publish
      collector ! ChannelConnectionFound("bar", connection, proxy.ref)
      proxy.receiveN(1) // Register observer
      proxy.send(collector, ChannelMessageReceived(message))
      Thread.sleep(200)
    }

    "start looking for new proxy when observed proxy leaves channel" in {
      val mediator = TestProbe()
      val proxy = TestProbe()
      val factory = mock[MessageRepositoryFactory]
      val repository = mock[MessageRepository]
      (factory.newRepository _).expects().returns(repository)

      val collector = system.actorOf(Props(new ChannelMessageCollector(serverDesc, "bar", mediator.ref, factory)))
      mediator.receiveN(2) // Subscribe, publish
      collector ! ChannelConnectionFound("bar", connection, proxy.ref)
      proxy.receiveN(1) // Register observer
      proxy.send(collector, LeftChannel("bar"))
      mediator.expectMsg(Publish("channels-foo:42", FindChannelConnection(serverDesc, "bar")))
    }

    "unsubscribe and stop" in {
      val mediator = TestProbe()
      val proxy = TestProbe()
      val factory = mock[MessageRepositoryFactory]
      val repository = mock[MessageRepository]
      (factory.newRepository _).expects().returns(repository)
      val collector = system.actorOf(Props(new ChannelMessageCollector(serverDesc, "bar", mediator.ref, factory)))
      mediator.receiveN(2) // Subscribe, publish
      collector ! ChannelConnectionFound("bar", connection, proxy.ref)
      proxy.receiveN(1) // Register observer

      collector ! Stop
      watch(collector)
      proxy.expectMsg(UnregisterObserver(Observer(collector, Set(ChannelSubject("bar")))))
      expectTerminated(collector)
    }
  }
}
