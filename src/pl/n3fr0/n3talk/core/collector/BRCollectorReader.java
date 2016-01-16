package pl.n3fr0.n3talk.core.collector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import pl.n3fr0.n3talk.core.data.Header;
import pl.n3fr0.n3talk.core.data.Protocol;
import pl.n3fr0.n3talk.core.data.RawData;

public class BRCollectorReader implements Reader {

    public BRCollectorReader(InputStream inputStream) {
        br = new BufferedReader(new InputStreamReader(inputStream));
    }

    public boolean dataValid() throws IOException {
        rs.readRaw(true);
        return rs.type != RawSType.CORRUPTED;
    }

    public String readContent() throws IOException {
        String str = null;
        if (rs.isContnet()) {
            str = rs.rawLine;
        }
        return str;
    }

    public List<Header> readHeader() throws IOException {
        final List<Header> h = new LinkedList<Header>();
        while (rs.isHeader()) {
            String[] hrs = rs.rawLine.split(Header.HSEP);
            if (hrs.length >= 2) {
                h.add(new Header(hrs));
            }
        }
        return h;
    }

    public boolean isEnd() throws IOException {
        rs.readRaw();
        boolean res = rs.type == RawSType.END;
        if (res) {
            rs.reset();
        }
        return res;
    }
    BufferedReader br;
    final RawSegment rs = new RawSegment();

    private class RawSegment {

        public RawSegment() {
            reset();
        }

        void readRaw() throws IOException {
            readRaw(false);
        }

        synchronized void readRaw(boolean readOnce) throws IOException {
            do {
                // readProtocol at start have true value, if first line of data don't
                // have the Protocol.HEAD data is corrupt
                if (!readProtocol && type == RawSType.PROTOCOL) {
                    type = RawSType.HEADER;
                }

                if (type != RawSType.CORRUPTED
                        &&(rawLine = br.readLine()) != null
                        && rawLine.isEmpty()
                        && !readProtocol) {
                    
                    nextSegment();
                    return;
                }

                if (rawLine == null) {
                    //type = RawSType.END;
                    throw new IOException("n3Talk: CONNECTION ERROR");
                }

                if (readProtocol) {

                    //safe read...
                    if (type == RawSType.CORRUPTED) {
                        buff = new char[Protocol.HEAD.length()];
                        byte unsafeRead = 0;
                        while (true) {
                            br.read(buff);
                            if (Arrays.equals(buff, Protocol.HEAD.toCharArray())) {
                                final StringBuffer sbuff = new StringBuffer();
                                sbuff.append(buff);

                                char[] cbuff = new char[1];
                                while (br.read(cbuff) != 0) {
                                    if (cbuff[0] != RawData.SEPARATOR.charAt(0)) {
                                        sbuff.append(cbuff);
                                    } else {
                                        break;
                                    }
                                }
                                
                                rawLine = sbuff.toString();
                                buff = null;
                                type = RawSType.PROTOCOL;
                                break;
                            } else {
                                if (++unsafeRead > 3) {
                                    throw new IOException("n3Talk: UNSAFE READ");
                                }
                            }
                        }
                    }
                    if (rawLine.startsWith(Protocol.HEAD)) {
                        readProtocol = false;
                    } else {
                        type = RawSType.CORRUPTED;
                    }
                }
            } while ((readProtocol || (!readProtocol && type == RawSType.PROTOCOL)) && !readOnce);
        }

        void nextSegment() {
            switch (type) {
                case HEADER:
                    type = RawSType.CONTENT;
                    break;
                case CONTENT:
                    type = RawSType.END;
                    break;
                default:
                    break;
            }

        }

        boolean isHeader() throws IOException {
            readRaw();
            if (type == RawSType.HEADER) {
                return true;
            }
            return false;
        }

        private boolean isContnet() throws IOException {
            readRaw();
            if (type == RawSType.CONTENT) {
                return true;
            }
            return false;
        }

        private void reset() {
            rawLine = null;
            type = RawSType.PROTOCOL;
            readProtocol = true;
        }
        String rawLine;
        RawSType type;
        boolean readProtocol;
        char[] buff;
    }

    private enum RawSType {

        PROTOCOL, HEADER, CONTENT, END, CORRUPTED;
    }
}
