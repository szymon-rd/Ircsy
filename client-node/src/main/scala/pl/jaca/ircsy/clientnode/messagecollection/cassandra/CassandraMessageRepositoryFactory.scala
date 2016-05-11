package pl.jaca.ircsy.clientnode.messagecollection.cassandra

import java.net.InetAddress

import pl.jaca.ircsy.clientnode.messagecollection.repository.{MessageRepository, MessageRepositoryFactory}

/**
  * @author Jaca777
  *         Created 2016-05-10 at 19
  */
class CassandraMessageRepositoryFactory(contactPoints: Set[InetAddress], keyspace: String, tableName: String) extends MessageRepositoryFactory {
  override def newRepository(): MessageRepository = new CassandraMessageRepository(contactPoints, keyspace, tableName)
}
