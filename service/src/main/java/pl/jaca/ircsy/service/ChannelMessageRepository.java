package pl.jaca.ircsy.service;

import pl.jaca.ircsy.chat.messages.ChannelMessage;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Jaca777
 *         Created 2016-05-28 at 16
 */
public interface ChannelMessageRepository {
    List<ChannelMessage> getMessages(LocalDateTime from, LocalDateTime to);
    List<ChannelMessage> getLastMessages(int count);
}
