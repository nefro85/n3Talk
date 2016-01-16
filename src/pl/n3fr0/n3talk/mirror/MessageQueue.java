package pl.n3fr0.n3talk.mirror;

import java.util.LinkedList;
import java.util.Queue;
import pl.n3fr0.n3talk.message.MirrorMessage;

public class MessageQueue {

    public MessageQueue() {
        msgQueue = new LinkedList<MirrorMessage>();
    }

    public boolean add(MirrorMessage e) {

        return msgQueue.add(e);
    }

    public void dispatch() {
        final MirrorMessage message = msgQueue.poll();
        MirrorMessage msg2Join = null;
        while ((msg2Join = msgQueue.poll()) != null) {
            message.joinCommands(msg2Join);
            msg2Join.clearCommands();
            msg2Join.dispose();
        }
        container.manager.outboundMessage(message);
    }

    public void abort() {
        msgQueue.clear();
    }

    public Queue<MirrorMessage> getQueue() {
        return msgQueue;
    }

    public void close() {
        container.messageQueue = null;
        container = null;
        msgQueue.clear();
    }
    MirrorContainer container;
    final private Queue<MirrorMessage> msgQueue;
}
