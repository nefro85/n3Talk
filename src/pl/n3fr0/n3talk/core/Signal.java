package pl.n3fr0.n3talk.core;

import pl.n3fr0.n3talk.core.server.ServerClientIO;

public interface Signal {
    void onSignal(Type type, ServerClientIO scio, Callback callback);
    
    public class Callback<T> {

        public void callback(boolean bool) {
            this.bool = bool;
        }

        public void callback(T object) {
            this.object = object;
        }

        public boolean getBoolean() {
            return bool;
        }

        public T getObject() {
            return object;
        }
        
        private boolean bool = true;
        private T object;
    }

    public enum Type {
        REGISTER, UNREGISTER,
        NOTIFY, AUTH,
        CONNECT, DISCONNECT;
    }
}
