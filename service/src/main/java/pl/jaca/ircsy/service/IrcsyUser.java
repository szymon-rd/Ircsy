package pl.jaca.ircsy.service;

import pl.jaca.ircsy.chat.ServerDesc;
import pl.jaca.ircsy.chat.messages.ChannelMessage;
import pl.jaca.ircsy.chat.messages.Notification;
import pl.jaca.ircsy.chat.messages.PrivateMessage;
import rx.Observable;

import java.util.Set;

/**
 * @author Jaca777
 *         Created 2016-05-27 at 22
 */
public interface IrcsyUser {
    String getName();
    Observable<ChannelMessage> getMessages();
    Observable<PrivateMessage> getPrivateMessages();
    Observable<Notification> getNotifications();
    UserMessageRepository getMessageRepository();
    Set<ServerDesc> getServers();
    Set<String> getChannels(ServerDesc server);
    void joinChannel(ServerDesc server, String channelName);
    void leaveChannel(ServerDesc server, String channelName);
    void sendChannelMessage(ServerDesc server, String channel, String message);
    void sendPrivateMessage(ServerDesc server, String destUserName, String message);
}
