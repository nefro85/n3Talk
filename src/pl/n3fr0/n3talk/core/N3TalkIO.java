package pl.n3fr0.n3talk.core;

import java.util.LinkedList;
import java.util.List;
import pl.n3fr0.n3talk.core.data.RawData;
import pl.n3fr0.n3talk.core.server.ServerClientIO;
import pl.n3fr0.n3talk.core.Signal.Type;
import pl.n3fr0.n3talk.core.Signal.Callback;

public abstract class N3TalkIO implements RawDataSend {

    public synchronized void fireRawDataDispatch(final RawData rd) {
        for (RawDataDispatch rdd : rawDataDispatchs) {
            rdd.onRawData(rd);
        }
    }

    public void addRawDataDispatchListener(RawDataDispatch rawDataDispatch) {
        rawDataDispatchs.add(rawDataDispatch);
    }

    public void delRawDataDispatchListener(RawDataDispatch rawDataDispatch) {
        rawDataDispatchs.remove(rawDataDispatch);
    }

    protected void postSignal(Type type, ServerClientIO scio, Callback callback) {
        if (signal != null) {
            signal.onSignal(type, scio, callback);
        }
    }

    public Signal getSignal() {
        return signal;
    }

    public void setSignal(Signal signal) {
        this.signal = signal;
    }
    final private List<RawDataDispatch> rawDataDispatchs = new LinkedList<RawDataDispatch>();
    private Signal signal;
}
