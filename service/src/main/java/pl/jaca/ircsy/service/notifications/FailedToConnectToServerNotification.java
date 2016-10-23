package pl.jaca.ircsy.service.notifications;

import pl.jaca.ircsy.chat.ServerDesc;

import java.time.LocalDateTime;

/**
 * @author Jaca777
 *         Created 2016-06-10 at 20
 */
public class FailedToConnectToServerNotification implements ApplicationNotification{
    private ServerDesc serverDesc;
    private String nickname;
    private Throwable cause;
    private LocalDateTime time;

    public FailedToConnectToServerNotification(ServerDesc serverDesc, String nickname, Throwable cause, LocalDateTime time) {
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
    public LocalDateTime getTime() {
        return time;
    }
}
