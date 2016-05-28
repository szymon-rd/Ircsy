package pl.jaca.ircsy.chat;

import java.util.Objects;

/**
 * @author Jaca777
 *         Created 2016-05-28 at 16
 */
public class ServerDesc {
    private String host;
    private int port;

    protected ServerDesc(){

    }

    public ServerDesc(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return host + ":" + port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerDesc that = (ServerDesc) o;
        return getPort() == that.getPort() &&
                Objects.equals(getHost(), that.getHost());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHost(), getPort());
    }
}

