package pl.jaca.ircsy.service.distributed

import pl.jaca.ircsy.chat.ServerDesc
import pl.jaca.ircsy.chat.messages.{ChannelMessage, Notification}
import pl.jaca.ircsy.service.{IrcsyUser, UserMessageRepository}
import rx.Observable

/**
  * @author Jaca777
  *         Created 2016-05-31 at 23
  */
class DistributedIrcsyUser extends IrcsyUser {
  override def getName: String = ???

  override def getMessages: Observable[ChannelMessage] = ???

  override def getNotifications: Observable[Notification] = ???

  override def joinChannel(server: ServerDesc, channelName: String): Unit = ???

  override def joinServer(server: ServerDesc): Unit = ???

  override def sendChannelMessage(server: ServerDesc, channel: String, message: String): Unit = ???

  override def sendPrivateMessage(server: ServerDesc, destUserName: String, message: String): Unit = ???

  override def getrMessageRepository(): UserMessageRepository = ???
}
