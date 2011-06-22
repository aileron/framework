/**
 * 
 */
package cc.aileron.pojo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

/**
 * @author aileron
 */
public interface PojoProperty
{
    /**
     * @author aileron
     * @param <T>
     */
    interface Key<T>
    {
    }

    /**
     * @return {@link PojoAccessor}
     */
    PojoAccessor<Object> accessor();

    /**
     * @param <E>
     * @param type
     * @return {@link Iterable}
     */
    <E> Iterable<PojoAccessor<E>> accessorIterable(Class<E> type);

    /**
     * @param category
     * @return exist
     */
    boolean exist(PojoAccessor.Category category);

    /**
     * @param category
     * @return Type
     */
    Type genericType(PojoAccessor.Category category);

    /**
     * @return value
     */
    Object get();

    /**
     * @param type
     * @param <E>
     * @return Object
     */
    <E> E get(Class<E> type);

    /**
     * @param type
     * @param <E>
     * @return Object
     */
    <E> E get(Key<E> type);

    /**
     * @param <E>
     * @param type
     * @return {@link Iterable}
     */
    <E> Iterable<E> iterable(Class<E> type);

    /**
     * @param value
     * @throws InvocationTargetException
     */
    void set(Object value) throws InvocationTargetException;

    /**
     * @param category
     * @return type
     */
    Class<?> type(PojoAccessor.Category category);
}
