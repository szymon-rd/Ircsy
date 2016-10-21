package pl.jaca.ircsy.service.notifications;

import pl.jaca.ircsy.chat.ServerDesc;

import java.time.LocalDate;

/**
 * @author Jaca777
 *         Created 2016-06-18 at 00
 */
public class FailedToSendPrivateMessageNotification implements ApplicationNotification {
    private ServerDesc server;
    private String destUserName;
    private String message;
    private Throwable cause;
    private LocalDate time;

    public FailedToSendPrivateMessageNotification(LocalDate time, ServerDesc server, String destUserName, String message, Throwable cause) {
        this.server = server;
        this.destUserName = destUserName;
        this.message = message;
        this.cause = cause;
        this.time = time;
    }

    public ServerDesc getServer() {
        return server;
    }

    public String getDestUserName() {
        return destUserName;
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
