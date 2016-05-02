package pl.jaca.ircsy.clientnode.listening

import akka.actor.ActorSystem
import akka.testkit.TestKit
import org.scalatest.WordSpecLike
import org.scalatest.mockito.MockitoSugar
import pl.jaca.ircsy.util.test.MoreMockitoSugar

/**
  * @author Jaca777
  *         Created 2016-05-02 at 12
  */
class ServerConnectionListenerSpec extends TestKit(ActorSystem("ServerConnectionListenerSpec")) with WordSpecLike with MockitoSugar with MoreMockitoSugar {

}