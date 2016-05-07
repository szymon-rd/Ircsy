package pl.jaca.ircsy.clientnode.messagescollection

import akka.actor.ActorSystem
import akka.testkit.{TestKitBase, TestKit}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{Matchers, WordSpec, WordSpecLike}


/**
  * @author Jaca777
  *         Created 2016-05-02 at 12
  */
class ServerMessageCollectorSpec extends {
  implicit val system = ActorSystem("ConnectionProxyRegionCoordinatorSpec")
} with WordSpec with TestKitBase with Matchers with MockFactory {

  "ServerMessageCollector" should {

  }
}
