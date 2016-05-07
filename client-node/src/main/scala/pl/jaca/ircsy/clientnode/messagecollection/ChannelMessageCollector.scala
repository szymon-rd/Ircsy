package pl.jaca.ircsy.clientnode.messagecollection

import akka.actor.Actor.Receive
import akka.actor.{Cancellable, Actor, ActorRef}
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe}
import akka.persistence.PersistentActor
import org.scalatest.path
import pl.jaca.ircsy.clientnode.connection.ConnectionObservableProxy.{ChannelSubject, ChannelMessageReceived, LeftChannel}
import pl.jaca.ircsy.clientnode.connection.ServerDesc
import pl.jaca.ircsy.clientnode.messagecollection.ChannelMessageCollector.Stop
import pl.jaca.ircsy.clientnode.messagecollection.ConnectionActivityObserver.{ChannelConnectionFound, FindChannelConnection, FindUserConnection}
import pl.jaca.ircsy.clientnode.messagecollection.repository.MessageRepositoryFactory
import pl.jaca.ircsy.clientnode.observableactor.ObservableActorProtocol.{UnregisterObserver, Observer, RegisterObserver}
import pl.jaca.ircsy.util.config.ConfigUtil.Configuration

import scala.concurrent.duration.{FiniteDuration, Duration}

/**
  * @author Jaca777
  *         Created 2016-05-01 at 23
  */
class ChannelMessageCollector(serverDesc: ServerDesc, channelName: String, pubSubMediator: ActorRef, repositoryFactory: MessageRepositoryFactory) extends Actor {

  implicit val executionContext = context.dispatcher
  val config = context.system.settings.config
  val broadcastInterval = Duration.fromNanos(config.getDuration("app.collector.broadcast-interval").toNanos)
  val repository = repositoryFactory.newRepository()
  val observer = Observer(self, Set(ChannelSubject(channelName)))

  pubSubMediator ! Subscribe(s"channels-$serverDesc", self)

  override def receive: Receive = lookingForProxy()

  def lookingForProxy(broadcasting: Cancellable): Receive = {
    case ChannelConnectionFound(`serverDesc`, `channelName`, proxy) =>
      proxy ! RegisterObserver(observer)
      context become collecting(proxy)
    case Stop =>
      stop()
  }

  def lookingForProxy(): Receive = {
    val broadcastMessage = Publish(s"channels-$serverDesc", FindChannelConnection(serverDesc, channelName))
    val broadcasting = context.system.scheduler.schedule(Duration.Zero, broadcastInterval, pubSubMediator, broadcastMessage)
    lookingForProxy(broadcasting)
  }

  def collecting(proxy: ActorRef): Receive = {
    case ChannelMessageReceived(message) =>
      repository.addChannelMessage(serverDesc, message)
    case LeftChannel => context become lookingForProxy()
    case Stop =>
      proxy ! UnregisterObserver(observer)
      stop()
  }

  def stop() = {
    context.stop(self)
  }

}
object ChannelMessageCollector {
  object Stop
}

