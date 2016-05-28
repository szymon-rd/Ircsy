package pl.jaca.ircsy.clientnode.messagecollection

import akka.actor.{Actor, ActorLogging, ActorRef, Cancellable}
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe}
import pl.jaca.ircsy.chat.ConnectionDesc
import pl.jaca.ircsy.clientnode.connection.ConnectionProxyPublisher.{FindUserConnection, UserConnectionFound}
import pl.jaca.ircsy.clientnode.connection.{ConnectionObservableProxy, ConnectionProxyPublisher}
import pl.jaca.ircsy.clientnode.messagecollection.repository.MessageRepositoryFactory
import pl.jaca.ircsy.clientnode.observableactor.ObservableActorProtocol.{ClassFilterSubject, Observer, RegisterObserver, UnregisterObserver}

import scala.concurrent.duration.Duration

/**
  * @author Jaca777
  *         Created 2016-05-07 at 21
  */
class PrivateMessageCollector(connectionDesc: ConnectionDesc, pubSubMediator: ActorRef, repositoryFactory: MessageRepositoryFactory) extends Actor with ActorLogging {
  import ConnectionObservableProxy._

  implicit val executionContext = context.dispatcher
  val config = context.system.settings.config
  val broadcastInterval = Duration.fromNanos(config.getDuration("app.collector.broadcast-interval").toNanos)
  val repository = repositoryFactory.newRepository()
  val observer = Observer(self, Set(ClassFilterSubject(classOf[PrivateMessageReceived], classOf[DisconnectedFromServer])))

  log.debug(s"Starting private message collector ($connectionDesc), subscribing to user topic ...")
  pubSubMediator ! Subscribe(s"users-${connectionDesc.getServer}", self)

  override def receive: Receive = lookingForProxy()

  def lookingForProxy(broadcasting: Cancellable): Receive = {
    case UserConnectionFound(`connectionDesc`, proxy) =>
      log.debug(s"Proxy ($connectionDesc) found, starting collection ...")
      proxy ! RegisterObserver(observer)
      context become collecting(proxy)
    case PrivateMessageCollector.Stop =>
      log.debug(s"Stopping private messages collector ($connectionDesc) during looking for proxy...")
      stop()
  }

  def lookingForProxy(): Receive = {
    log.debug(s"Starting looking for proxy: ($connectionDesc)")
    val broadcastMessage = Publish(s"users-${connectionDesc.getServer}", FindUserConnection(connectionDesc))
    val broadcasting = context.system.scheduler.schedule(Duration.Zero, broadcastInterval, pubSubMediator, broadcastMessage)
    lookingForProxy(broadcasting)
  }

  def collecting(proxy: ActorRef): Receive = {
    case PrivateMessageReceived(message) =>
      repository.addPrivateMessage(connectionDesc.getServer, message)
    case DisconnectedFromServer(`connectionDesc`) =>
      context become lookingForProxy()
    case PrivateMessageCollector.Stop =>
      log.debug(s"Stopping private messages collector ($connectionDesc) during collection...")
      proxy ! UnregisterObserver(observer)
      stop()
  }

  def stop() = context.stop(self)

}
object PrivateMessageCollector {
  object Stop
}
