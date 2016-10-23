package pl.jaca.ircsy.chat.messages;

import pl.jaca.ircsy.chat.ServerDesc;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Jaca777
 *         Created 2016-05-13 at 22
 */
public class ChannelMessage implements Serializable {

    private ServerDesc serverDesc;
    private String channel;
    private LocalDateTime time;
    private ChatUser author;
    private String text;

    public ChannelMessage(ServerDesc serverDesc, String channel, LocalDateTime time, ChatUser author, String text) {
        this.serverDesc = serverDesc;
        this.channel = channel;
        this.time = time;
        this.author = author;
        this.text = text;
    }

    public ServerDesc getServerDesc() {
        return serverDesc;
    }

    public String getChannel() {
        return channel;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public ChatUser getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }
}
