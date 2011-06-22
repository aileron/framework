package cc.aileron.pojo.meta;


/**
 * @author Aileron
 * 
 * @param <T>
 */
public interface PojoPropertiesMeta<T>
{
    /**
     * @param target
     * @param name
     * @return object
     */
    PojoAccessorGetter get(final T target, final String name);

    /**
     * @return keys
     */
    Iterable<String> keys();

    /**
     * @param target
     * @param name
     * @return accessor
     */
    PojoAccessorSetter set(final T target, final String name);

}