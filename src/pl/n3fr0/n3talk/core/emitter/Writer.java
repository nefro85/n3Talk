package pl.n3fr0.n3talk.core.emitter;

import java.io.IOException;
import java.util.List;
import pl.n3fr0.n3talk.core.data.Header;

public interface Writer {

    void writeProtocol() throws IOException;
    void nextSegment() throws IOException;
    void writeHeader(List<Header> headers) throws IOException;
    void writeContent(String content) throws IOException;


}
