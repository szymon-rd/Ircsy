package pl.jaca.ircsy.chat.messages.notification;

import pl.jaca.ircsy.chat.ServerDesc;
import pl.jaca.ircsy.chat.messages.Notification;

import java.time.LocalDateTime;

/**
 * @author Jaca777
 *         Created 2016-05-13 at 22
 */
public class ErrorNotification implements Notification {

    private ServerDesc serverDesc;
    private LocalDateTime time;
    private String error;

    public ErrorNotification(ServerDesc serverDesc, LocalDateTime time, String error) {
        this.serverDesc = serverDesc;
        this.time = time;
        this.error = error;
    }

    public ServerDesc getServerDesc() {
        return serverDesc;
    }

    @Override
    public LocalDateTime getTime() {
        return time;
    }

    public String getError() {
        return error;
    }
}

