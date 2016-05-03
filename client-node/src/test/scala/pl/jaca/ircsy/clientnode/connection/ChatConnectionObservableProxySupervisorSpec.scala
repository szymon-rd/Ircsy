package pl.jaca.ircsy.clientnode.connection

import java.io.IOException

import akka.actor.{Props, ActorSystem}
import akka.persistence.PersistentActor
import akka.testkit.{TestActorRef, TestKit}
import org.mockito.internal.verification.VerificationModeFactory
import org.scalatest.{Matchers, WordSpecLike}
import org.scalatest.mockito.MockitoSugar
import pl.jaca.ircsy.clientnode.connection.ChatConnectionObservableProxy._
import pl.jaca.ircsy.clientnode.connection.ChatConnectionObservableProxySupervisor.Initialize
import pl.jaca.ircsy.util.test.MoreMockitoSugar
import org.mockito.Mockito._
import rx.lang.scala.Observable
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * @author Jaca777
  *         Created 2016-05-02 at 12
  */
class ChatConnectionObservableProxySupervisorSpec extends TestKit(ActorSystem("ChatConnectionObservableProxySpec")) with Matchers with WordSpecLike with MockitoSugar with MoreMockitoSugar {

  val testDesc: ChatConnectionDesc = ChatConnectionDesc("foo", 42, "bar")

  "ChatConnectionObservableProxy" should {
    "start" in {
      val factory = mock[ChatConnectionFactory]
      val connection = mock[ChatConnection]
      when(factory.newConnection()).thenReturn(connection)
      when(connection.channelMessages).thenReturn(Observable.empty)
      when(connection.privateMessages).thenReturn(Observable.empty)
      val proxy = system.actorOf(Props[ChatConnectionObservableProxySupervisor])
      proxy ! Initialize(testDesc, factory)
      proxy ! Start
      Thread.sleep(200)
      verify(connection).connectTo(testDesc)
    }
    "stop" in {
      val factory = mock[ChatConnectionFactory]
      val connection = mock[ChatConnection]
      when(factory.newConnection()).thenReturn(connection)
      when(connection.channelMessages).thenReturn(Observable.empty)
      when(connection.privateMessages).thenReturn(Observable.empty)
      val proxy = system.actorOf(Props[ChatConnectionObservableProxySupervisor])
      proxy ! Initialize(testDesc, factory)
      proxy ! Start
      proxy ! Stop
      Thread.sleep(200)
      verify(connection).disconnect()
    }
    "join channel" in {
      val factory = mock[ChatConnectionFactory]
      val connection = mock[ChatConnection]
      when(factory.newConnection()).thenReturn(connection)
      when(connection.channelMessages).thenReturn(Observable.empty)
      when(connection.privateMessages).thenReturn(Observable.empty)
      val proxy = system.actorOf(Props[ChatConnectionObservableProxySupervisor])
      proxy ! Initialize(testDesc, factory)
      proxy ! Start
      proxy ! JoinChannel("channel")
      proxy ! Stop
      Thread.sleep(200)
      verify(connection).joinChannel("channel")
    }

    "notify observers when joined channel" in {
      val factory = mock[ChatConnectionFactory]
      val connection = mock[ChatConnection]
      when(factory.newConnection()).thenReturn(connection)
      when(connection.channelMessages).thenReturn(Observable.empty)
      when(connection.privateMessages).thenReturn(Observable.empty)
      val proxy = system.actorOf(Props[ChatConnectionObservableProxySupervisor])
      proxy ! Initialize(testDesc, factory)
      proxy ! RegisterObserver(Observer(testActor, classOf[JoinedChannel]))
      proxy ! Start
      proxy ! JoinChannel("channel")
      proxy ! Stop
      receiveOne(200 millis) should be (JoinedChannel("channel"))
    }

    "notify observers when failed to join channel" in {
      val factory = mock[ChatConnectionFactory]
      val connection = mock[ChatConnection]
      when(factory.newConnection()).thenReturn(connection)
      when(connection.channelMessages).thenReturn(Observable.empty)
      when(connection.privateMessages).thenReturn(Observable.empty)
      when(connection.joinChannel(any[String])).thenThrow(new RuntimeException())
      val proxy = system.actorOf(Props[ChatConnectionObservableProxySupervisor])
      proxy ! Initialize(testDesc, factory)
      proxy ! RegisterObserver(Observer(testActor, classOf[JoinedChannel], classOf[FailedToJoinChannel]))
      proxy ! Start
      proxy ! JoinChannel("channel")
      proxy ! Stop
      receiveOne(200 millis) should be (FailedToJoinChannel("channel"))
    }
  }
}
