/**
 * 
 */
package cc.aileron.generic.util;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * h2db から パクってきた。トーマス感謝!!!
 * 
 * Map which stores items using SoftReference. Items can be garbage collected
 * and removed. It is not a general purpose cache, as it doesn't implement some
 * methods, and others not according to the map definition, to improve speed.
 * 
 * @param <K>
 *            the key type
 * @param <V>
 *            the value type
 */
public class SoftHashMap<K, V> extends AbstractMap<K, V>
{

    /**
     * A soft reference that has a hard reference to the key.
     */
    private static class SoftValue<T> extends SoftReference<T>
    {
        public SoftValue(final T ref, final ReferenceQueue<T> q,
                final Object key)
        {
            super(ref, q);
            this.key = key;
        }

        final Object key;

    }

    @Override
    public void clear()
    {
        processQueue();
        map.clear();
    }

    @Override
    public Set<Entry<K, V>> entrySet()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public V get(final Object key)
    {
        processQueue();
        final SoftReference<V> o = map.get(key);
        if (o == null)
        {
            return null;
        }
        return o.get();
    }

    /**
     * Store the object. The return value of this method is null or a
     * SoftReference.
     * 
     * @param key
     *            the key
     * @param value
     *            the value
     * @return null or the old object.
     */
    @Override
    public V put(final K key, final V value)
    {
        processQueue();
        final SoftValue<V> old = map.put(key, new SoftValue<V>(value,
                queue,
                key));
        return old == null ? null : old.get();
    }

    /**
     * Remove an object.
     * 
     * @param key
     *            the key
     * @return null or the old object
     */
    @Override
    public V remove(final Object key)
    {
        processQueue();
        final SoftReference<V> ref = map.remove(key);
        return ref == null ? null : ref.get();
    }

    @SuppressWarnings("unchecked")
    private void processQueue()
    {
        while (true)
        {
            final Reference<? extends V> o = queue.poll();
            if (o == null)
            {
                return;
            }
            final SoftValue<V> k = (SoftValue<V>) o;
            final Object key = k.key;
            map.remove(key);
        }
    }

    /**
     * default constractor
     */
    public SoftHashMap()
    {
        map = new HashMap<K, SoftValue<V>>();
    }

    private final Map<K, SoftValue<V>> map;

    private final ReferenceQueue<V> queue = new ReferenceQueue<V>();

}