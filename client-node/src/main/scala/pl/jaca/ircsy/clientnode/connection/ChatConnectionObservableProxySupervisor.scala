package pl.jaca.ircsy.clientnode.connection

import java.io.IOException

import akka.actor.SupervisorStrategy.{Stop, Restart}
import akka.actor._
import akka.actor.Actor.Receive
import scala.concurrent.duration._

import pl.jaca.ircsy.clientnode.connection.ChatConnectionObservableProxySupervisor.Initialize

import scala.language.postfixOps

/**
  * @author Jaca777
  *         Created 2016-05-03 at 20
  */
class ChatConnectionObservableProxySupervisor extends Actor {


  override def supervisorStrategy: SupervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 20, withinTimeRange = 1 hour, loggingEnabled = true) {
      case _: ActorInitializationException => Stop
      case _: Exception => Restart
    }

  override def receive: Receive = {
    case Initialize(desc, factory) =>
      val proxy = context.actorOf(Props(new ChatConnectionObservableProxy(desc, factory)))
      context.watch(proxy)
      context become supervising(proxy)
  }

  def supervising(proxy: ActorRef): Receive = {
    case msg => proxy ! msg
    case Terminated(_) => //TODO what?
  }
}

object ChatConnectionObservableProxySupervisor {

  case class Initialize(connectionDesc: ChatConnectionDesc, connectionFactory: ChatConnectionFactory)

}
