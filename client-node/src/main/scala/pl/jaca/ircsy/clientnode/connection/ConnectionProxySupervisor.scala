package pl.jaca.ircsy.clientnode.connection

import akka.actor.SupervisorStrategy.{Restart, Stop}
import akka.actor._
import akka.util.Timeout
import pl.jaca.ircsy.clientnode.connection.ConnectionProxySupervisor.Initialize

import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * @author Jaca777
  *         Created 2016-05-03 at 20
  */
class ConnectionProxySupervisor extends Actor with ActorLogging {

  log.debug("Starting connection proxy supervisor...")

  implicit val executionContext = context.dispatcher

  override def supervisorStrategy: SupervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 20, withinTimeRange = 1 hour, loggingEnabled = true) {
      case _: ActorInitializationException => Stop
      case _: Exception => Restart
    }

  def receive = {
    case Initialize(desc, factory) =>
      log.debug(s"Initializing connection proxy ($desc)...")
      val proxy = context.actorOf(Props(new ConnectionObservableProxy(desc, factory)))
      context watch proxy
      context become supervising(proxy)
  }

  def supervising(proxy: ActorRef): Receive = {
    case Terminated(_) =>
      log.debug("Connection proxy stopped, stopping supervisor...")
      context.stop(self)
    case msg => proxy ! msg
  }

  implicit val timeout = Timeout(2 seconds)
}

object ConnectionProxySupervisor {

  case class Initialize(connectionDesc: ConnectionDesc, connectionFactory: ChatConnectionFactory)

}
