/**
 * 
 */
package cc.aileron.template;

/**
 * @author aileron
 * @param <T>
 *            each target object type
 * @param <R>
 */
public interface EachContextHandller<T, R>
{
    /**
     * @param object
     * @return result
     */
    R call(T object);

    /**
     * @param object
     * @return result
     */
    R last(T object);
}
