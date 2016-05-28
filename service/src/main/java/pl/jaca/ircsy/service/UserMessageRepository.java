package pl.jaca.ircsy.service;

import pl.jaca.ircsy.chat.PrivateChat;
import pl.jaca.ircsy.chat.messages.Notification;
import pl.jaca.ircsy.chat.messages.PrivateMessage;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Jaca777
 *         Created 2016-05-28 at 16
 */
public interface UserMessageRepository {
    List<PrivateMessage> getPrivateMessages(PrivateChat chat, LocalDate from, LocalDate to);
    List<PrivateMessage> getLastPrivateMessages(PrivateChat chat, int count);
}
