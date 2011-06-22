/**
 * 
 */
package cc.aileron.web;

import java.util.Properties;

import cc.aileron.generic.ObjectReference;
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
     * @param loader
     * @param p
     * @throws Exception
     */
    public WebModel(final ClassLoader loader, final Properties p)
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
                binder.bind(WebBinder.Container.class)
                        .to(WebBinderContainerImpl.class)
                        .asEagerSingleton();
                binder.bind(WebBinder.class)
                        .to(WebBinderImpl.class)
                        .asEagerSingleton();
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
         * web app configuration
         */
        final WebBinder binder = injector.getInstance(WebBinder.class);
        injector.getInstance(configureClass).configure(binder);

        /*
         * container
         */
        container = injector.getInstance(WebBinder.Container.class);
    }

    /**
     * @param properties
     * @throws Exception
     */
    public WebModel(final Properties properties) throws Exception
    {
        this(Thread.currentThread().getContextClassLoader(), properties);
    }

    private final PojoAccessorRepository accessorRepository = PojoAccessor.Repository;
    private final Container container;
    private final Injector injector;
}
