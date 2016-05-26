package pl.jaca.ircsy.clientnode.connection

import akka.actor.{ActorSystem, ExtendedActorSystem, Props}
import akka.testkit.{TestKit, TestKitBase, TestKitExtension, TestProbe}
import org.scalamock.scalatest.MockFactory
import org.scalatest._
import pl.jaca.ircsy.clientnode.connection.ConnectionObservableProxy._
import pl.jaca.ircsy.clientnode.connection.ConnectionProxySupervisor.Initialize
import rx.lang.scala.Observable

import scala.language.postfixOps

/**
  * @author Jaca777
  *         Created 2016-05-02 at 12
  */
class ConnectionProxySupervisorSpec extends {
  implicit val system = ActorSystem("ConnectionProxySupervisorSpec")
} with WordSpec with TestKitBase with Matchers with OneInstancePerTest with MockFactory {

  val testDesc: ConnectionDesc = ConnectionDesc(ServerDesc("foo", 42), "bar")

  "ConnectionProxySupervisor" should {
    "start a proxy" in {
      val factory = mock[ChatConnectionFactory]
      val connection = mock[ChatConnection]
      val mediator = TestProbe()
      (factory.newConnection _).expects(*).returns(connection)
      (connection.connectTo _).expects(testDesc, *)
      (connection.channelMessages _).expects().returns(Observable.empty)
      (connection.privateMessages _).expects().returns(Observable.empty)
      val supervisor = system.actorOf(Props(new ConnectionProxySupervisor))
      supervisor ! Initialize(testDesc, factory, mediator.ref)
      supervisor ! Start
      Thread.sleep(200)
    }
  }
}
