package pl.jaca.ircsy.service.notifications;

import pl.jaca.ircsy.chat.ConnectionDesc;
import pl.jaca.ircsy.chat.ServerDesc;

import java.time.LocalDateTime;

/**
 * @author Jaca777
 *         Created 2016-06-10 at 21
 */
public class JoinedChannelNotification implements ApplicationNotification{
    private ConnectionDesc connectionDesc;
    private String channelName;
    private LocalDateTime time;

    public JoinedChannelNotification(ConnectionDesc connectionDesc, String channelName, LocalDateTime time) {
        this.connectionDesc = connectionDesc;
        this.channelName = channelName;
        this.time = time;
    }

    public ConnectionDesc getConnectionDesc() {
        return connectionDesc;
    }

    public String getChannelName() {
        return channelName;
    }

    @Override
    public LocalDateTime getTime() {
        return time;
    }
}
