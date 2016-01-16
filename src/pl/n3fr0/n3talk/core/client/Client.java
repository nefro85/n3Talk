package pl.n3fr0.n3talk.core.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.n3fr0.n3talk.core.N3TalkIO;
import pl.n3fr0.n3talk.core.Signal.Type;
import pl.n3fr0.n3talk.core.collector.Collector;
import pl.n3fr0.n3talk.core.data.RawData;
import pl.n3fr0.n3talk.core.data.RawEntity.Orgin;
import pl.n3fr0.n3talk.core.emitter.Emitter;

public class Client extends N3TalkIO {

    public Client() {
        gateSend = new Semaphore(1);
        gateSend.tryAcquire();

        gateDataSet = new Semaphore(1);
    }

    public void connect(String server, int port) {
        if (socket == null) {
            socket = new Socket();
        }

        if (!socket.isConnected()) {
            try {
                socket.connect(new InetSocketAddress(server, port));
                init();
                if (events != null) {
                    events.onConnect(null);
                }
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void disconnect() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void send(RawData rd) throws IOException {
        if (tw != null) {
            tw.putRawData(rd);
        } else {
            throw new IOException("Client NOT CONNECTED TO FRAMEWORK!");
        }
    }

    public RawData request(RawData rq) {
        return null;
    }

    private void init() {
        cont = new Controler();
        //cont.start();
        cont.run();
        
        rw = new RxWorker(socket);
        rw.start();

        tw = new TxWorker(socket);
        tw.start();
    }

    public ClientEvents getEvents() {
        return events;
    }

    public void setEvents(ClientEvents events) {
        this.events = events;
    }

    private class TxWorker extends Thread {

        public TxWorker(Socket socket) {
            super("EMITTER WORKER");
            s = socket;
        }

        void putRawData(RawData data){
            try {
                //XXX do queue
                gateDataSet.acquire();
                this.data = data;
                gateSend.release();
            } catch (InterruptedException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void run() {
            try {
                e = new Emitter(s.getOutputStream());
                while (true) {
                    gateSend.acquire();
                    e.emit(data);
                    gateDataSet.release();
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        Socket s;
        Emitter e;
        private volatile RawData data;
    }

    private class RxWorker extends Thread {

        public RxWorker(Socket socket) {
            super("COLLECTOR WORKER");
            s = socket;
        }

        @Override
        public void run() {
            try {
                c = new Collector(s.getInputStream());

                while (true) {
                    if (c.collect()) {
                        cont.newData(c.getCollectedData());
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Collector c;
        Socket s;
    }

    private class Controler extends Thread {

        public Controler() {
            super("CLIENT CONTROLER");
        }

        void newData(RawData data){
            data.setOrgin(Orgin.MASTER);
            fireRawDataDispatch(data);
        }

        private void init() {
            postSignal(Type.CONNECT, null, null);
        }

        @Override
        public void run() {
            this.init();
            while(true) {
                //things to do...
                break;
            }
        }
    }
    
    private Semaphore gateSend;
    private Semaphore gateDataSet;
    private TxWorker tw;
    private RxWorker rw;
    private Controler cont;
    private Socket socket;
    private ClientEvents events;
}
