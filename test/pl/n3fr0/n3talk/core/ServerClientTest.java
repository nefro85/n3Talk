/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.n3fr0.n3talk.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import pl.n3fr0.n3talk.core.client.Client;
import pl.n3fr0.n3talk.core.data.Header;
import pl.n3fr0.n3talk.core.data.RawData;
import pl.n3fr0.n3talk.core.server.Server;

/**
 *
 * @author Administrator
 */
public class ServerClientTest {

    @Test
    public void test001() throws InterruptedException, IOException{
        Server server = new Server();
        server.start();

        Thread.sleep(500);

        Client client = new Client();
        client.connect("127.0.0.1", 9666);

        RawData rd = new RawData();
        List<Header> hs = new ArrayList<Header>();
        hs.add(new Header("header1", "val1"));
        hs.add(new Header("header2", "var2"));
        rd.setContent("super content");
        rd.setHeaders(hs);
        client.send(rd);


        Thread.sleep(5 * 60 * 1000);
    }

}
