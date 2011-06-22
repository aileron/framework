/**
 * 
 */
package cc.aileron.dao;

import cc.aileron.dao.impl.DataAccessorRepositoryFactoryImpl;
import cc.aileron.dao.jdbc.StatmentLogger;
import cc.aileron.generic.ObjectProvider;

/**
 * @author aileron
 */
public interface DataAccessorRepository
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
         * @return {@link DataAccessorRepository}
         */
        DataAccessorRepository get(boolean isCache,
                final ObjectProvider<Class<?>, Object> instanceRepository,
                DataTransaction transaction, StatmentLogger logger);

        /**
         * @param isCache
         * @param instanceRepository
         * @param transaction
         * @param logger
         * @param delegateRepository 
         * @return {@link DataAccessorRepository}
         */
        DataAccessorRepository get(boolean isCache,
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
    Factory factory = new DataAccessorRepositoryFactoryImpl();
}
