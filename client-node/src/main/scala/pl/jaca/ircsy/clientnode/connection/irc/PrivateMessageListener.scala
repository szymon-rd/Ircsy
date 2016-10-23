package pl.jaca.ircsy.clientnode.connection.irc

import java.time.LocalDateTime

import com.ircclouds.irc.api.domain.messages.UserPrivMsg
import com.ircclouds.irc.api.listeners.VariousMessageListenerAdapter
import pl.jaca.ircsy.chat.{PrivateChat, ServerDesc}
import pl.jaca.ircsy.chat.messages.PrivateMessage
import rx.lang.scala.Subject

/**
  * @author Jaca777
  *         Created 2016-05-14 at 10
  */
class PrivateMessageListener(server: ServerDesc, messages: Subject[PrivateMessage]) extends VariousMessageListenerAdapter {
  override def onUserPrivMessage(msg: UserPrivMsg): Unit = {
    val chat = new PrivateChat(msg.getToUser, msg.getSource.getNick)
    messages.onNext(new PrivateMessage(server, LocalDateTime.now(), chat, new ChatUserAdapter(msg.getSource), msg.getText))
  }
}
