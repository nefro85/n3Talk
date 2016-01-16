package pl.n3fr0.n3talk.mirror.entity;

@Mirror
public class PlainJObject extends MirrorBase {

    public PlainJObject() {
    }
    
    public PlainJObject(Object object) {
        if (object instanceof Integer) {
            type = INTEGER;
            integer = (Integer) object;
        }
    }

    public PlainJObject(Integer type) {
        this.type = type;
    }

    public Object getValue() {
        if (type.equals(INTEGER)) {
            return integer;
            
        } else if (type.equals(STRING)) {
            return string;

        } else{
            return null;
        }
    }

    public void quietDetach() {
        if (isMirrored()) {
            container.remove(getObjectUID());
            setObjectUID(null);
            manager = null;
            container = null;
        }
    }

    public Integer getInteger() {
        return integer;
    }

    public void setInteger(Integer integer) {
       setProperty(integer);
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        setProperty(string);
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Property
    public Integer integer;
    @Property
    public String string;
    @Property
    private Integer type;

    public static final Integer INTEGER = new Integer(1);
    public static final Integer STRING = new Integer(2);
}
