package pl.jaca.ircsy.service.distributed.cassandra

import java.net.InetAddress
import java.time.{LocalDateTime, ZoneId}
import java.util
import java.util.stream.Collectors

import com.datastax.driver.core._
import pl.jaca.ircsy.chat.ServerDesc
import pl.jaca.ircsy.chat.messages.{ChannelMessage, ChatUser}
import pl.jaca.ircsy.clientnode.messagecollection.repository.MessageRepository
import pl.jaca.ircsy.service.{ChannelMessageRepository, ChannelMessageRepositoryFactory}
import pl.jaca.ircsy.util.function.FunctionConversions.toJavaFunction
import sun.util.calendar.BaseCalendar.Date

import scala.collection.JavaConverters._

/**
  * @author Jaca777
  *         Created 2016-06-05 at 13
  */
class CassandraChannelMessageRepository(server: ServerDesc, channelName: String, contactPoints: Set[InetAddress], keyspace: String = "ircsy",
                                        channelMessagesTable: String = "channel_messages") extends ChannelMessageRepository {


  val cluster = Cluster.builder()
    .addContactPoints(contactPoints.asJava)
    .build()

  val session = cluster.connect(keyspace)

  val timeRangeChannelMessageStatement = {
    val unprepared: RegularStatement =
      new SimpleStatement(s"SELECT * FROM $channelMessagesTable " +
        s"WHERE server = '${server.toString}' " +
        s"AND channel = $channelName " +
        s"AND timestamp > ? " +
        s"AND timestamp < ?")
        .setConsistencyLevel(ConsistencyLevel.QUORUM).asInstanceOf[RegularStatement]
    session.prepare(unprepared)
  }

  val lastChannelMessageStatement = {
    val unprepared: RegularStatement =
      new SimpleStatement(s"SELECT * FROM $channelMessagesTable " +
        s"WHERE server = '${server.toString}' " +
        s"AND channel = $channelName " +
        s"LIMIT ?")
        .setConsistencyLevel(ConsistencyLevel.QUORUM).asInstanceOf[RegularStatement]
    session.prepare(unprepared)
  }

  private def toChannelMessage(row: Row): ChannelMessage = {
    val host = row.getString("server").split(':')(0)
    val port = row.getString("server").split(':')(1).toInt
    val channel: String = row.getString("channel")
    val date: LocalDateTime = toLocalDateTime(row.getTimestamp("timestamp"))
    val author = new ChatUser(row.getString("author_nick"), row.getString("author_hostname"), row.getString("author_ident"))
    val message = row.getString("message")
    new ChannelMessage(new ServerDesc(host, port), channel, date, author, message)
  }

  private def toLocalDateTime(date: util.Date) = LocalDateTime.ofInstant(date.toInstant, ZoneId.systemDefault())

  override def getMessages(from: LocalDateTime, to: LocalDateTime): util.List[ChannelMessage] = {
    val statement = timeRangeChannelMessageStatement.bind(toCassandraDate(from.toString), toCassandraDate(to.toString))
    executeMessageSelectStatement(statement)
  }

  private def toCassandraDate(javaDate: String): String = javaDate.replace('T', ' ')

  private def executeMessageSelectStatement(statement: BoundStatement): util.List[ChannelMessage]  = {
    session.execute(statement).all()
      .parallelStream()
      .map(toJavaFunction(toChannelMessage))
      .collect(Collectors.toList())
  }

  override def getLastMessages(count: Int): util.List[ChannelMessage] = {
    val statement = lastChannelMessageStatement.bind(count)
    executeMessageSelectStatement(statement)
  }
}

object CassandraChannelMessageRepository {

  class Factory(contactPoints: Set[InetAddress]) extends ChannelMessageRepositoryFactory {
    override def newRepository(server: ServerDesc, channelName: String): ChannelMessageRepository = new CassandraChannelMessageRepository(server, channelName, contactPoints)
  }

}
