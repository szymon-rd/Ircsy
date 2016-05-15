package pl.jaca.ircsy.chat.messages;

import pl.jaca.ircsy.chat.PrivateChat;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author Jaca777
 *         Created 2016-05-13 at 22
 */
public class PrivateMessage implements Serializable {

    private LocalDate time;
    private PrivateChat chat;
    private ChatUser author;
    private String text;

    public PrivateMessage(LocalDate time, PrivateChat chat, ChatUser author, String text) {
        this.time = time;
        this.chat = chat;
        this.author = author;
        this.text = text;
    }

    public LocalDate getTime() {
        return time;
    }

    public PrivateChat getChat() {
        return chat;
    }

    public ChatUser getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }
}
