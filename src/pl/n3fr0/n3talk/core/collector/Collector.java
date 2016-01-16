package pl.n3fr0.n3talk.core.collector;

import java.io.IOException;
import java.io.InputStream;
import pl.n3fr0.n3talk.core.data.RawData;

public class Collector {

    public Collector(InputStream is) {
        this.is = is;
    }

    public boolean  collect() throws IOException {
        return reading();
    }

    public RawData getCollectedData() {
        return data;
    }

    private boolean reading() throws IOException {
        if (r == null) {
            r = new BRCollectorReader(is);
        }
        if (r.dataValid()) {
            data = new RawData();
            data.setHeaders(r.readHeader());
            data.setContent(r.readContent());
            return r.isEnd();

        } else {
            return false;
        }
    }

    private RawData data;
    private InputStream is;
    private Reader r;

}
