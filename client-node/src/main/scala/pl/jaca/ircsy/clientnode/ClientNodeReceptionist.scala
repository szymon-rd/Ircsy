package pl.jaca.ircsy.clientnode

import java.net.InetAddress

import akka.actor.{Actor, ActorRef, Props}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.sharding.ClusterSharding
import pl.jaca.ircsy.chat.ConnectionDesc
import pl.jaca.ircsy.clientnode.ClientNodeReceptionist._
import pl.jaca.ircsy.clientnode.connection.ConnectionObservableProxy._
import pl.jaca.ircsy.clientnode.connection.ConnectionProxyRegionCoordinator
import pl.jaca.ircsy.clientnode.connection.ConnectionProxyRegionCoordinator.{ForwardToProxy, ProxyCoordinatorCmd, StartProxy, StopProxy}
import pl.jaca.ircsy.clientnode.connection.irc.IrcConnectionFactory
import pl.jaca.ircsy.clientnode.messagecollection.ChannelMessageCollectionRegionSupervisor
import pl.jaca.ircsy.clientnode.messagecollection.cassandra.CassandraMessageRepositoryFactory
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

  val contactPoints: Set[InetAddress] = ??? //TODO
  val repositoryFactory = new CassandraMessageRepositoryFactory(contactPoints)
  val messageCollectionCoordinator = context.actorOf(Props(
    new ChannelMessageCollectionRegionSupervisor(sharding, repositoryFactory, mediator))
  )

  override def receive: Actor.Receive = {
    case StartConnection(connection) =>
      proxyCoordinator ! StartProxy(connection)
    case StopConnection(connection) =>
      proxyCoordinator ! StopProxy(connection)
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

  val UserObserverSubject: ObserverSubject = ClassFilterSubject(
    classOf[ChannelMessageReceived],
    classOf[PrivateMessageReceived],
    classOf[NotificationReceived],
    classOf[JoinedChannel]) and FilterSubject {
    case _: ConnectionNotification => true
  }

  case class StartConnection(connection: ConnectionDesc)

  case class StopConnection(connection: ConnectionDesc)

  case class RunConnectionCommand private[ClientNodeReceptionist](connection: ConnectionDesc, cmd: Any)

  object RunConnectionCommand {
    def apply(connection: ConnectionDesc, connectionCmd: ConnectionCmd) = new RunConnectionCommand(connection, connectionCmd)

    def apply(connection: ConnectionDesc, observableCmd: ObserverCmd) = new RunConnectionCommand(connection, observableCmd)
  }

  case class RunCommand(cmd: ProxyCoordinatorCmd)

  case class ObserveUser(connectionDesc: ConnectionDesc, observer: ActorRef)

  case class StopObservingUser(connectionDesc: ConnectionDesc, observer: ActorRef)

}
