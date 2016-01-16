package pl.n3fr0.n3talk.core.emitter;

import java.io.IOException;
import java.io.OutputStream;
import pl.n3fr0.n3talk.core.data.RawData;

public class Emitter {

    public Emitter(OutputStream os) {
        this.os = os;
    }
    public void emit(RawData data) throws IOException {
        emit(data, 0);
    }

    public void emit(RawData data, int emitType) throws IOException {
        if (w == null) {
            w = new BWEmitterWriter(os);
        }
        switch (emitType) {
            default:
                emitData(data);
                break;
        }
    }

    private void emitData(final RawData data) throws IOException {
        w.writeProtocol();
        w.writeHeader(data.getHeaders());
        //w.nextSegment();
        if (data.getContent() != null) {
            w.writeContent(data.getContent());
        } else {
            w.nextSegment();
        }
        w.nextSegment();
    }
    private OutputStream os;
    private Writer w;
}
