package pl.jaca.ircsy.service.distributed.cassandra

import java.time.LocalDate
import java.util

import pl.jaca.ircsy.chat.PrivateChat
import pl.jaca.ircsy.chat.messages.PrivateMessage
import pl.jaca.ircsy.service.{ChannelMessageRepository, ChannelMessageRepositoryFactory, UserMessageRepository, UserMessageRepositoryFactory}

/**
  * @author Jaca777
  *         Created 2016-06-05 at 13
  */
class CassandraUserMessageRepository(username: String) extends UserMessageRepository {
  override def getPrivateMessages(chat: PrivateChat, from: LocalDate, to: LocalDate): util.List[PrivateMessage] = ???

  override def getLastPrivateMessages(chat: PrivateChat, count: Int): util.List[PrivateMessage] = ???
}

object CassandraUserMessageRepository {

  class Factory(username: String) extends UserMessageRepositoryFactory {
    override def newRepository(): UserMessageRepository = new CassandraUserMessageRepository(username)
  }

}