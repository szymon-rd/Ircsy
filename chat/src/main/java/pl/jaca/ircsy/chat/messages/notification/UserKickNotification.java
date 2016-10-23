package pl.jaca.ircsy.chat.messages.notification;

import pl.jaca.ircsy.chat.ServerDesc;
import pl.jaca.ircsy.chat.messages.ChannelNotification;
import pl.jaca.ircsy.chat.messages.ChatUser;

import java.time.LocalDateTime;

/**
 * @author Jaca777
 *         Created 2016-05-13 at 22
 */
public class UserKickNotification implements ChannelNotification {

    private ServerDesc serverDesc;
    private LocalDateTime time;
    private String channel;
    private ChatUser kicker;
    private String kickedName;
    private String message;

    public UserKickNotification(ServerDesc serverDesc, LocalDateTime time, String channel, ChatUser kicker, String kickedName, String message) {
        this.serverDesc = serverDesc;
        this.time = time;
        this.channel = channel;
        this.kicker = kicker;
        this.kickedName = kickedName;
        this.message = message;
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
