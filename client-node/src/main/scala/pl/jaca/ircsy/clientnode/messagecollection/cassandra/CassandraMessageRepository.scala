package pl.jaca.ircsy.clientnode.messagecollection.cassandra

import java.net.InetAddress

import com.datastax.driver.core.{Cluster, ConsistencyLevel, RegularStatement, SimpleStatement}
import pl.jaca.ircsy.clientnode.connection.messages.{ChannelMessage, PrivateMessage}
import pl.jaca.ircsy.clientnode.connection.{ConnectionDesc, ServerDesc}
import pl.jaca.ircsy.clientnode.messagecollection.repository.MessageRepository

import scala.collection.JavaConverters._
import scala.util.Try

/**
  * @author Jaca777
  *         Created 2016-05-10 at 19
  */
class CassandraMessageRepository(contactPoints: Set[InetAddress],
                                 keyspace: String,
                                 channelMessagesTable: String = "channel_essages",
                                 privateMessagesTable: String = "private_messages") extends MessageRepository {

  val cluster = Cluster.builder()
    .addContactPoints(contactPoints.asJava)
    .build()

  val session = cluster.connect(keyspace)

  val channelMessageStatement = {
    val unprepared: RegularStatement =
      new SimpleStatement(s"INSERT INTO $channelMessagesTable VALUES (?, ?, ?, ?, ?, uuid())").setConsistencyLevel(ConsistencyLevel.QUORUM).asInstanceOf[RegularStatement]
    session.prepare(unprepared)
  }

  override def addChannelMessage(server: ServerDesc, message: ChannelMessage): Try[Unit] = Try {
    val statement = channelMessageStatement.bind(server.toString, message.channelName, message.time, message.author, message.message)
    session.execute(statement)
  }

  val privateMessageStatement = {
    val unprepared: RegularStatement =
      new SimpleStatement(s"INSERT INTO $privateMessagesTable VALUES (?, ?, ?, ?, ?, ?, uuid())").setConsistencyLevel(ConsistencyLevel.QUORUM).asInstanceOf[RegularStatement]
    session.prepare(unprepared)
  }

  override def addPrivateMessage(server: ServerDesc, message: PrivateMessage): Try[Unit] = Try {
    val statement = privateMessageStatement.bind(server.toString, message.firstParticipant, message.secondParticipant, message.time, message.sender, message.message)
    session.execute(statement)
  }

  override def close() {
    session.close()
  }
}
