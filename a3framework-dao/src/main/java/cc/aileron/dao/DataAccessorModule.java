/**
 * 
 */
package cc.aileron.dao;

import java.lang.annotation.Annotation;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import cc.aileron.dao.jdbc.StatmentLoggerImpl;
import cc.aileron.generic.ObjectProvider;

import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.matcher.Matchers;

/**
 * @author aileron
 */
public class DataAccessorModule implements Module
{
    @Override
    public void configure(final Binder binder)
    {
        binder.requestInjection(i);
        final LinkedBindingBuilder<DataAccessorRepository> b;
        if (an == null)
        {
            b = binder.bind(DataAccessorRepository.class);
        }
        else
        {
            b = binder.bind(DataAccessorRepository.class).annotatedWith(an);
        }
        b.toInstance(entityAccessorRepository);

        binder.bindInterceptor(Matchers.any(),
                Matchers.annotatedWith(DataTransactional.class),
                new MethodInterceptor()
                {
                    @Override
                    public Object invoke(final MethodInvocation invocation)
                            throws Throwable
                    {
                        try
                        {
                            transaction.begin();
                            final Object result = invocation.proceed();
                            transaction.commit();
                            return result;
                        }
                        catch (final Throwable th)
                        {
                            transaction.rollback();
                            throw th;
                        }
                        finally
                        {
                            transaction.end();
                        }
                    }
                });
    }

    /**
     * @param isCache
     * @param transaction
     */
    public DataAccessorModule(final boolean isCache,
            final DataTransaction transaction)
    {
        this(isCache, transaction, null);
    }

    /**
     * @param isCache
     * @param transaction
     * @param an
     */
    public DataAccessorModule(final boolean isCache,
            final DataTransaction transaction,
            final Class<? extends Annotation> an)
    {
        this.transaction = transaction;
        this.an = an;
        this.entityAccessorRepository = DataAccessorRepository.factory.get(isCache,
                i,
                transaction,
                new StatmentLoggerImpl());
    }

    /**
     * @param transaction
     */
    public DataAccessorModule(final DataTransaction transaction)
    {
        this(transaction, null);
    }

    /**
     * @param transaction
     * @param an
     */
    public DataAccessorModule(final DataTransaction transaction,
            final Class<? extends Annotation> an)
    {
        this(false, transaction, an);
    }

    final DataTransaction transaction;

    private final Class<? extends Annotation> an;

    private final DataAccessorRepository entityAccessorRepository;
    private final ObjectProvider<Class<?>, Object> i = new ObjectProvider<Class<?>, Object>()
    {
        @Override
        public Object get(final Class<?> type)
        {
            return injector.getInstance(type);
        }

        @Inject
        Injector injector;
    };
}
