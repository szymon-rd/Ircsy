package pl.jaca.ircsy.service;

import pl.jaca.ircsy.chat.ServerDesc;
import pl.jaca.ircsy.chat.messages.ChannelMessage;
import pl.jaca.ircsy.chat.messages.Notification;
import rx.Observable;

/**
 * @author Jaca777
 *         Created 2016-05-27 at 22
 */
public interface IrcsyUser {
    String getName();
    Observable<ChannelMessage> getMessages();
    Observable<Notification> getNotifications();
    void joinChannel(ServerDesc server, String channelName);
    void joinServer(ServerDesc server);
    void sendChannelMessage(ServerDesc server, String channel, String message);
    void sendPrivateMessage(ServerDesc server, String destUserName, String message);
}
