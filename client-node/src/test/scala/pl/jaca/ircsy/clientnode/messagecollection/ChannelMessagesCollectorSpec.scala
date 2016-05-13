package pl.jaca.ircsy.clientnode.messagecollection

import java.time.LocalDate

import akka.actor.{Terminated, Props, ActorSystem}
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe}

import akka.testkit.{TestActorRef, TestProbe, TestKitBase, TestKit}
import org.scalamock.matchers.MockParameter
import org.scalamock.scalatest.MockFactory
import org.scalatest.{Matchers, WordSpec, WordSpecLike}
import pl.jaca.ircsy.chat.messages.{ChatUser, ChannelMessage}
import pl.jaca.ircsy.clientnode.connection.ConnectionObservableProxy.{LeftChannel, ChannelMessageReceived, ChannelSubject}
import pl.jaca.ircsy.clientnode.connection.ServerDesc
import pl.jaca.ircsy.clientnode.messagecollection.ChannelMessageCollector.Stop
import pl.jaca.ircsy.clientnode.messagecollection.ConnectionActivityObserver.{ChannelConnectionFound, FindChannelConnection, FindUserConnection}
import pl.jaca.ircsy.clientnode.messagecollection.repository.{MessageRepositoryFactory, MessageRepository}
import pl.jaca.ircsy.clientnode.observableactor.ObservableActorProtocol.{UnregisterObserver, ClassFilterSubject, Observer, RegisterObserver}
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * @author Jaca777
  *         Created 2016-05-02 at 12
  */
class ChannelMessagesCollectorSpec extends {
  implicit val system = ActorSystem("MessagesCollectorSpec")
} with WordSpec with TestKitBase with Matchers with MockFactory {

  val serverDesc = ServerDesc("foo", 42)
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
      collector ! ChannelConnectionFound(serverDesc, "bar", proxy.ref)
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
      collector ! ChannelConnectionFound(serverDesc, "bar", proxy.ref)
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
      collector ! ChannelConnectionFound(serverDesc, "bar", proxy.ref)
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
      collector ! ChannelConnectionFound(serverDesc, "bar", proxy.ref)
      proxy.receiveN(1) // Register observer

      collector ! Stop
      watch(collector)
      proxy.expectMsg(UnregisterObserver(Observer(collector, Set(ChannelSubject("bar")))))
      expectTerminated(collector)
    }
  }
}
