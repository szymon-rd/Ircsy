package pl.jaca.ircsy.chat.messages;

import pl.jaca.ircsy.chat.PrivateChat;
import pl.jaca.ircsy.chat.ServerDesc;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author Jaca777
 *         Created 2016-05-13 at 22
 */
public class PrivateMessage implements Serializable {

    private ServerDesc server;
    private LocalDate time;
    private PrivateChat chat;
    private ChatUser author;
    private String text;

    public PrivateMessage(ServerDesc server, LocalDate time, PrivateChat chat, ChatUser author, String text) {
        this.server = server;
        this.time = time;
        this.chat = chat;
        this.author = author;
        this.text = text;
    }

    public ServerDesc getServer() {
        return server;
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
