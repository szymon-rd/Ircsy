package pl.jaca.ircsy.chat.messages.notification;

import pl.jaca.ircsy.chat.messages.Notification;

import java.time.LocalDate;

/**
 * @author Jaca777
 *         Created 2016-05-13 at 22
 */
public class ErrorNotification implements Notification {

    private LocalDate time;
    private String error;

    public ErrorNotification(LocalDate time, String error) {
        this.time = time;
        this.error = error;
    }

    public LocalDate getTime() {
        return time;
    }

    public String getError() {
        return error;
    }
}

