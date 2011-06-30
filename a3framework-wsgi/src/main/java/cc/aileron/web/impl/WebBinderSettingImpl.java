package cc.aileron.web.impl;

import java.util.List;

import cc.aileron.commons.di.InstanceRepository;
import cc.aileron.generic.util.SkipList;
import cc.aileron.web.WebBinder;
import cc.aileron.web.WebProcess;

/**
 * Binding 設定
 * 
 * @param <T>
 */
public class WebBinderSettingImpl<T> implements WebBinder.Setting<T>
{

    @Override
    public List<WebProcess<? super T>> process()
    {
        return process;
    }

    /*
     * (non-Javadoc)
     * 
     * @see cc.aileron.web.Setting#process(java.lang.Class)
     */
    @Override
    public WebBinder.Setting<T> process(
            final Class<? extends WebProcess<? super T>> process)
    {
        this.process.add(instance.get(process));
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see cc.aileron.web.Setting#process(cc.aileron.web.WebProcess)
     */
    @Override
    public WebBinder.Setting<T> process(final WebProcess<? super T> process)
    {
        instance.injectMembers(process);
        this.process.add(process);
        return this;
    }

    @Override
    public T resource()
    {
        return instance.get(resource);
    }

    @Override
    public Class<T> type()
    {
        return resource;
    }

    /**
     * @param resource
     * @param instance
     * @throws NoSuchMethodException
     * @throws SecurityException
     */
    public WebBinderSettingImpl(final Class<T> resource,
            final InstanceRepository instance) throws SecurityException,
            NoSuchMethodException
    {
        this.resource = resource;
        this.instance = instance;

        final WebParameterBinder parameter = instance.get(WebParameterBinder.class);
        process.addAll(parameter.bind(resource));
    }

    /**
     * プロセス
     */
    public final List<WebProcess<? super T>> process = new SkipList<WebProcess<? super T>>();

    /**
     * リソースクラス
     */
    public final Class<T> resource;

    /**
     * instance
     */
    private final InstanceRepository instance;
}