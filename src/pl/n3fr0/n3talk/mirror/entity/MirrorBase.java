package pl.n3fr0.n3talk.mirror.entity;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.n3fr0.n3talk.mirror.MirrorContainer;
import pl.n3fr0.n3talk.mirror.MirrorException;
import pl.n3fr0.n3talk.mirror.MirrorManager;
import pl.n3fr0.n3talk.mirror.entity.MirrorListModificator.Kind;


public abstract class MirrorBase implements MirrorObject {

    public MirrorBase() {
        this(null);
    }

    public MirrorBase(Object base) {
        if (base == null) {
            init(this);
        } else {
            init(base);
        }
    }

    protected void init(Object base) {
        baseObj = base;
        for (String p : baseObj.getClass().getAnnotation(Mirror.class).virtualProperty()) {
            registerProperty(p);
        }
        for (Field f : baseObj.getClass().getDeclaredFields()) {
            try {
                f.setAccessible(true);
                if (f.getAnnotation(Property.class) != null) {
                    registerProperty(f.getName());
                }
            } catch (SecurityException ex) {
                Logger.getLogger(MirrorManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    /**
     * Use setProperty method.
     * @see MirrorBase#setProperty(java.lang.Object) 
     * @deprecated
     */
    @Deprecated
    protected synchronized void notifyMirror() {
        notifyMirror(propertyName(getCallingMethod(STMN).getName()));
    }

    public synchronized void notifyMirror(String propertyName) {
        if (isMirrored() && !editingByBase) {
            manager.doUpdate(this, propertyName, container);
        }
        if (isMirrored() && editingByBase) {
            container.doChange();
        }
    }    

    public boolean isMirrored() {
        if (manager != null && objectUID != null) {
            return true;
        }
        return false;
    }

    public void attach() {
        if (!isMirrored()) {
            setObjectUID(manager.genObjectUID());
            manager.doCreate(this, container);
        }

    }

    public void detach() {
        if (isMirrored()) {
            container.remove(objectUID);
            manager.doDelete(this, container);
            objectUID = null;
            manager = null;
            container = null;
            baseObj = null;
        }
    }

    public String getObjectClass() {
        return baseObj.getClass().getName();
    }

    public MirrorManager getManager() {
        return manager;
    }

    public void setManager(MirrorManager manager) {
        this.manager = manager;
    }

    public String getObjectUID() {
        return objectUID;
    }

    public void setObjectUID(String objectUID) {
        this.objectUID = objectUID;
    }

    public MirrorContainer getContainer() {
        return container;
    }

    public void setContainer(MirrorContainer container) {
        this.container = container;
    }

    public MirrorProperty[] getMirrorProperties() {
        final List<MirrorProperty> lprop = new LinkedList<MirrorProperty>();
        for (final String p : regProps.keySet()) {
            lprop.add(new MirrorProperty() {

                public String getName() {
                    return p;
                }

                public Object getValue() {
                    Object value = getPropertyValue(p);
                    if (Tool.isMap(value)) {
                        value = new MirrorMapTransferer((MirrorBase)baseObj, (Map) value);
                    }
                    return value;
                }

                public boolean isList() {
                    return Tool.isList(getPropertyValue(p).getClass());
                }

                public boolean isMap() {
                    return Tool.isMap(getPropertyValue(p));
                }
                
            });
        }
        return lprop.toArray(new MirrorProperty[lprop.size()]);
    }

    public Object getPropertyValue(String propName) {
        Object o = null;
        try {
            final Method method = baseObj.getClass().getDeclaredMethod(regProps.get(propName).getMethodName);
            final Object[] params = {};
            o = method.invoke(baseObj, params);
            
        } catch (IllegalAccessException ex) {
            Logger.getLogger(MirrorBase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(MirrorBase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(MirrorBase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(MirrorBase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(MirrorBase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return o;
    }

    public void updataProperty(MirrorMapModificator mmm) {
        try {
            final Field field = this.getClass().getDeclaredField(mmm.getName());
            field.setAccessible(true);
            final Map map = (Map) field.get(this);
            switch (mmm.getKind()) {
                case PUT:
                    map.put(mmm.getKey(), mmm.getValue());
                    break;
                case REMOVE:
                    map.remove(mmm.getKey());
                    break;
            }

        } catch (NoSuchFieldException ex) {
            Logger.getLogger(MirrorBase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(MirrorBase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(MirrorBase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(MirrorBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateProperty(MirrorListModificator mlm) {
        Method method = null;
        for (Method m : baseObj.getClass().getDeclaredMethods()) {
            for (Annotation a : m.getDeclaredAnnotations()) {
                switch (mlm.getKind()) {
                    case ADD:
                        if (a.annotationType().equals(Adder.class) && ((Adder) a).property().equals(mlm.getName())) {
                            method = m;
                            break;
                        }
                        break;
                    case REMOVE:
                        if (a.annotationType().equals(Remover.class) && ((Remover) a).property().equals(mlm.getName())) {
                            method = m;
                            break;
                        }
                        break;
                    default:
                        break;
                }
            }
            if (method != null) {
                break;
            }
        }
        try {
            invoke(method, mlm.getObject());

        } catch (IllegalAccessException ex) {
            Logger.getLogger(MirrorBase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(MirrorBase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(MirrorBase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException ex) {
            Logger.getLogger(MirrorBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public synchronized void updateProperty(MirrorProperty p) {

        try {
            Class[] paramclass = null;
            Method method = null;
            final Object value = p.getValue();
            //XXX
            if (p.isList() || p.isMap()) {
                for (Method m : baseObj.getClass().getDeclaredMethods()) {
                    if (m.getName().equals(regProps.get(p.getName()).setMethodName)) {
                        method = m;
                        break;
                    }
                }
            } else {
                paramclass = new Class[]{value.getClass()};
                method = baseObj.getClass().getDeclaredMethod(regProps.get(p.getName()).setMethodName, paramclass);
            }

            invoke(method, value);

        } catch (IllegalAccessException ex) {
            Logger.getLogger(MirrorBase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(MirrorBase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(MirrorBase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(MirrorBase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(MirrorBase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException ex) {
            Logger.getLogger(MirrorBase.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void invoke(Method method, Object args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        editingByBase = true;

        final Object[] params = {args};
        method.invoke(baseObj, params);
        
        editingByBase = false;
    }

    private String propertyName(String name, String prefix) {
        final StringBuffer sb = new StringBuffer(prefix);
        sb.append(name.substring(0, 1).toUpperCase());
        sb.append(name.substring(1));
        return sb.toString();
    }

    public String propertyName(String methodName) {
        String propname = methodName.substring(3);
        return propname.substring(0, 1).toLowerCase() + propname.substring(1);
    }

    protected void setProperty(Object value) {
        try {
            final Method m = getCallingMethod("setProperty");
            String propertyName = null;
            if (m.isAnnotationPresent(Setter.class)) {
                propertyName = m.getAnnotation(Setter.class).propertyName();
            } else {
                propertyName = propertyName(m.getName());
            }
            final Field f = this.getClass().getDeclaredField(propertyName);
            f.setAccessible(true);
            f.set(this, value);
            notifyMirror(propertyName);

        } catch (NoSuchFieldException ex) {
            Logger.getLogger(MirrorBase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(MirrorBase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(MirrorBase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(MirrorBase.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException ex) {
            Logger.getLogger(MirrorBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @SuppressWarnings("unchecked")
    protected void addToList(List list, Object object) {
        container.add(object);
        list.add(object);
        if (!editingByBase) {
            final Method method = getCallingMethod("addToList");
            if (method != null && method.isAnnotationPresent(Adder.class)) {
                final Adder a = method.getAnnotation(Adder.class);
                updateList(object, a.property(), Kind.ADD);
            }
        }
    }

    protected void removeFromList(List list, Object object) {        
        if (!editingByBase) {
            final Method method = getCallingMethod("removeFromList");
            if (method != null && method.isAnnotationPresent(Remover.class)) {
                final Remover r = method.getAnnotation(Remover.class);
                updateList(object, r.property(), Kind.REMOVE);
            }
        }
        container.remove(object);
        list.remove(object);
    }

    private void updateList(final Object listItem, final String propertyName, final Kind kind) {
        final MirrorObject mo = this;
        manager.doUpdate(this, new MirrorListModificator() {

            public String getObjectUID() {
                return mo.getObjectUID();
            }

            public String getName() {
                return propertyName;
            }

            public Kind getKind() {
                return kind;
            }

            public Object getObject() {
                return listItem;
            }
        }, container);
    }

    public Object putToMap(Object key, Object value, Map map) {
        MirrorBase mbKey = null;
        if (!(key instanceof MirrorBase)) {
            mbKey = new PlainJObject(key);
        }
        final Method method = getCallingMethod("putToMap");
        if (method != null && method.isAnnotationPresent(Adder.class)) {
            container.add(mbKey != null ? mbKey : key);
            container.add(value);
            final Adder a = method.getAnnotation(Adder.class);
            updateMap(mbKey != null ? mbKey : key, value, a.property(), MirrorMapModificator.Kind.PUT);
        }
        return map.put(key, value);
    }

    private void updateMap(final Object key, final Object value, final String property, final MirrorMapModificator.Kind kind) {
        final MirrorObject mo = this;
        manager.doUpdate(this, new MirrorMapModificator() {

            public String getObjectUID() {
                return mo.getObjectUID();
            }

            public String getName() {
                return property;
            }

            public Kind getKind() {
                return kind;
            }

            public Object getKey() {
                return key;
            }

            public Object getValue() {
                return value;
            }
        }, container);
    }

    private final Method getCallingMethod(final String myMethodName) {
        boolean call = false;
        Method method = null;
        for (StackTraceElement e : Thread.currentThread().getStackTrace()) {
            if (call) {
                for (Method m : this.getClass().getDeclaredMethods()) {
                    if (m.getName().equals(e.getMethodName())) {
                        method = m;
                        break;
                    }
                }
                break;
            }
            if (e.getMethodName().equals(myMethodName)) {
                call = true;
            }
        }
        return method;
    }

    /**
     * Get object lock status.
     * @return true, if object is unlockt. False if object is lockt.
     */
    public boolean tryLock() {
        return !lock;
    }

    public synchronized void setLock(ObjectLock lockOwner) throws MirrorException {
        if (lock) {
            throw new MirrorException(MirrorException.OBJECT_LOCKT);
        } else {
            this.lockOwner = lockOwner;
            lock = true;
        }
    }

    public synchronized void unlock(ObjectLock lockOwner) {
        //
    }

    private void registerProperty(String propertyName) {
        final RegisteredProperty p = new RegisteredProperty();
        for (Method m : baseObj.getClass().getDeclaredMethods()) {
            m.setAccessible(true);

            final Setter setter = m.getAnnotation(Setter.class);
            if (setter != null && setter.propertyName().equals(propertyName)) {
                p.setMethodName = m.getName();
            }

            final Getter getter = m.getAnnotation(Getter.class);
            if (getter != null && getter.propertyName().equals(propertyName)) {
                p.getMethodName = m.getName();
            }
        }

        if (p.setMethodName == null) {
            p.setMethodName = propertyName(propertyName, "set");
        }
        if (p.getMethodName == null) {
            p.getMethodName = propertyName(propertyName, "get");
        }

        regProps.put(propertyName, p);
        
    }

    private class RegisteredProperty {
        String setMethodName;
        String getMethodName;
    }
    
    private Object baseObj;
    protected MirrorManager manager;
    protected MirrorContainer container;
    private String objectUID;
    private boolean editingByBase;
    private volatile boolean lock;
    private ObjectLock lockOwner;
    final private Map<String, RegisteredProperty> regProps = new Hashtable<String, RegisteredProperty>();
    private final static String STMN = "notifyMirror";

}
