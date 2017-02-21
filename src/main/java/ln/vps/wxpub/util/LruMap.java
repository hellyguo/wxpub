package ln.vps.wxpub.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by helly on 2016/10/25.
 */
public class LruMap<K, V> extends LinkedHashMap<K, V> {

    private int maxSize;

    public LruMap(int maxSize, KeepOrder keepOrder) {
        super(maxSize, 0.75f, keepOrder.order);
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxSize;
    }

    public static enum KeepOrder {
        INSERT_ORDER(false), ACCESS_ORDER(true);
        private boolean order;

        KeepOrder(boolean order) {
            this.order = order;
        }
    }
}
