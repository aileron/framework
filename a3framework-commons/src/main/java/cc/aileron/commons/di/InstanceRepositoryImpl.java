/**
 * 
 */
package cc.aileron.commons.di;

import java.lang.annotation.Annotation;

import javax.inject.Inject;

import cc.aileron.generic.ObjectProvider;

import com.google.inject.Injector;
import com.google.inject.Key;

/**
 * @author aileron
 */
@javax.inject.Singleton
public class InstanceRepositoryImpl implements InstanceRepository
{

    @Override
    public <T> T get(final Class<T> type)
    {
        return injector.getInstance(type);
    }

    @Override
    public <T> T get(final Class<T> type,
            final Class<? extends Annotation> annotation)
    {
        return injector.getInstance(Key.get(type, annotation));
    }

    @Override
    public <T> T injectMembers(final T object)
    {
        injector.injectMembers(object);
        return object;
    }

    @Override
    public ObjectProvider<Class<?>, Object> objectProvider()
    {
        return objectProvider;
    }

    /**
     * @param injector
     */
    @Inject
    public InstanceRepositoryImpl(final Injector injector)
    {
        this.injector = injector;
        this.objectProvider = new ObjectProvider<Class<?>, Object>()
        {
            @Override
            public Object get(final Class<?> type)
            {
                return injector.getInstance(type);
            }
        };
    }

    final Injector injector;
    final ObjectProvider<Class<?>, Object> objectProvider;
}
