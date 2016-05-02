package pl.jaca.ircsy.clientnode.listening



import pl.jaca.ircsy.clientnode.listening.ChatConnection.{PrivateMessage, ChannelMessage}
import rx.lang.scala.Observable

/**
  * @author Jaca777
  *         Created 2016-05-01 at 17
  */
abstract class ChatConnection {
  def connectTo(connectionDesc: ChatConnectionDesc)
  def disconnect()
  def joinChannel(name: String)
  def leaveChannel(name: String)
  def sendChannelMessage(channel: String, msg: String)
  def sendPrivateMessage(user: String, msg: String)
  def channelMessages: Observable[ChannelMessage]
  def privateMessages: Observable[PrivateMessage]
}
object ChatConnection {
  case class ChannelMessage(channel: String, msg: String)
  case class PrivateMessage(user: String, msg: String)
}
