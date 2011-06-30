/**
 * 
 */
package cc.aileron.web;

import java.util.Properties;

import cc.aileron.commons.di.InstanceRepository;
import cc.aileron.generic.ObjectReference;
import cc.aileron.hier.UrlTreeContainerDelimiter;
import cc.aileron.hier.UrlTreeContainerImpl;
import cc.aileron.pojo.PojoAccessor;
import cc.aileron.pojo.PojoAccessorRepository;
import cc.aileron.web.WebBinder.Container;
import cc.aileron.web.impl.WebBinderContainerImpl;
import cc.aileron.web.impl.WebBinderImpl;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * @author aileron
 */
public class WebModel implements ObjectReference<WebBinder.Container>
{
    /**
     * @return {@link Container}
     */
    @Override
    public WebBinder.Container get()
    {
        return container;
    }

    /**
     * @param resource
     *            リソースクラス
     * @return リソースインスタンス
     */
    public PojoAccessor<Object> get(final Class<Object> resource)
    {
        return accessorRepository.from(injector.getInstance(resource));
    }

    /**
     * @param p
     * @throws Exception
     */
    public WebModel(final Properties p) throws Exception
    {
        this(p, Thread.currentThread().getContextClassLoader());
    }

    /**
     * @param loader
     * @param p
     * @throws Exception
     */
    public WebModel(final Properties p, final ClassLoader loader)
            throws Exception
    {

        /*
         * 設定ファイルを読み込み
         */
        final String configure = p.getProperty("wsgi.configure");
        final String module = p.getProperty("wsgi.module");

        /*
         * configure-class
         */
        final Class<? extends WebConfigure> configureClass = loader.loadClass(configure)
                .asSubclass(WebConfigure.class);

        /*
         * application-module-class
         */
        final Class<? extends Module> moduleClass = loader.loadClass(module)
                .asSubclass(Module.class);
        final Module applicationModule = moduleClass.newInstance();

        /*
         * inejctor
         */
        injector = Guice.createInjector(new Module()
        {
            @Override
            public void configure(final Binder binder)
            {
                binder.install(applicationModule);

                /*
                 * binder.bindScope(WebScope.Session.class, new Scope() {
                 * 
                 * @Override public <T> Provider<T> scope(final Key<T> key,
                 * final Provider<T> unscoped) { return new Provider<T>() {
                 * 
                 * @Override public T get() { final HttpServletRequest req =
                 * WebContext.request(); final HttpSession session =
                 * req.getSession(true); final String name = key.toString();
                 * final Object object = session.getAttribute(name); if (object
                 * != null) { return Functions.<T> cast(object); } final T value
                 * = unscoped.get(); session.setAttribute(name, value); return
                 * value; } }; } });
                 */
            }
        });

        /*
         * コンテナインスタンス取得
         */
        final UrlTreeContainerDelimiter delimiter = injector.getInstance(UrlTreeContainerDelimiter.class);
        container = new WebBinderContainerImpl(new UrlTreeContainerImpl(delimiter));
        final WebBinder binder = new WebBinderImpl(container,
                injector.getInstance(InstanceRepository.class));

        /*
         * web app configuration
         */
        injector.getInstance(configureClass).configure(binder);
        container.printDebugTree();
    }

    private final PojoAccessorRepository accessorRepository = PojoAccessor.Repository;
    private final Container container;
    private final Injector injector;
}
