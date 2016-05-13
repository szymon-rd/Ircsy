package pl.jaca.ircsy.chat.messages.notification;

import pl.jaca.ircsy.chat.messages.ChannelNotification;
import pl.jaca.ircsy.chat.messages.ChatUser;

/**
 * @author Jaca777
 *         Created 2016-05-13 at 22
 */
public class UserKickNotification implements ChannelNotification {
    private String channel;
    private ChatUser kicker;
    private String kickedName;
    private String message;

    public UserKickNotification(String channel, ChatUser kicker, String kickedName, String message) {
        this.channel = channel;
        this.kicker = kicker;
        this.kickedName = kickedName;
        this.message = message;
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
