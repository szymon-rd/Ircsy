package pl.jaca.ircsy.clientnode.connection

import akka.actor.SupervisorStrategy.{Restart, Stop}
import akka.actor._
import akka.util.Timeout
import pl.jaca.ircsy.clientnode.connection.ConnectionProxySupervisor.InitializeConnection

import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * @author Jaca777
  *         Created 2016-05-03 at 20
  */
class ConnectionProxySupervisor extends Actor {

  implicit val executionContext = context.dispatcher

  override def supervisorStrategy: SupervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 20, withinTimeRange = 1 hour, loggingEnabled = true) {
      case _: ActorInitializationException => Stop
      case _: Exception => Restart
    }

  def receive = {
    case InitializeConnection(desc, factory) =>
      val proxy = context.actorOf(Props(new ConnectionObservableProxy(desc, factory)))
      context.watch(proxy)
      context become supervising(proxy)
  }

  def supervising(proxy: ActorRef): Receive = {
    case msg => proxy ! msg
    case Terminated(_) => context.stop(self)
  }

  implicit val timeout = Timeout(2 seconds)
}

object ConnectionProxySupervisor {

  case class InitializeConnection(connectionDesc: ConnectionDesc, connectionFactory: ChatConnectionFactory)

}
