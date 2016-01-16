package pl.n3fr0.n3talk.mirror.entity;

import java.util.LinkedList;
import java.util.List;
import org.junit.Test;
import pl.n3fr0.n3talk.N3Talk;
import pl.n3fr0.n3talk.core.client.Client;
import pl.n3fr0.n3talk.core.server.Server;
import pl.n3fr0.n3talk.mirror.MessageQueue;
import pl.n3fr0.n3talk.mirror.MirrorContainer;
import pl.n3fr0.n3talk.mirror.MirrorManager;
import pl.n3fr0.n3talk.mirror.proxy.MirrorMatrix;

public class MirrorBaseList {

    @Test
    public void test001() throws InterruptedException {
        Server server = new Server();
        server.start();

        Thread.sleep(100);

        MirrorContainer clientContainer = setupClient();

        N3Talk n3Talk = new N3Talk();
        n3Talk.setIO(server);

        MirrorManager manager = new MirrorManager();
        n3Talk.addFacility(manager);

        MirrorContainer container = new MirrorContainer();
        manager.setContainer(container);

        MessageQueue messageQueue = container.startQueueing();

        //proxy ;)
        MirrorMatrix matrix = new MirrorMatrix();
        IItem item = (IItem) matrix.createMirror(new Item("proxy"));
        container.add(item);
        item.setName("black");

        Box box = new Box();

        List<Item> items = new LinkedList<Item>();
        items.add(new Item("red"));
        items.add(new Item("blue"));
        items.add(new Item("gren"));

        box.setItems(items);
        
        container.add(box);        

        box.addItem(new Item("pink"));
        Item gray = new Item("gray");
        box.addItem(gray);
        
        Drawers drawers = new Drawers();
        container.add(drawers);
        drawers.addBox(new Integer(1), box);

        messageQueue.dispatch();
        messageQueue.close();

        Thread.sleep(1000);

        printContainerItems(clientContainer);

        System.out.println("Removing gray...");
        box.removeItem(gray);
        Thread.sleep(1000);

        printContainerItems(clientContainer);
        
    }

    void printContainerItems(MirrorContainer container) {
        int nbr = 0;
        for (MirrorBase b : container.getObjects()) {
            System.out.print("[Entity #" + ++nbr + "]");
            if (b instanceof Box) {
                System.out.println("box with items count: " + ((Box)b).getItems().size());
                for (Item i : ((Box)b).getItems()) {
                    System.out.println(" - box item: " + i.name);
                }
            } else if (b instanceof Item) {
                System.out.println(((Item)b).getName());
            } else {
                System.out.println(b.getClass().getName());
            }
        }
    }

    public MirrorContainer setupClient(){
        Client client = new Client();
        N3Talk n3Talk = new N3Talk();
        n3Talk.setIO(client);

        MirrorManager manager = new MirrorManager();
        MirrorContainer container = new MirrorContainer();
        manager.setContainer(container);

        n3Talk.addFacility(manager);

        client.connect("127.0.0.1", 9666);

        return container;
    }

}
