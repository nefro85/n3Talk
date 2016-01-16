package pl.n3fr0.n3talk.core.collector;

import java.io.IOException;
import java.util.List;
import pl.n3fr0.n3talk.core.data.Header;

public interface Reader {

    boolean dataValid() throws IOException;
    List<Header> readHeader() throws IOException;
    String readContent() throws IOException;
    boolean isEnd() throws IOException;

}
