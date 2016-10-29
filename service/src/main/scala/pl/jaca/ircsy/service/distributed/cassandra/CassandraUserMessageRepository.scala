package pl.jaca.ircsy.service.distributed.cassandra

import java.net.InetAddress
import java.time.{LocalDateTime, ZoneId}
import java.util
import java.util.stream.Collectors
import scala.collection.JavaConverters._

import com.datastax.driver.core.{BoundStatement, _}
import pl.jaca.ircsy.chat.{PrivateChat, ServerDesc}
import pl.jaca.ircsy.chat.messages.{ChannelMessage, ChatUser, PrivateMessage}
import pl.jaca.ircsy.service.{ChannelMessageRepository, ChannelMessageRepositoryFactory, UserMessageRepository, UserMessageRepositoryFactory}
import pl.jaca.ircsy.util.function.FunctionConversions._

/**
  * @author Jaca777
  *         Created 2016-06-05 at 13
  */
class CassandraUserMessageRepository(username: String, contactPoints: Set[InetAddress], keyspace: String = "ircsy",
                                     userMessageTable: String = "user_messages") extends UserMessageRepository {

  val cluster = Cluster.builder()
    .addContactPoints(contactPoints.asJava)
    .build()

  val session = cluster.connect(keyspace)

  val timeRangeUserMessageStatement = {
    val unprepared: RegularStatement =
      new SimpleStatement(s"SELECT * FROM $userMessageTable " +
        s"WHERE server = ? " +
        s"AND mainParticipant = ? " +
        s"AND secondParticipant = ?" +
        s"AND timestamp > ? " +
        s"AND timestamp < ?")
        .setConsistencyLevel(ConsistencyLevel.QUORUM).asInstanceOf[RegularStatement]
    session.prepare(unprepared)
  }

  val lastUserMessageStatement = {
    val unprepared: RegularStatement =
      new SimpleStatement(s"SELECT * FROM $userMessageTable " +
        s"WHERE server = ? " +
        s"AND mainParticipant = ? " +
        s"AND secondParticipant = ?" +
        s"LIMIT ?")
        .setConsistencyLevel(ConsistencyLevel.QUORUM).asInstanceOf[RegularStatement]
    session.prepare(unprepared)
  }

  private def toPrivateMessage(row: Row): PrivateMessage = {
    val host = row.getString("server").split(':')(0)
    val port = row.getString("server").split(':')(1).toInt
    val date: LocalDateTime = toLocalDateTime(row.getTimestamp("timestamp"))
    val chat = new PrivateChat(new ServerDesc(host, port), row.getString("mainParticipant"), row.getString("secondParticipant"))
    val author = new ChatUser(row.getString("author_nick"), row.getString("author_hostname"), row.getString("author_ident"))
    val message = row.getString("message")
    new PrivateMessage(date, chat, author, message)
  }

  private def toLocalDateTime(date: util.Date) = LocalDateTime.ofInstant(date.toInstant, ZoneId.systemDefault())

  override def getPrivateMessages(chat: PrivateChat, from: LocalDateTime, to: LocalDateTime): util.List[PrivateMessage] = {
    val statement = lastUserMessageStatement.bind(chat.getServer, chat.getMainParticipantName, chat.getSecondParticipantName,
      toCassandraDate(from.toString), toCassandraDate(to.toString))
    executeMessageSelectStatement(statement)
  }

  private def toCassandraDate(javaDate: String): String = javaDate.replace('T', ' ')

  override def getLastPrivateMessages(chat: PrivateChat, count: Int): util.List[PrivateMessage] = {
    val statement = lastUserMessageStatement.bind(chat.getServer, chat.getMainParticipantName, chat.getSecondParticipantName, count.toString)
    executeMessageSelectStatement(statement)
  }

  private def executeMessageSelectStatement(statement: BoundStatement): util.List[PrivateMessage]  = {
    session.execute(statement).all()
      .parallelStream()
      .map[PrivateMessage](toJavaFunction(toPrivateMessage))
      .collect(Collectors.toList())
  }
}

object CassandraUserMessageRepository {

  class Factory(contactPoints: Set[InetAddress]) extends UserMessageRepositoryFactory {
    override def newRepository(username: String): UserMessageRepository =
      new CassandraUserMessageRepository(username, contactPoints)
  }

}