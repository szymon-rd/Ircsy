package pl.jaca.ircsy.chat.messages.notification;

import pl.jaca.ircsy.chat.ChannelTopic;
import pl.jaca.ircsy.chat.ServerDesc;
import pl.jaca.ircsy.chat.messages.ChannelNotification;
import pl.jaca.ircsy.chat.messages.ChatUser;

import java.time.LocalDateTime;

/**
 * @author Jaca777
 *         Created 2016-05-13 at 22
 */
public class TopicChangeNotification implements ChannelNotification {

    private ServerDesc serverDesc;
    private LocalDateTime time;
    private String channel;
    private ChatUser user;
    private ChannelTopic newTopic;

    public TopicChangeNotification(ServerDesc serverDesc, LocalDateTime time, String channel, ChatUser user, ChannelTopic newTopic) {
        this.serverDesc = serverDesc;
        this.time = time;
        this.channel = channel;
        this.user = user;
        this.newTopic = newTopic;
    }

    public ServerDesc getServerDesc() {
        return serverDesc;
    }

    @Override
    public LocalDateTime getTime() {
        return time;
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
