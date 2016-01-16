package pl.n3fr0.n3talk.core.data;

import java.util.List;

public class RawData extends RawEntity {

    public RawData() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean hasContent() {
        return content != null;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public void setHeaders(List<Header> headers) {
        this.headers = headers;
    }

    public void addHeader(Header header) {
        headers.add(header);
    }

    public boolean isHeaderEqual(String name, String value) {
        if (headers != null && headers.size() > 0) {
            for (Header h : headers) {
                if (h.getName().equals(name) && h.getValue().equals(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isHeader(Header header) {
        return  headers.contains(header);
    }

    @Override
    public void dispose(){
        super.dispose();
        content = null;
        headers = null;
    }

    private List<Header> headers;
    String content;

    public static final String SEPARATOR = "\n";
}
