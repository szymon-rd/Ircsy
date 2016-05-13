package pl.jaca.ircsy.chat.messages;

import pl.jaca.ircsy.chat.PrivateChat;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author Jaca777
 *         Created 2016-05-13 at 22
 */
public class PrivateMessage implements Serializable {

    private PrivateChat chat;
    private LocalDate time;
    private ChatUser author;
    private String text;

    public PrivateMessage(PrivateChat chat, LocalDate time, ChatUser author, String text) {
        this.chat = chat;
        this.time = time;
        this.author = author;
        this.text = text;
    }

    public PrivateChat getChat() {
        return chat;
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
