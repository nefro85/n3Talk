package pl.n3fr0.n3talk.mirror.entity;

import java.util.LinkedHashMap;
import java.util.Map;

@Mirror
public class Drawers extends MirrorBase {

    public Drawers() {
    }

    @Adder(property = "drawers")
    public void addBox(Integer idx, Box box) {
        putToMap(idx, box, drawers);
    }

    public Map<Integer, Box> getDrawers() {
        return drawers;
    }

    public void setDrawers(Map<Integer, Box> drawers) {
        setProperty(drawers);
    }
    
    @Property
    private Map<Integer, Box> drawers = new LinkedHashMap<Integer, Box>();
}
