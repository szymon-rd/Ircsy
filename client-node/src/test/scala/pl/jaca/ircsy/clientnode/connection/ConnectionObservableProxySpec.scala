package pl.jaca.ircsy.clientnode.connection

import akka.actor.{ActorSystem, Props}
import akka.testkit.{TestKitBase, TestKit}
import org.scalamock.scalatest.MockFactory
import org.scalatest._
import pl.jaca.ircsy.clientnode.connection.ConnectionObservableProxy._
import pl.jaca.ircsy.clientnode.observableactor.ObservableActorProtocol.{ClassFilterSubject, Observer, RegisterObserver}
import rx.lang.scala.Observable

import scala.concurrent.duration._
import scala.language.postfixOps


/**
  * @author Jaca777
  *         Created 2016-05-04 at 23
  */
class ConnectionObservableProxySpec extends {
  implicit val system = ActorSystem("ConnectionProxyRegionCoordinatorSpec")
} with WordSpec with TestKitBase with Matchers with OneInstancePerTest with MockFactory {




  val testDesc: ConnectionDesc = ConnectionDesc(ServerDesc("foo", 42), "bar")

  "ConnectionObservableProxy" should {
    "join channel" in {
      val factory = mock[ChatConnectionFactory]
      val connection = mock[ChatConnection]
      (factory.newConnection _).expects().returns(connection)
      (connection.joinChannel _).expects("channel")
      (connection.channelMessages _).expects().returns(Observable.empty)
      (connection.privateMessages _).expects().returns(Observable.empty)
      val proxy = system.actorOf(Props(new ConnectionObservableProxy(testDesc, factory)))
      proxy ! Start
      proxy ! JoinChannel("channel")
      Thread.sleep(400)
    }


    "notify observers when child joined channel" in {
      val factory = mock[ChatConnectionFactory]
      val connection = mock[ChatConnection]
      (factory.newConnection _).expects().returns(connection)
      (connection.channelMessages _).expects().returns(Observable.empty)
      (connection.privateMessages _).expects().returns(Observable.empty)
      (connection.connectTo _).expects(*)
      (connection.joinChannel _).expects(*)
      val proxy = system.actorOf(Props(new ConnectionObservableProxy(testDesc, factory)))
      proxy ! RegisterObserver(Observer(testActor, Set(ClassFilterSubject(classOf[JoinedChannel], classOf[FailedToJoinChannel]))))
      proxy ! Start
      proxy ! JoinChannel("channel")
      expectMsg(JoinedChannel("channel"))
    }

    "notify observers when failed to join channel" in {
      val factory = mock[ChatConnectionFactory]
      val connection = mock[ChatConnection]
      (factory.newConnection _).expects().returns(connection)
      (connection.connectTo _).expects(*)
      (connection.channelMessages _).expects().returns(Observable.empty)
      (connection.privateMessages _).expects().returns(Observable.empty)
      (connection.joinChannel _).expects(*).throws(new RuntimeException())
      val proxy = system.actorOf(Props(new ConnectionObservableProxy(testDesc, factory)))
      proxy ! RegisterObserver(Observer(testActor, Set(ClassFilterSubject(classOf[JoinedChannel], classOf[FailedToJoinChannel]))))
      proxy ! Start
      proxy ! JoinChannel("channel")
      expectMsg(FailedToJoinChannel("channel"))
    }
  }
}
