package pl.jaca.ircsy.irc.cluster

import akka.actor.Actor.Receive
import akka.actor.{Props, Actor, ActorSystem, ActorRef}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{MemberLeft, MemberEvent, MemberUp}
import pl.jaca.ircsy.util.config.ConfigUtil
import ConfigUtil._
/**
  * @author Jaca777
  *         Created 2016-04-30 at 16
  */
class ServerNode extends Actor {
  val systemConfig = context.system.settings.config

  val cluster = Cluster(context.system)

  cluster.subscribe(self, classOf[MemberUp], classOf[MemberLeft])

  override def receive: Receive = {
    case MemberUp =>
    case MemberLeft =>
  }
}

object ServerNode {
  def runServerNode(): ActorRef = {
    val actorSystem = ActorSystem("irc-client")
    actorSystem.actorOf(Props[ServerNode])
  }
}