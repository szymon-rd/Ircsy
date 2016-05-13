package pl.jaca.ircsy.chat.messages.notification;

import pl.jaca.ircsy.chat.messages.ChannelMessage;
import pl.jaca.ircsy.chat.messages.ChannelNotification;
import pl.jaca.ircsy.chat.messages.ChatUser;

import java.time.LocalDate;

/**
 * @author Jaca777
 *         Created 2016-05-13 at 22
 */
public class PartNotification implements ChannelNotification {

    private LocalDate time;
    private String channel;
    private ChatUser user;
    private String message;

    public PartNotification(LocalDate time, String channel, ChatUser user, String message) {
        this.time = time;
        this.channel = channel;
        this.user = user;
        this.message = message;
    }

    @Override
    public LocalDate getTime() {
        return time;
    }

    @Override
    public String getChannel() {
        return channel;
    }

    public ChatUser getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }
}
