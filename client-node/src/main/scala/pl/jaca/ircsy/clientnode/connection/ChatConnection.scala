package pl.jaca.ircsy.clientnode.connection


import pl.jaca.ircsy.chat.ConnectionDesc
import pl.jaca.ircsy.chat.messages.{ChannelMessage, Notification, PrivateMessage}
import rx.lang.scala.Observable

import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.util.Try

/**
  * @author Jaca777
  *         Created 2016-05-01 at 17
  */
trait ChatConnection {
  def connectTo(connectionDesc: ConnectionDesc, timeout: Duration): Future[Unit]
  def disconnect()
  def joinChannel(name: String): Future[Unit]
  def leaveChannel(name: String): Future[Unit]
  def sendChannelMessage(channel: String, msg: String): Future[Unit]
  def sendPrivateMessage(user: String, msg: String): Future[Unit]
  def channelMessages: Observable[ChannelMessage]
  def privateMessages: Observable[PrivateMessage]
  def notifications: Observable[Notification]
}