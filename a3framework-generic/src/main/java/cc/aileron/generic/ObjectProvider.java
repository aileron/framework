/**
 * 
 */
package cc.aileron.generic;

/**
 * @author aileron
 * @param <K>
 * @param <V>
 */
public interface ObjectProvider<K, V>
{
    /**
     * @param k
     * @return V
     */
    V get(K k);
}
