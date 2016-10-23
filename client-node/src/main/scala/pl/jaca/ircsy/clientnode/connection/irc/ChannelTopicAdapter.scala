package pl.jaca.ircsy.clientnode.connection.irc

import java.time.{ZoneId, LocalDateTime}

import com.ircclouds.irc.api.domain.{IRCTopic, WritableIRCTopic}
import pl.jaca.ircsy.chat.ChannelTopic

/**
  * @author Jaca777
  *         Created 2016-05-13 at 23
  */
class ChannelTopicAdapter(topic: IRCTopic)
  extends ChannelTopic(topic.getDate.toInstant.atZone(ZoneId.systemDefault()).toLocalDateTime, topic.getSetBy, topic.getValue)
