/**
 * 
 */
package cc.aileron.pojo;

import cc.aileron.pojo.impl.PojoAccessorRepositoryImpl;

/**
 * @author aileron
 */
public interface PojoAccessorRepository
{
    /**
     * @author aileron
     */
    class Factory extends PojoAccessorRepositoryImpl
    {
        static final PojoAccessorRepository INSTANCE = new Factory();

        public static PojoAccessorRepository get()
        {
            return INSTANCE;
        }
    }

    /**
     * @param <T>
     * @param object
     * @return {@link PojoAccessor}
     */
    <T> PojoAccessor<T> from(final T object);
}
