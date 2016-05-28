package pl.jaca.ircsy.service.distributed

import pl.jaca.ircsy.chat.ServerDesc
import pl.jaca.ircsy.service.{ChannelMessageRepository, ChatService, IrcsyUser, UserMessageRepository}

/**
  * @author Jaca777
  *         Created 2016-05-28 at 17
  */
class DistributedChatService extends ChatService{
  override def getUser(name: String): IrcsyUser = ???

  override def createUser(name: String): IrcsyUser = ???

  override def getUserMessageRepository(userName: String): UserMessageRepository = ???

  override def getChannelMessageRepository(server: ServerDesc, channelName: String): ChannelMessageRepository = ???
}
