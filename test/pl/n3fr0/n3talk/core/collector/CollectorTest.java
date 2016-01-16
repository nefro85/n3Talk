/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.n3fr0.n3talk.core.collector;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import pl.n3fr0.n3talk.core.data.Header;
import static org.junit.Assert.*;
import pl.n3fr0.n3talk.core.data.RawData;
import pl.n3fr0.n3talk.core.emitter.Emitter;

/**
 *
 * @author nefro
 */
public class CollectorTest {

    public CollectorTest() {
    }

    @Ignore @Test
    public void test001() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        baos.write(new Header("test", "test111123123").getRawHeader().getBytes());
        baos.write("\n\n".getBytes());
        baos.write("\n\n".getBytes());

        final Collector collector = new Collector(new ByteArrayInputStream(baos.toByteArray()));
        collector.collect();
    }

    @Test
    public void test002() throws IOException {

        RawData rd = new RawData();
        List<Header> hs = new ArrayList<Header>();
        hs.add(new Header("a", "a"));
        hs.add(new Header("b", "b"));
        rd.setHeaders(hs);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Emitter emitter = new Emitter(baos);
        emitter.emit(rd);

        System.out.println(baos.size());
        byte[] b = baos.toByteArray();
        System.out.println(new String(baos.toByteArray()));
    }

}