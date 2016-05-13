package pl.jaca.ircsy.clientnode.connection.irc

import java.time.LocalDate

import com.ircclouds.irc.api.domain.IRCUser
import com.ircclouds.irc.api.domain.messages._
import com.ircclouds.irc.api.domain.messages.interfaces.IMessage
import com.ircclouds.irc.api.listeners.{VariousMessageListenerAdapter, IVariousMessageListener, IMessageListener}
import pl.jaca.ircsy.chat.messages.Notification
import pl.jaca.ircsy.chat.messages.notification._
import scala.collection.JavaConverters._

import rx.lang.scala.{Subject, Observable}

/**
  * @author Jaca777
  *         Created 2016-05-13 at 18
  */
class NotificationListener(notifications: Subject[Notification]) extends VariousMessageListenerAdapter {
  override def onChannelPart(msg: ChanPartMessage): Unit =
    notifications.onNext(new PartNotification(LocalDate.now(), msg.getChannelName, new ChatUserAdapter(msg.getSource), msg.getPartMsg))

  override def onError(msg: ErrorMessage): Unit =
    notifications.onNext(new ErrorNotification(LocalDate.now(), msg.getText))

  override def onServerNotice(msg: ServerNotice): Unit =
    notifications.onNext(new ServerNoticeNotification(LocalDate.now(), msg.getText))

  override def onUserQuit(msg: QuitMessage): Unit =
    notifications.onNext(new UserQuitNotification(LocalDate.now(), new ChatUserAdapter(msg.getSource), msg.getQuitMsg))

  override def onChannelKick(msg: ChannelKick): Unit =
    notifications.onNext(new UserKickNotification(LocalDate.now(), msg.getChannelName, new ChatUserAdapter(msg.getSource), msg.getKickedNickname, msg.getText))

  override def onServerPing(msg: ServerPing): Unit =
    notifications.onNext(new ServerPingNotification(LocalDate.now(), msg.getText))

  override def onChannelJoin(msg: ChanJoinMessage): Unit =
    notifications.onNext(new ChannelJoinNotification(LocalDate.now(), msg.getChannelName, new ChatUserAdapter(msg.getSource)))

  override def onChannelNotice(msg: ChannelNotice): Unit =
    notifications.onNext(new ChannelNoticeNotification(LocalDate.now(), msg.getChannelName, new ChatUserAdapter(msg.getSource), msg.getText))

  override def onTopicChange(msg: TopicMessage): Unit =
    notifications.onNext(new TopicChangeNotification(LocalDate.now(), msg.getChannelName, new ChatUserAdapter(msg.getSource), new ChannelTopicAdapter(msg.getTopic)))

  override def onNickChange(msg: NickMessage): Unit =
    notifications.onNext(new NickChangeNotification(LocalDate.now(), new ChatUserAdapter(msg.getSource), msg.getSource.getNick, msg.getNewNick))

  override def onUserNotice(msg: UserNotice): Unit =
    notifications.onNext(new UserNoticeNotification(LocalDate.now(), new ChatUserAdapter(msg.getSource), msg.getText))

  override def onChannelMode(msg: ChannelModeMessage): Unit = {
    val user = new ChatUserAdapter(msg.getSource.asInstanceOf[IRCUser])
    val addedModes = msg.getAddedModes.asScala.map(_.getChannelModeType).asJava
    val removedModes = msg.getRemovedModes.asScala.map(_.getChannelModeType).asJava
    notifications.onNext(new ChannelModeNotification(LocalDate.now(), msg.getChannelName, user, addedModes, removedModes))
  }
}
