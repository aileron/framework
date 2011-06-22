package cc.aileron.pojo.meta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

/**
 * @author Aileron
 */
public interface PojoAccessorSetter
{
    /**
     * @return type
     */
    Class<?> argumentType();

    /**
     * @return {@link Type}
     */
    Type genericType();

    /**
     * @return {@link PojoPropertiesMetaCategory}
     */
    PojoPropertiesMetaCategory meta();

    /**
     * @param value
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    void set(Object value)
            throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException;
}