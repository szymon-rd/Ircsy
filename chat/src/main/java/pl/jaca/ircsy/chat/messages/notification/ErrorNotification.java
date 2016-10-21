package pl.jaca.ircsy.chat.messages.notification;

import pl.jaca.ircsy.chat.ServerDesc;
import pl.jaca.ircsy.chat.messages.Notification;

import java.time.LocalDate;

/**
 * @author Jaca777
 *         Created 2016-05-13 at 22
 */
public class ErrorNotification implements Notification {

    private ServerDesc serverDesc;
    private LocalDate time;
    private String error;

    public ErrorNotification(ServerDesc serverDesc, LocalDate time, String error) {
        this.serverDesc = serverDesc;
        this.time = time;
        this.error = error;
    }

    public ServerDesc getServerDesc() {
        return serverDesc;
    }

    @Override
    public LocalDate getTime() {
        return time;
    }

    public String getError() {
        return error;
    }
}

