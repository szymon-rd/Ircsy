package pl.jaca.ircsy.chat.messages.notification;

import pl.jaca.ircsy.chat.messages.ChannelNotification;
import pl.jaca.ircsy.chat.messages.ChatUser;

/**
 * @author Jaca777
 *         Created 2016-05-13 at 22
 */
public class ChannelJoinNotification implements ChannelNotification {
    private String channel;
    private ChatUser user;

    public ChannelJoinNotification(String channel, ChatUser user) {
        this.channel = channel;
        this.user = user;
    }

    @Override
    public String getChannel() {
        return channel;
    }

    public ChatUser getUser() {
        return user;
    }
}
