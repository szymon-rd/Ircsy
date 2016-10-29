package pl.jaca.ircsy.clientnode.messagecollection

import akka.actor.SupervisorStrategy.{Restart, Stop}
import akka.actor._
import pl.jaca.ircsy.chat.ConnectionDesc
import pl.jaca.ircsy.clientnode.messagecollection.PrivateMessageCollectorSupervisor.Initialize
import pl.jaca.ircsy.clientnode.messagecollection.repository.MessageRepositoryFactory

import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * @author Jaca777
  *         Created 2016-05-03 at 18
  */
class PrivateMessageCollectorSupervisor extends Actor with ActorLogging {

  log.debug("Starting private message collector supervisor.")

  override def supervisorStrategy: SupervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 20, withinTimeRange = 1 hour, loggingEnabled = true) {
      case _: ActorInitializationException => Stop
      case _: Exception => Restart
    }

  override def receive: Receive = uninitialized

  def uninitialized: Receive = {
    case Initialize(connectionDesc, mediator, repositoryFactory) =>
      log.debug(s"Initializing private message collector ($connectionDesc)...")
      val collector = context.actorOf(Props(new PrivateMessageCollector(connectionDesc, mediator, repositoryFactory)))
      context watch collector
      context become supervising(collector)
  }

  def supervising(collector: ActorRef): Receive = {
    case Terminated(_) =>
      log.debug("Private message collector terminated, stopping supervisor...")
      context.stop(self)
    case any => collector ! any
  }


}

object PrivateMessageCollectorSupervisor {
  case class Initialize(connectionDesc: ConnectionDesc, pubSubMediator: ActorRef, repositoryFactory: MessageRepositoryFactory)
}