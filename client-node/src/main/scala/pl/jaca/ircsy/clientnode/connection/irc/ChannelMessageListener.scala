package pl.jaca.ircsy.clientnode.connection.irc

import java.time.LocalDate

import com.ircclouds.irc.api.domain.messages.{ChannelPrivMsg, UserPrivMsg}
import com.ircclouds.irc.api.domain.messages.interfaces.IMessage
import com.ircclouds.irc.api.listeners.VariousMessageListenerAdapter
import pl.jaca.ircsy.chat.ServerDesc
import pl.jaca.ircsy.chat.messages.ChannelMessage
import rx.lang.scala.{Observable, Subject}

/**
  * @author Jaca777
  *         Created 2016-05-14 at 10
  */
class ChannelMessageListener(server: ServerDesc, messages: Subject[ChannelMessage]) extends VariousMessageListenerAdapter {
  override def onChannelMessage(aMsg: ChannelPrivMsg): Unit =
    messages.onNext(new ChannelMessage(server, aMsg.getChannelName, LocalDate.now(), new ChatUserAdapter(aMsg.getSource), aMsg.getText))
}
