package pl.jaca.ircsy.service.notifications;

import pl.jaca.ircsy.chat.ServerDesc;

import java.time.LocalDate;

/**
 * @author Jaca777
 *         Created 2016-06-18 at 00
 */
public class FailedToSendChannelMessageNotification implements ApplicationNotification {
    private ServerDesc server;
    private String channelName;
    private String message;
    private Throwable cause;
    private LocalDate time;

    public FailedToSendChannelMessageNotification(LocalDate time, ServerDesc server, String channelName, String message, Throwable cause) {
        this.server = server;
        this.channelName = channelName;
        this.message = message;
        this.cause = cause;
        this.time = time;
    }

    public ServerDesc getServer() {
        return server;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getMessage() {
        return message;
    }

    public Throwable getCause() {
        return cause;
    }

    public LocalDate getTime() {
        return time;
    }
}
