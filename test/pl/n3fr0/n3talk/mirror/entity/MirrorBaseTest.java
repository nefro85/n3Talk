/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.n3fr0.n3talk.mirror.entity;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import pl.n3fr0.n3talk.mirror.MirrorManager;
import static org.junit.Assert.*;

/**
 *
 * @author Administrator
 */
public class MirrorBaseTest {

    public MirrorBaseTest() {
    }

    @Test
    public void test001(){

        MirrorManager manager = new MirrorManager();
        MirrorBaseImpl m = new MirrorBaseImpl();
        m.setManager(manager);
        m.setObjectUID("sada");
        m.boom();
        m.detach();
    }

    public class MirrorBaseImpl extends MirrorBase {
        void boom(){
            notifyMirror();
        }
    }

}