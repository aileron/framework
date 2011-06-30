/**
 * 
 */
package cc.aileron.web.impl;

import gnu.trove.TIntObjectHashMap;

import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Singleton;

import cc.aileron.generic.$;
import cc.aileron.hier.UrlTreeContainer;
import cc.aileron.web.WebBinder;
import cc.aileron.web.WebBinder.Setting;
import cc.aileron.wsgi.Wsgi.Method;

/**
 * @author aileron
 */
@Singleton
public class WebBinderContainerImpl implements WebBinder.Container
{

    @Override
    public WebBinder.Setting<Object> get(final Method method, final String uri,
            final Map<String, Object> parameter)
    {
        final int id = container.get(uri, method, parameter, parameter.keySet());
        if (id == 0)
        {
            return null;
        }
        final Setting<?> setting = objects.get(id);
        return $.cast(setting);
    }

    @Override
    public void printDebugTree()
    {
        for (final Entry<String, Integer> e : container.all().entrySet())
        {
            System.err.println(e.getKey() + " "
                    + objects.get(e.getValue()).type());
        }
    }

    @Override
    public void set(final Method method, final String uri,
            final Setting<?> setting)
    {
        objects.put(id, setting);
        container.put(uri, method, id++);
    }

    @Override
    public void set(final Method method, final String uri,
            final String overrideKey, final Setting<?> setting)
    {
        objects.put(id, setting);
        container.put(uri, method, overrideKey, id++);
    }

    /**
     * @param treeContainer
     */
    @Inject
    public WebBinderContainerImpl(final UrlTreeContainer treeContainer)
    {
        this.container = treeContainer;
    }

    private final UrlTreeContainer container;
    private int id = 1;
    private final TIntObjectHashMap<Setting<?>> objects = new TIntObjectHashMap<Setting<?>>();
}
