package pl.jaca.ircsy.chat.messages;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author Jaca777
 *         Created 2016-05-13 at 22
 */
public class PrivateMessage implements Serializable {

    private LocalDate time;
    private ChatUser author;
    private String text;

    public PrivateMessage(LocalDate time, ChatUser author, String text) {
        this.time = time;
        this.author = author;
        this.text = text;
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
