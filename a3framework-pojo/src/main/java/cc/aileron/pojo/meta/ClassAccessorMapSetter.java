package cc.aileron.pojo.meta;

import java.lang.reflect.Type;

/**
 * @author Aileron
 */
public interface ClassAccessorMapSetter
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
     * @param key
     * @param value
     */
    void set(Object target, String key, Object value);
}