package pl.n3fr0.n3talk;

import pl.n3fr0.n3talk.message.Message;
import pl.n3fr0.n3talk.message.MessageInbound;
import pl.n3fr0.n3talk.message.MessageOutbound;

public abstract class Facility implements MessageInbound, MessageOutbound {

    public void outboundMessage(Message message) {
        if (messageOutbound != null) {
            messageOutbound.outboundMessage(message);
        }
    }

    public MessageOutbound getMessageOutbound() {
        return messageOutbound;
    }

    public void setMessageOutbound(MessageOutbound messageOutbound) {
        this.messageOutbound = messageOutbound;
    }
    private MessageOutbound messageOutbound;
    protected static final String FACILITY = "Facility";
}
