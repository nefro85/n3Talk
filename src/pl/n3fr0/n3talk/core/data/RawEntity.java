package pl.n3fr0.n3talk.core.data;

import pl.n3fr0.n3talk.core.server.ServerClientIO;

public abstract class RawEntity {

    public ServerClientIO getSource() {
        return source;
    }

    public void setSource(ServerClientIO source) {
        this.source = source;
    }

    public boolean hasSource() {
        return source != null;
    }

    public boolean thisIsSource(ServerClientIO scio) {
        boolean result = false;
        if (isFeedback() && scio != null && hasSource() && getSource().getUID().equals(scio.getUID())) {
            result = true;
        }
        return result;
    }

    public boolean isFeedback() {
        return feedback;
    }

    public void setFeedback(boolean feedback) {
        this.feedback = feedback;
    }

    public ServerClientIO getDestination() {
        return destination;
    }

    public void setDestination(ServerClientIO destination) {
        this.destination = destination;
    }

    public boolean hasDestination() {
        return destination != null;
    }

    public Orgin getOrgin() {
        return orgin;
    }

    public void setOrgin(Orgin orgin) {
        this.orgin = orgin;
    }

    public void dispose() {
        source = null;
        destination = null;
        orgin = null;
    }

    public enum Orgin {
        MASTER, SLAVE;
    }
    private ServerClientIO source;
    private ServerClientIO destination;
    private boolean feedback;
    private Orgin orgin;
}
