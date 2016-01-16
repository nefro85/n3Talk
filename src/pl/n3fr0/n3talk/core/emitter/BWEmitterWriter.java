package pl.n3fr0.n3talk.core.emitter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import pl.n3fr0.n3talk.core.data.Header;
import pl.n3fr0.n3talk.core.data.Protocol;
import pl.n3fr0.n3talk.core.data.RawData;

public class BWEmitterWriter implements Writer {

    public BWEmitterWriter(OutputStream outputStream) {
        bw = new BufferedWriter(new OutputStreamWriter(outputStream));
    }

    public void writeProtocol() throws IOException {
        bw.write(Protocol.HEAD);
        bw.write(RawData.SEPARATOR);
        bw.flush();
    }

    public void nextSegment() throws IOException {
        bw.write(RawData.SEPARATOR);
        bw.flush();
    }

    public void writeContent(String content) throws IOException {
        bw.write(content);
        nextSegment();
    }

    public void writeHeader(List<Header> headers) throws IOException {
        StringBuffer sb = new StringBuffer();
        for(Header h : headers){
            sb.append(h.getRawHeader());
        }
        bw.write(sb.toString());
        nextSegment();
    }

    BufferedWriter bw;

}
