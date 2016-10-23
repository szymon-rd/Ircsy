package pl.jaca.ircsy.service.notifications;

import pl.jaca.ircsy.chat.ConnectionDesc;

import java.time.LocalDateTime;

/**
 * @author Jaca777
 *         Created 2016-06-10 at 21
 */
public class FailedToJoinChannelNotification implements ApplicationNotification {

    private ConnectionDesc connectionDesc;
    private String channelName;
    private Throwable cause;
    private LocalDateTime time;

    public FailedToJoinChannelNotification(ConnectionDesc connectionDesc, String channelName, Throwable cause, LocalDateTime time) {
        this.connectionDesc = connectionDesc;
        this.channelName = channelName;
        this.cause = cause;
        this.time = time;
    }

    public ConnectionDesc getConnectionDesc() {
        return connectionDesc;
    }

    public String getChannelName() {
        return channelName;
    }

    public Throwable getCause() {
        return cause;
    }

    @Override
    public LocalDateTime getTime() {
        return time;
    }
}
