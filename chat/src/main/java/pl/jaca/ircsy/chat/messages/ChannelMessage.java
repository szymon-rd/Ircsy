package pl.jaca.ircsy.chat.messages;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author Jaca777
 *         Created 2016-05-13 at 22
 */
public class ChannelMessage implements Serializable {

    private String channel;
    private LocalDate time;
    private ChatUser author;
    private String text;

    public ChannelMessage(String channel, LocalDate time, ChatUser author, String text) {
        this.channel = channel;
        this.time = time;
        this.author = author;
        this.text = text;
    }

    public String getChannel() {
        return channel;
    }

    public LocalDate getTime() {
        return time;
    }

    public ChatUser getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }
}
