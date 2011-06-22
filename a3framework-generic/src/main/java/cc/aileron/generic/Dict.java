/**
 * 
 */
package cc.aileron.generic;

/**
 * @author aileron
 * @param <K>
 *            key
 * @param <V>
 *            value
 */
public interface Dict<K, V> extends Iterable<ConsCell<K, V>>
{
    /**
     * @param dict
     * @return dict
     */
    Dict<K, V> add(Dict<K, V> dict);

    /**
     * @param key
     * @return value
     */
    V get(K key);

    /**
     * @param cell
     * @return {@link Dict}
     */
    Dict<K, V> put(ConsCell<K, V> cell);

    /**
     * @param key
     * @param value
     * @return {@link Dict}
     */
    Dict<K, V> put(K key, V value);
}
