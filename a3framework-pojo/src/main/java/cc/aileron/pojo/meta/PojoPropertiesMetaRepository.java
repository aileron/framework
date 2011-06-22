/**
 * 
 */
package cc.aileron.pojo.meta;

/**
 * @author aileron
 */
public interface PojoPropertiesMetaRepository
{
    /**
     * @param <T>
     * @param target
     * @return {@link PojoPropertiesMeta}
     */
    <T> PojoPropertiesMeta<T> get(Class<T> target);
}
