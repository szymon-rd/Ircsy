package pl.jaca.ircsy.clientnode.messagecollection

import akka.actor.{Cancellable, Actor, ActorRef}
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe}
import akka.persistence.PersistentActor
import pl.jaca.ircsy.clientnode.connection.ConnectionDesc
import pl.jaca.ircsy.clientnode.connection.ConnectionObservableProxy._
import pl.jaca.ircsy.clientnode.messagecollection.ConnectionActivityObserver.{FindUserConnection, UserConnectionFound, FindChannelConnection, ChannelConnectionFound}
import pl.jaca.ircsy.clientnode.messagecollection.PrivateMessageCollector.Stop
import pl.jaca.ircsy.clientnode.messagecollection.repository.MessageRepositoryFactory
import pl.jaca.ircsy.clientnode.observableactor.ObservableActorProtocol.{ClassFilterSubject, UnregisterObserver, RegisterObserver, Observer}

import scala.concurrent.duration.Duration

/**
  * @author Jaca777
  *         Created 2016-05-07 at 21
  */
class PrivateMessageCollector(connectionDesc: ConnectionDesc, pubSubMediator: ActorRef, repositoryFactory: MessageRepositoryFactory) extends Actor{

  implicit val executionContext = context.dispatcher
  val config = context.system.settings.config
  val broadcastInterval = Duration.fromNanos(config.getDuration("app.collector.broadcast-interval").toNanos)
  val repository = repositoryFactory.newRepository()
  val observer = Observer(self, Set(ClassFilterSubject(classOf[PrivateMessageReceived], classOf[DisconnectedFromServer])))

  pubSubMediator ! Subscribe(s"users-${connectionDesc.serverDesc}", self)

  override def receive: Receive = lookingForProxy()

  def lookingForProxy(broadcasting: Cancellable): Receive = {
    case UserConnectionFound(`connectionDesc`, proxy) =>
      proxy ! RegisterObserver(observer)
      context become collecting(proxy)
    case Stop =>
      stop()
  }

  def lookingForProxy(): Receive = {
    val broadcastMessage = Publish(s"users-${connectionDesc.serverDesc}", FindUserConnection(connectionDesc))
    val broadcasting = context.system.scheduler.schedule(Duration.Zero, broadcastInterval, pubSubMediator, broadcastMessage)
    lookingForProxy(broadcasting)
  }

  def collecting(proxy: ActorRef): Receive = {
    case PrivateMessageReceived(message) =>
      repository.addPrivateMessage(connectionDesc.serverDesc, message)
    case DisconnectedFromServer(`connectionDesc`) =>
      context become lookingForProxy()
    case Stop =>
      proxy ! UnregisterObserver(observer)
      stop()
  }

  def stop() = {
    context.stop(self)
  }
}
object PrivateMessageCollector {
  object Stop
}
