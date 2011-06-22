package cc.aileron.pojo.meta;

import java.lang.reflect.Type;

/**
 * @author aileron
 */
public interface PojoAccessorGetter
{
    /**
     * @return {@link Type}
     */
    Type genericType();

    /**
     * @return value
     */
    Object get();

    /**
     * @return {@link PojoPropertiesMetaCategory}
     */
    PojoPropertiesMetaCategory meta();

    /**
     * @return type
     */
    Class<?> resultType();
}