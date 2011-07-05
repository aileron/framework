/**
 * 
 */
package cc.aileron.dao;

import cc.aileron.dao.impl.DataAccessorObjectsFactoryImpl;
import cc.aileron.dao.jdbc.StatmentLogger;
import cc.aileron.generic.ObjectProvider;

/**
 * @author aileron
 */
public interface DataAccessorObjects
{
    /**
     * Factory
     */
    interface Factory
    {
        /**
         * @param isCache
         * @param instanceRepository
         * @param transaction
         * @param logger
         * @return {@link DataAccessorObjects}
         */
        DataAccessorObjects get(boolean isCache,
                final ObjectProvider<Class<?>, Object> instanceRepository,
                DataTransaction transaction, StatmentLogger logger);

        /**
         * @param isCache
         * @param instanceRepository
         * @param transaction
         * @param logger
         * @param delegateRepository 
         * @return {@link DataAccessorObjects}
         */
        DataAccessorObjects get(boolean isCache,
                final ObjectProvider<Class<?>, Object> instanceRepository,
                DataTransaction transaction, StatmentLogger logger,
                DataAccessorDelegateRepository delegateRepository);
    }

    /**
     * @param <T>
     * @param target
     * @return {@link DataAccessor}
     */
    <T> DataAccessor<T> from(Class<T> target);

    /**
     * factory
     */
    Factory factory = new DataAccessorObjectsFactoryImpl();
}
