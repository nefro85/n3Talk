package pl.n3fr0.n3talk.core;

import java.io.IOException;
import pl.n3fr0.n3talk.core.data.RawData;

public interface RawDataSend {
    void send(RawData data) throws IOException;
}