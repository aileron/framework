package cc.aileron.pojo.meta;

import java.lang.reflect.Type;

/**
 * @author Aileron
 */
public interface ClassAccessorGetter
{
    /**
     * @return {@link Type}
     */
    Type genericType();

    /**
     * @param target
     * @param key
     * @return value
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    Object get(Object target, String key)
            throws IllegalArgumentException, IllegalAccessException;

    /**
     * @return {@link PojoPropertiesMetaCategory}
     */
    PojoPropertiesMetaCategory meta();

    /**
     * @return type
     */
    Class<?> resultType();
}