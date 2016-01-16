package pl.n3fr0.n3talk;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.n3fr0.n3talk.core.N3TalkIO;
import pl.n3fr0.n3talk.core.RawDataDispatch;
import pl.n3fr0.n3talk.core.Signal;
import pl.n3fr0.n3talk.core.data.RawData;
import pl.n3fr0.n3talk.core.server.ServerClientIO;
import pl.n3fr0.n3talk.message.Assembler;
import pl.n3fr0.n3talk.message.Message;
import pl.n3fr0.n3talk.message.MessageInbound;
import pl.n3fr0.n3talk.message.MessageOutbound;
import pl.n3fr0.n3talk.message.MirrorMessage;
import pl.n3fr0.n3talk.mirror.MirrorContainer;
import pl.n3fr0.n3talk.mirror.MirrorManager;

public class N3Talk {

    public N3Talk() {}
    

    public void addFacility(Facility facility){
        facilities.add(facility);
        facility.setMessageOutbound(controler);
    }
    
    public void removeFacility(Facility facility){
        facilities.remove(facility);
        facility.setMessageOutbound(null);

    }    

    public N3TalkIO getIO() {
        return io;
    }

    public void setIO(N3TalkIO io) {
        this.io = io;
        this.io.addRawDataDispatchListener(controler);
        this.io.setSignal(controler);
    }

    public MirrorContainer createMirrorContainer(){
        final MirrorContainer container = new MirrorContainer();
        final MirrorManager manager = new MirrorManager();
        manager.setContainer(container);
        addFacility(manager);
        return container;
    }

    private class FacilityControler implements RawDataDispatch, MessageInbound,
            MessageOutbound, Signal {

        public void onSignal(Type type, ServerClientIO scio, Callback callback) {
            switch(type) {
                case REGISTER:
                    break;
                case NOTIFY:
                    inbMirrorMessage(signalNotifyMirror(scio));
                    break;
                default: break;
            }
        }

        public void onRawData(RawData data) {
            final Message message = assembler.asm(data);
            if (message != null) {
                onMessageInbound(message);
            }
        }

        public void onMessageInbound(Message message) {
            if (message instanceof MirrorMessage) {
                inbMirrorMessage((MirrorMessage) message);
            }
        }        

        void inbMirrorMessage(MirrorMessage mm){
            for(Facility f : facilities) {
                if (f instanceof MirrorManager) {
                    f.onMessageInbound(mm);
                }
            }
        }

        public void outboundMessage(Message message) {
            assembler.disasm(message);
            try {
                io.send((RawData) message);
            } catch (IOException ex) {
                Logger.getLogger(N3Talk.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        MirrorMessage signalNotifyMirror(ServerClientIO scio) {
            final MirrorMessage msg = new MirrorMessage();
            msg.doClientInitialization();
            msg.setDestination(scio);
            return msg;
        }
    }

    private N3TalkIO io;
    final private List<Facility> facilities = new LinkedList<Facility>();
    final private FacilityControler controler = new FacilityControler();
    final private Assembler assembler = new Assembler();
}
