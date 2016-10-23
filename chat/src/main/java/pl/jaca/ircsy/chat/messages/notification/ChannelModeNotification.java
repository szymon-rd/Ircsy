package pl.jaca.ircsy.chat.messages.notification;

import pl.jaca.ircsy.chat.ServerDesc;
import pl.jaca.ircsy.chat.messages.ChannelNotification;
import pl.jaca.ircsy.chat.messages.ChatUser;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Jaca777
 *         Created 2016-05-13 at 22
 */
public class ChannelModeNotification implements ChannelNotification {

    private ServerDesc serverDesc;
    private LocalDateTime time;
    private String channel;
    private ChatUser user;
    private List<Character> addedModes;
    private List<Character> removedModes;

    public ChannelModeNotification(ServerDesc serverDesc, LocalDateTime time, String channel, ChatUser user, List<Character> addedModes, List<Character> removedModes) {
        this.serverDesc = serverDesc;
        this.time = time;
        this.channel = channel;
        this.user = user;
        this.addedModes = addedModes;
        this.removedModes = removedModes;
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

    public List<Character> getAddedModes() {
        return addedModes;
    }

    public List<Character> getRemovedModes() {
        return removedModes;
    }
}
