package pl.jaca.ircsy.clientnode.connection.irc


import com.ircclouds.irc.api
import com.ircclouds.irc.api.IRCApiImpl
import com.ircclouds.irc.api.domain.IRCChannel
import com.ircclouds.irc.api.state.IIRCState
import pl.jaca.ircsy.chat.messages.{ChannelMessage, Notification, PrivateMessage}
import pl.jaca.ircsy.clientnode.connection.{ChatConnection, ConnectionDesc}
import rx.lang.scala.{Subject, Observable}
import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.Try


/**
  * @author Jaca777
  *         Created 2016-05-02 at 13
  *         Non blocking.
  */
class IrcConnection(executionContext: ExecutionContext) extends ChatConnection {

  implicit val ec = executionContext

  override val notifications = Subject[Notification]()
  override val privateMessages = Subject[PrivateMessage]()
  override val channelMessages = Subject[ChannelMessage]()
  val notificationListener = new NotificationListener(notifications)
  val privateMessageListener = new PrivateMessageListener(privateMessages)
  val channelMessageListener = new ChannelMessageListener(channelMessages)
  val irc = new IRCApiImpl(true)
  irc.addListener(notificationListener)
  irc.addListener(privateMessageListener)
  irc.addListener(channelMessageListener)

  override def connectTo(connectionDesc: ConnectionDesc, timeout: Duration): Future[Unit] =
    toFuture((irc.connect _).curried(new IrcServerParameterAdapter(connectionDesc)))


  override def disconnect() {
    irc.disconnect()
  }

  override def sendPrivateMessage(user: String, msg: String): Future[Unit] =
    toFuture((irc.message(_: String, _: String, _: api.Callback[String])).curried(user)(msg))

  override def joinChannel(name: String): Future[Unit] =
    toFuture((irc.joinChannel (_: String, _: api.Callback[IRCChannel])).curried(name))


  override def sendChannelMessage(channel: String, msg: String): Future[Unit] =
    toFuture((irc.message (_: String, _: String, _: api.Callback[String])).curried(channel)(msg))

  override def leaveChannel(name: String): Future[Unit] =
    toFuture((irc.leaveChannel (_: String, _: api.Callback[String])).curried(name))

  private def toFuture[T](fun: (api.Callback[T]) => Unit): Future[Unit] = {
    val callback = new IrcCallback[T]
    fun(callback)
    callback.future.map(_ => ())
  }
}
