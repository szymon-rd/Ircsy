package pl.jaca.ircsy.clientnode

import akka.actor.{Actor, ActorRef, Props}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.sharding.ClusterSharding
import pl.jaca.ircsy.chat.ConnectionDesc
import pl.jaca.ircsy.clientnode.ClientNodeReceptionist._
import pl.jaca.ircsy.clientnode.connection.ConnectionObservableProxy.{ChannelMessageReceived, ConnectionCmd, NotificationReceived, PrivateMessageReceived}
import pl.jaca.ircsy.clientnode.connection.ConnectionProxyRegionCoordinator
import pl.jaca.ircsy.clientnode.connection.ConnectionProxyRegionCoordinator.{ForwardToProxy, ProxyCoordinatorCmd}
import pl.jaca.ircsy.clientnode.connection.irc.IrcConnectionFactory
import pl.jaca.ircsy.clientnode.messagecollection.MessageCollectionRegionCoordinator
import pl.jaca.ircsy.clientnode.observableactor.ObservableActorProtocol._
import pl.jaca.ircsy.clientnode.sharding.RegionAwareClusterShardingImpl

/**
  * @author Jaca777
  *         Created 2016-04-30 at 17
  */
class ClientNodeReceptionist extends Actor {

  val sharding = new RegionAwareClusterShardingImpl(ClusterSharding(context.system))

  val mediator = DistributedPubSub(context.system).mediator

  val proxyCoordinator = context.actorOf(Props(new ConnectionProxyRegionCoordinator(sharding, new IrcConnectionFactory, mediator)))

  val messageCollectionCoordinator = context.actorOf(Props(new MessageCollectionRegionCoordinator(sharding, null)))

  override def receive: Actor.Receive = {
    case StartConnection(connection) =>
    case RunConnectionCommand(connection, cmd) =>
      proxyCoordinator ! ForwardToProxy(connection, cmd)
    case RunCommand(cmd: ProxyCoordinatorCmd) =>
      proxyCoordinator ! cmd
    case ObserveUser(connection, observer) =>
      proxyCoordinator ! ForwardToProxy(connection, RegisterObserver(Observer(observer, Set(UserObserverSubject))))
    case StopObservingUser(connection, observer) =>
      proxyCoordinator ! ForwardToProxy(connection, UnregisterObserver(Observer(observer, Set(UserObserverSubject))))
  }

}

object ClientNodeReceptionist {

  val UserObserverSubject = ClassFilterSubject(classOf[ChannelMessageReceived], classOf[PrivateMessageReceived], classOf[NotificationReceived])

  case class StartConnection(connection: ConnectionDesc)
  private[clientnode] case class RunConnectionCommand(connection: ConnectionDesc, cmd: Any)
  object RunConnectionCommand {
    def apply(connection: ConnectionDesc, connectionCmd: ConnectionCmd) = new RunConnectionCommand(connection, connectionCmd)
    def apply(connection: ConnectionDesc, observableCmd: ObservableCmd) = new RunConnectionCommand(connection, observableCmd)
  }
  case class RunCommand(cmd: ProxyCoordinatorCmd)
  case class ObserveUser(connectionDesc: ConnectionDesc, observer: ActorRef)
  case class StopObservingUser(connectionDesc: ConnectionDesc, observer: ActorRef)
}
