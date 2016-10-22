package pl.jaca.ircsy.service.notifications;

import pl.jaca.ircsy.chat.ServerDesc;

import java.time.LocalDate;

/**
 * @author Jaca777
 *         Created 2016-10-22 at 13
 */
public class FailedToDisconnectFromServerNotification implements ApplicationNotification {
    private ServerDesc serverDesc;
    private String nickname;
    private Throwable cause;
    private LocalDate time;

    public FailedToDisconnectFromServerNotification(ServerDesc serverDesc, String nickname, Throwable cause, LocalDate time) {
        this.serverDesc = serverDesc;
        this.nickname = nickname;
        this.cause = cause;
        this.time = time;
    }

    public ServerDesc getServerDesc() {
        return serverDesc;
    }

    public String getNickname() {
        return nickname;
    }

    public Throwable getCause() {
        return cause;
    }

    @Override
    public LocalDate getTime() {
        return time;
    }
}
