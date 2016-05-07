package pl.jaca.ircsy.clientnode.connection



import pl.jaca.ircsy.clientnode.connection.messages.{Notification, ChannelMessage, PrivateMessage}
import rx.lang.scala.Observable

/**
  * @author Jaca777
  *         Created 2016-05-01 at 17
  */
trait ChatConnection {
  def connectTo(connectionDesc: ConnectionDesc)
  def disconnect()
  def joinChannel(name: String)
  def leaveChannel(name: String)
  def sendChannelMessage(channel: String, msg: String)
  def sendPrivateMessage(user: String, msg: String)
  def channelMessages: Observable[ChannelMessage]
  def privateMessages: Observable[PrivateMessage]
  def notifications: Observable[Notification]
}