package pl.jaca.ircsy.chat.messages.notification;

import pl.jaca.ircsy.chat.messages.ChannelNotification;
import pl.jaca.ircsy.chat.messages.ChatUser;

import java.time.LocalDate;

/**
 * @author Jaca777
 *         Created 2016-05-13 at 22
 */
public class UserKickNotification implements ChannelNotification {

    private LocalDate time;
    private String channel;
    private ChatUser kicker;
    private String kickedName;
    private String message;

    public UserKickNotification(LocalDate time, String channel, ChatUser kicker, String kickedName, String message) {
        this.time = time;
        this.channel = channel;
        this.kicker = kicker;
        this.kickedName = kickedName;
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

    public ChatUser getKicker() {
        return kicker;
    }

    public String getKickedName() {
        return kickedName;
    }

    public String getMessage() {
        return message;
    }
}
