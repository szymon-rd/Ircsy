package pl.jaca.ircsy.clientnode.messagecollection

import java.time.LocalDate

import akka.actor.{Props, ActorSystem}
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe}
import akka.testkit.{TestProbe, TestKitBase}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{Matchers, WordSpec}
import pl.jaca.ircsy.clientnode.connection.ConnectionObservableProxy._
import pl.jaca.ircsy.clientnode.connection.{ConnectionDesc, ServerDesc}
import pl.jaca.ircsy.clientnode.connection.messages.{PrivateMessage, ChannelMessage}
import pl.jaca.ircsy.clientnode.messagecollection.ConnectionActivityObserver.{UserConnectionFound, FindUserConnection, ChannelConnectionFound, FindChannelConnection}
import pl.jaca.ircsy.clientnode.messagecollection.repository.{MessageRepositoryFactory, MessageRepository}
import pl.jaca.ircsy.clientnode.observableactor.ObservableActorProtocol.{ClassFilterSubject, Observer, RegisterObserver}
import scala.concurrent.duration._
import scala.language.postfixOps


/**
  * @author Jaca777
  *         Created 2016-05-08 at 00
  */
class PrivateMessageCollectorSpec extends {
  implicit val system = ActorSystem("PrivateMessageCollectorSpec")
} with WordSpec with TestKitBase with Matchers with MockFactory {

  val serverDesc = ServerDesc("foo", 42)
  val connectionDesc = ConnectionDesc(serverDesc, "user")

  "PrivateMessageCollector" should {
    "subscribe to users topic" in {
      val mediator = TestProbe()
      val repository = mock[MessageRepository]
      val factory = mock[MessageRepositoryFactory]
      (factory.newRepository _).expects().returns(repository)
      val collector = system.actorOf(Props(new PrivateMessageCollector(connectionDesc, mediator.ref, factory)))
      mediator.expectMsg(Subscribe("users-foo:42", collector))
    }

    "schedule messages broadcasting" in {
      val mediator = TestProbe()
      val repository = mock[MessageRepository]
      val factory = mock[MessageRepositoryFactory]
      (factory.newRepository _).expects().returns(repository)
      val collector = system.actorOf(Props(new PrivateMessageCollector(connectionDesc, mediator.ref, factory)))
      mediator.receiveN(1)
      within(2 seconds) {
        mediator.receiveN(2).foreach {
          msg => msg should be(Publish("users-foo:42", FindUserConnection(connectionDesc)))
        }
      }
    }

    "register observer" in {
      val mediator = TestProbe()
      val proxy = TestProbe()
      val repository = mock[MessageRepository]
      val factory = mock[MessageRepositoryFactory]
      (factory.newRepository _).expects().returns(repository)
      val collector = system.actorOf(Props(new PrivateMessageCollector(connectionDesc, mediator.ref, factory)))
      mediator.receiveN(2)
      collector ! UserConnectionFound(connectionDesc, proxy.ref)
      proxy.expectMsg(RegisterObserver(Observer(collector, Set(ClassFilterSubject(classOf[PrivateMessageReceived], classOf[DisconnectedFromServer])))))
    }


    "collect messages in" in {
      val mediator = TestProbe()
      val proxy = TestProbe()
      val repository = mock[MessageRepository]
      val factory = mock[MessageRepositoryFactory]
      (factory.newRepository _).expects().returns(repository)
      val message = PrivateMessage("user", "user2", "user", LocalDate.now(), "message")
      (repository.addPrivateMessage _).expects(serverDesc, message)

      val collector = system.actorOf(Props(new PrivateMessageCollector(connectionDesc, mediator.ref, factory)))
      mediator.receiveN(2)
      collector ! UserConnectionFound(connectionDesc, proxy.ref)
      proxy.receiveN(1)
      proxy.send(collector, PrivateMessageReceived(message))
      Thread.sleep(200)
    }

    "start looking for new proxy when observed proxy disconnects" in {
      val mediator = TestProbe()
      val proxy = TestProbe()
      val factory = mock[MessageRepositoryFactory]
      val repository = mock[MessageRepository]
      (factory.newRepository _).expects().returns(repository)

      val collector = system.actorOf(Props(new PrivateMessageCollector(connectionDesc, mediator.ref, factory)))
      mediator.receiveN(2)
      collector ! UserConnectionFound(connectionDesc, proxy.ref)
      proxy.receiveN(1)
      proxy.send(collector, DisconnectedFromServer(connectionDesc))
      mediator.expectMsg(Publish("users-foo:42", FindUserConnection(connectionDesc)))
    }
  }
}

