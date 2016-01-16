package pl.n3fr0.n3talk.mirror.entity;

import java.util.Map;

public class MirrorMapTransferer {

    public MirrorMapTransferer(MirrorBase base, Map map) {
        this.base = base;
        this.map = map;
    }

    public MapEntry[] getEntries() {
        final MapEntry[] entries = new MapEntry[map.size()];
        int i = 0;
        for (Object k : map.keySet()) {
            final MirrorBase pjo = new PlainJObject(k);
            base.container.add(pjo);
            entries[i++] = new MapEntry(pjo, (MirrorBase) map.get(k));
        }
        return entries;
    }

    public String getMapClassName() {
        return map.getClass().getName();
    }

    public class MapEntry {

        public MapEntry(MirrorBase key, MirrorBase value) {
            this.key = key;
            this.value = value;
        }
        public final MirrorBase key;
        public final MirrorBase value;
    }

    private MirrorBase base;
    private Map map;
}
