/**
 * 
 */
package cc.aileron.generic;

/**
 * @author aileron
 * @param <T>
 */
public interface Procedure<T>
{
    /**
     * @param object
     * @throws Exception
     */
    void call(T object) throws Exception;
}
