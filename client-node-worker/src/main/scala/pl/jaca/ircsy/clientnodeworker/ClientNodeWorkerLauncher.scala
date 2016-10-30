package pl.jaca.ircsy.clientnodeworker

import java.net.InetSocketAddress

import akka.actor.{Actor, Props}
import akka.cluster.Cluster
import pl.jaca.ircsy.util.config.ConfigUtil._

/**
  * @author Jaca777
  *         Created 2016-10-29 at 21
  */
class ClientNodeWorkerLauncher extends Actor {

  val systemConfig = context.system.settings.config

  val cluster = Cluster(context.system)
  val localClusterAddress = cluster.selfAddress
  val mainClusterAddress = localClusterAddress.copy(
    protocol = systemConfig.stringAt("app.cluster.contact-point.address.protocol").getOrElse(localClusterAddress.protocol),
    system = systemConfig.stringAt("app.cluster.contact-point.address.system").getOrElse(localClusterAddress.system),
    host = systemConfig.stringAt("app.cluster.contact-point.address.host").orElse(localClusterAddress.host),
    port = systemConfig.intAt("app.cluster.contact-point.address.port").orElse(localClusterAddress.port)
  )
  cluster.join(mainClusterAddress)

  override def receive: Receive = {
    case _ =>
  }
}
