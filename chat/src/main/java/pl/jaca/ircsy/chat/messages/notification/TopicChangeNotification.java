package pl.jaca.ircsy.chat.messages.notification;

import pl.jaca.ircsy.chat.ChannelTopic;
import pl.jaca.ircsy.chat.messages.ChannelNotification;
import pl.jaca.ircsy.chat.messages.ChatUser;

/**
 * @author Jaca777
 *         Created 2016-05-13 at 22
 */
public class TopicChangeNotification implements ChannelNotification {
    private String channel;
    private ChatUser user;
    private ChannelTopic newTopic;

    public TopicChangeNotification(String channel, ChatUser user, ChannelTopic newTopic) {
        this.channel = channel;
        this.user = user;
        this.newTopic = newTopic;
    }

    @Override
    public String getChannel() {
        return channel;
    }

    public ChatUser getUser() {
        return user;
    }

    public ChannelTopic getNewTopic() {
        return newTopic;
    }
}
