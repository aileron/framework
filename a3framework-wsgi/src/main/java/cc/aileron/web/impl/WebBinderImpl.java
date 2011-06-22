/**
 * 
 */
package cc.aileron.web.impl;

import javax.inject.Inject;

import cc.aileron.commons.di.InstanceRepository;
import cc.aileron.web.WebBinder;
import cc.aileron.wsgi.Wsgi.Method;

/**
 * @author aileron
 */
public class WebBinderImpl implements WebBinder
{
    @Override
    public <T> Bind<T> bind(final Class<T> type)
    {
        return new Bind<T>()
        {
            @Override
            public Setting<T> to(final Method method, final String uri)
                    throws SecurityException, NoSuchMethodException
            {
                final WebBinderSettingImpl<T> setting = new WebBinderSettingImpl<T>(type,
                        instance);
                container.set(method, uri, setting);
                return setting;
            }

            @Override
            public Setting<T> to(final Method method, final String uri,
                    final String overrideKey)
                    throws SecurityException, NoSuchMethodException
            {
                final Setting<T> setting = new WebBinderSettingImpl<T>(type,
                        instance);
                container.set(method, uri, overrideKey, setting);
                return setting;
            }
        };
    }

    /**
     * @param container
     * @param instance
     */
    @Inject
    public WebBinderImpl(final WebBinder.Container container,
            final InstanceRepository instance)
    {
        this.container = container;
        this.instance = instance;
    }

    final Container container;
    final InstanceRepository instance;

}
