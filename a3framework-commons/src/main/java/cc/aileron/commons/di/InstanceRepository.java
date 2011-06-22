/**
 * 
 */
package cc.aileron.commons.di;

import java.lang.annotation.Annotation;

import cc.aileron.generic.ObjectProvider;

import com.google.inject.ImplementedBy;

/**
 * @author aileron
 */
@ImplementedBy(InstanceRepositoryImpl.class)
public interface InstanceRepository
{
    /**
     * @param <T>
     * @param type
     * @return instance
     */
    <T> T get(Class<T> type);

    /**
     * @param <T>
     * @param type
     * @param annotation
     * @return T
     */
    <T> T get(Class<T> type, Class<? extends Annotation> annotation);

    /**
     * @param <T>
     * @param object
     * @return object
     */
    <T> T injectMembers(T object);

    /**
     * @return {@link ObjectProvider}
     */
    ObjectProvider<Class<?>, Object> objectProvider();
}
