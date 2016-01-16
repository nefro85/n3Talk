package pl.n3fr0.n3talk.core.data;

public class Header {

    public Header(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public Header(String[] sa) {
        this(sa[0], sa[1]);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRawHeader(){
        StringBuffer sb = new StringBuffer();
        sb.append(name);
        sb.append(HSEP);
        sb.append(value);
        sb.append("\n");

        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Header other = (Header) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.value == null) ? (other.value != null) : !this.value.equals(other.value)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 37 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }



    String name;
    String value;

    public static final String HSEP = ": ";

}
