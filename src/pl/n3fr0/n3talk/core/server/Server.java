/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.n3fr0.n3talk.core.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.n3fr0.n3talk.core.N3TalkIO;
import pl.n3fr0.n3talk.core.Signal.Callback;
import pl.n3fr0.n3talk.core.Signal.Type;
import pl.n3fr0.n3talk.core.collector.Collector;
import pl.n3fr0.n3talk.core.data.RawData;
import pl.n3fr0.n3talk.core.data.RawEntity.Orgin;
import pl.n3fr0.n3talk.core.emitter.Emitter;

/**
 *
 * @author nefro
 */
public class Server extends N3TalkIO {

    public Server() {
    }

    public void start() {
        if (connecter == null) {
            connecter = new Connecter();
        }
        connecter.start();
    }

    public void stop() {
        connecter.interrupt();
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public synchronized void send(RawData data) throws IOException {
        if (!data.hasDestination()) {
            for (Map.Entry<String, ServerClientIO> e : clients.entrySet()) {
                try {
                    if (!data.thisIsSource(e.getValue())) {
                        final Callback callback = new Callback();
                        postSignal(Type.AUTH, e.getValue(), callback);
                        if (callback.getBoolean()) {
                            e.getValue().send(data);
                        }
                    }
                } catch (IOException ex) {
                    throw new UnsupportedOperationException("something is wrong" + ex.getMessage());
                }
            }
        } else {
            clients.get(data.getDestination().getUID()).send(data);
        }
    }

    private void registerClient(ServerClientIO clientIO) {
        clients.put(clientIO.getUID(), clientIO);
    }

    private synchronized String genUID(){
        return "UID#" + ++nbUID;
    }

    private class Connecter extends Thread {

        public Connecter() {
            super("CONNECTER ACCEPTER");
        }

        @Override
        public void run() {
            try {

                serverSocket = new ServerSocket(port);
                while (true) {

                    final Socket socket = serverSocket.accept();
                    if (onlineClients < SERVER_CLIENT_LIMIT) {
                        final Thread sc = new ServerClient(socket);
                        sc.setDaemon(true);
                        sc.start();
                        onlineClients++;
                    } else {
                        socket.close();
                    }

                }
            } catch (IOException e) {
                System.err.println(e);
            }
        }
        ServerSocket serverSocket;
        volatile int onlineClients;
    }

    private class ServerClient extends Thread implements ServerClientIO {

        public ServerClient(Socket socket) {
            this.socket = socket;
            uid = genUID();
            setName("SERVER CLIENT: "+ uid);

            registerClient(this);
            postSignal(Type.REGISTER, this, null); //Do it thread: connection accepter
        }

        private void init() throws IOException {            
            c = new Collector(socket.getInputStream());
            e = new Emitter(socket.getOutputStream());
            postSignal(Type.NOTIFY, this, null); //Do it thread: server client
        }

        @Override
        public void run() {
            try {
                init();
                while (true) {
                    if (c.collect()) {
                        final RawData rawData = c.getCollectedData();
                        rawData.setOrgin(Orgin.SLAVE);
                        rawData.setSource(this);
                        fireRawDataDispatch(rawData);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                clean();               
            }
        }

        public String getUID() {
            return uid;
        }

        public void send(RawData data) throws IOException {
            e.emit(data);
        }

        private void clean(){
            try {
                socket.close();
                clients.remove(uid);
                connecter.onlineClients --;
                postSignal(Type.UNREGISTER, this, null);
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }            
        }

        
        private final String uid;
        private final Socket socket;
        private Emitter e;
        private Collector c;
    }

    private int nbUID;
    Connecter connecter;
    Map<String, ServerClientIO> clients = new LinkedHashMap<String, ServerClientIO>();
    private int port = DEFAULT_PORT;
    private static final int DEFAULT_PORT = 9666;
    private static final int SERVER_CLIENT_LIMIT = 5;
}
