package pl.n3fr0.n3Talk.showcase.mirrorobjects;

import pl.n3fr0.n3talk.mirror.entity.Mirror;
import pl.n3fr0.n3talk.mirror.entity.MirrorBase;
import pl.n3fr0.n3talk.mirror.entity.Property;

@Mirror
public class Rectangle extends MirrorBase {

    public Rectangle() {
    }
    


    public Integer getSizeX() {
        return sizeX;
    }

    public void setSizeX(Integer sizeX) {
        setProperty(sizeX);
    }

    public Integer getSizeY() {
        return sizeY;
    }

    public void setSizeY(Integer sizeY) {
        setProperty(sizeY);
    }

    @Property
    private Integer sizeX;
    @Property
    private Integer sizeY;
}
