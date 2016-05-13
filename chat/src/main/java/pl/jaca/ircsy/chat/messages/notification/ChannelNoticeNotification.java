package pl.jaca.ircsy.chat.messages.notification;

import pl.jaca.ircsy.chat.messages.ChannelNotification;
import pl.jaca.ircsy.chat.messages.ChatUser;

import java.time.LocalDate;

/**
 * @author Jaca777
 *         Created 2016-05-13 at 22
 */
public class ChannelNoticeNotification implements ChannelNotification {

    private LocalDate time;
    private String channel;
    private ChatUser author;
    private String text;

    public ChannelNoticeNotification(LocalDate time, String channel, ChatUser author, String text) {
        this.time = time;
        this.channel = channel;
        this.author = author;
        this.text = text;
    }

    @Override
    public LocalDate getTime() {
        return time;
    }

    @Override
    public String getChannel() {
        return channel;
    }

    public ChatUser getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }
}
