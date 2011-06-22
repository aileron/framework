package cc.aileron.pojo.meta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

/**
 * @author Aileron
 */
public interface ClassAccessorSetter
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
     * @param target
     * @param value
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws InvocationTargetException 
     */
    void set(Object target, Object value)
            throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException;
}