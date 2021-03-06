package org.noisesmith.noisespew;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.function.BiConsumer;

public class BiMap<V> {
    // for now specializing on V <-> int
    public Hashtable<Integer,V> store;

    public Hashtable<V,Integer> values;

    public BiMap () {
        this(new ArrayList<V> ());
    }

    public BiMap ( V[] initVals ) {
        this(new ArrayList<V> (Arrays.asList(initVals)));
    }

    public BiMap ( ArrayList<V> initVals ) {
        store = new Hashtable<Integer,V>();
        values = new Hashtable<V,Integer>();
        initVals.forEach(v -> put(v));
    }

    public int findIndex () {
        int i;
        for(i = 0; i < Integer.MAX_VALUE; i++) {
            if (!store.containsKey(i)) {
                return i;
            }
        }
        for(i = 0; i > Integer.MIN_VALUE; i--) {
            if (!store.containsKey(i)) {
                return i;
            }
        }
        return 0;
    }

    public int put( V v ) {
        int at;
        if (values.containsKey(v)) {
            return values.get(v);
        } else {
            at = findIndex();
            store.put(at, v);
            values.put(v, at);
            return at;
        }
    }

    public V get( int i ) {
        return store.get(i);
    }

    public BiConsumer<Integer,V> reindex = (i, s) -> values.put(s, i);

    public BiConsumer<V,Integer> indexre = (s, i) -> store.put(i, s);

    public void forEach(BiConsumer<Integer,V> action) {
        store.forEach(action);
        values.clear();
        store.forEach(reindex);
        store.clear();
        values.forEach(indexre);
    }

    public void remove(Integer index) {
        V v = store.get(index);
        store.remove(index);
        values.remove(v);
    }

    public int size() {
        return store.size();
    }

    public Boolean containsKey(Integer index) {
        return store.containsKey(index);
    }
}
