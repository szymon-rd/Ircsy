package pl.jaca.ircsy.service.notifications;

import pl.jaca.ircsy.chat.ConnectionDesc;
import pl.jaca.ircsy.chat.ServerDesc;

import java.time.LocalDate;

/**
 * @author Jaca777
 *         Created 2016-06-10 at 21
 */
public class FailedToJoinChannelNotification implements ApplicationNotification {

    private ConnectionDesc connectionDesc;
    private String channelName;
    private Throwable cause;
    private LocalDate time;

    public FailedToJoinChannelNotification(ConnectionDesc connectionDesc, String channelName, Throwable cause, LocalDate time) {
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
    public LocalDate getTime() {
        return time;
    }
}
