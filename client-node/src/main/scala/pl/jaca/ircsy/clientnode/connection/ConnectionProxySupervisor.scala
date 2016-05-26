package pl.jaca.ircsy.clientnode.connection

import akka.actor.SupervisorStrategy.{Restart, Stop}
import akka.actor._
import akka.cluster.pubsub.DistributedPubSub
import akka.util.Timeout
import pl.jaca.ircsy.clientnode.connection.ConnectionObservableProxy.ConnectionCmd
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
    case Initialize(desc: ConnectionDesc, factory: ChatConnectionFactory, mediator: ActorRef) =>
      log.debug(s"Initializing connection proxy ($desc)...")
      val proxy = context.actorOf(Props(new ConnectionObservableProxy(desc, factory)))
      val publisher = context.actorOf(Props(new ConnectionProxyPublisher(desc, proxy, mediator)))
      context watch proxy
      context watch publisher
      context become supervising(desc, proxy, publisher)
  }

  def supervising(desc: ConnectionDesc, proxy: ActorRef, publisher: ActorRef): Receive = {
    case Terminated(`proxy`) =>
      log.debug("Connection proxy stopped, stopping supervisor...")
      context.stop(publisher)
      context.stop(self)
    case Terminated(`publisher`) =>
      val mediator = DistributedPubSub(context.system).mediator
      val publisher = context.actorOf(Props(new ConnectionProxyPublisher(desc, proxy, mediator)))
      context watch publisher
      context become supervising(desc, proxy, publisher)
    case msg => proxy ! msg
  }

  implicit val timeout = Timeout(2 seconds)
}

object ConnectionProxySupervisor {

  private[clientnode] case class Initialize(desc: ConnectionDesc, factory: ChatConnectionFactory, mediator: ActorRef) extends ConnectionCmd

}
