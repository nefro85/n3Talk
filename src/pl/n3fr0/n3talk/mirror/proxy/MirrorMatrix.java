package pl.n3fr0.n3talk.mirror.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.n3fr0.n3talk.mirror.entity.MirrorBase;

public class MirrorMatrix {

    public Object createMirror(Object mirrorObj) {
        final ProxyMirror proxyMirror = new ProxyMirror(mirrorObj);
        final Object o = Proxy.newProxyInstance(
                mirrorObj.getClass().getClassLoader(),
                mirrorObj.getClass().getInterfaces(),
                proxyMirror);
        return o;
    }

    public MirrorBase getMirrorObject(Object mirrorObj){
        if (Proxy.isProxyClass(mirrorObj.getClass())) {
            InvocationHandler ih = Proxy.getInvocationHandler(mirrorObj);
            if (ih instanceof MirrorBase) {
                return (MirrorBase) ih;
            }
        }
        return null;
    }

    private class ProxyMirror extends MirrorBase implements InvocationHandler{

        public ProxyMirror(Object object) {
            super(object);
            base = object;
        }



        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            final Method m = base.getClass().getDeclaredMethod(method.getName(),new Class[]{args[0].getClass()});

            final Object o = m.invoke(base, args);
            
            final String property = propertyName(m.getName());

            final Notifier notifier = new Notifier(args[0].hashCode(), property, this);
            notifier.start();

            return o;
        }

        final Object base;

    }

    private class Notifier extends Thread {

        public Notifier(int targetHash, String property, MirrorBase base) {
            super("NOTIFIER");
            this.property = property;
            this.target = targetHash;
            this.base = base;
        }

        @Override
        public void run() {
            try {
                while (target != base.getPropertyValue(property).hashCode()) {
                    sleep(10);
                }
                base.notifyMirror(property);
            } catch (InterruptedException ex) {
                Logger.getLogger(MirrorMatrix.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        final int target;
        final String property;
        final MirrorBase base;
    }
}
