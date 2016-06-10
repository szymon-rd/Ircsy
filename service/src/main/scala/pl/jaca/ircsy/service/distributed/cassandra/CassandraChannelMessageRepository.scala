package pl.jaca.ircsy.service.distributed.cassandra

import java.time.LocalDate
import java.util

import pl.jaca.ircsy.chat.ServerDesc
import pl.jaca.ircsy.chat.messages.ChannelMessage
import pl.jaca.ircsy.service.{ChannelMessageRepository, ChannelMessageRepositoryFactory}

/**
  * @author Jaca777
  *         Created 2016-06-05 at 13
  */
class CassandraChannelMessageRepository(channelName: String, server: ServerDesc) extends ChannelMessageRepository{
  override def getMessages(from: LocalDate, to: LocalDate): util.List[ChannelMessage] = ???

  override def getLastMessages(count: Int): util.List[ChannelMessage] = ???
}
object CassandraChannelMessageRepository {
  class Factory(channelName: String, server: ServerDesc) extends ChannelMessageRepositoryFactory{
    override def newRepository(): ChannelMessageRepository = new CassandraChannelMessageRepository(channelName, server)
  }
}
