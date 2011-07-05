/**
 * 
 */
package cc.aileron.dao.impl;

import static cc.aileron.generic.$.*;

import java.util.Arrays;
import java.util.List;

import cc.aileron.dao.DataAccessor;
import cc.aileron.dao.DataAccessorDelegateRepository;
import cc.aileron.dao.DataFinder;
import cc.aileron.dao.DataTransaction;
import cc.aileron.dao.DataWhere;
import cc.aileron.dao.DataWhereCondition;
import cc.aileron.dao.jdbc.SqlTemplateCategory;
import cc.aileron.dao.jdbc.SqlTemplateFetcher;
import cc.aileron.dao.jdbc.SqlTemplateFetcherImpl;
import cc.aileron.dao.jdbc.StatmentExecutor;
import cc.aileron.dao.jdbc.StatmentExecutorImpl;
import cc.aileron.dao.jdbc.StatmentLogger;
import cc.aileron.generic.$.Flod;
import cc.aileron.generic.ConsCell;
import cc.aileron.generic.ObjectProvider;
import cc.aileron.generic.ObjectReference;
import cc.aileron.pojo.PojoAccessor;
import cc.aileron.pojo.PojoAccessorRepository;
import cc.aileron.template.Template;

/**
 * @author aileron
 * @param <T>
 */
class DataAccessorImpl<T> implements DataAccessor<T>
{

    @Override
    public void execute()
    {
        statment.execute(new SqlTemplateFetcherImpl(accessorRepository,
                templateRepository,
                type,
                "").fetch(SqlTemplateCategory.EXECUTE, null));
    }

    @Override
    public DataWhere<T> where(final DataWhereCondition<?>... conditions)
    {
        final DataWhere<T> delegate = delegateRepository.get(type, conditions);
        if (delegate != null)
        {
            return delegate;
        }
        return new DataWhere<T>()
        {

            @Override
            public void delete()
            {
                final DataFinderLocal<T> fetch = provider.get(Arrays.asList(conditions));
                statment.execute(fetch.fetcher.fetch(SqlTemplateCategory.DELETE,
                        fetch.parameter));
            }

            @Override
            public DataFinder<T> find()
            {
                final DataFinderLocal<T> fetch = provider.get(Arrays.asList(conditions));
                return fetch;
            }

            @Override
            public <R> DataFinder<R> find(final ObjectReference<R> factory)
            {
                final DataFinderLocal<R> fetch = provider.get(factory,
                        Arrays.asList(conditions));
                return fetch;
            }

            @Override
            public int insert(final T target)
            {
                final DataFinderLocal<T> fetch = provider.get(Arrays.asList(conditions));
                return statment.execute(fetch.fetcher.fetch(SqlTemplateCategory.INSERT,
                        fetch.parameter.add(target)));
            }

            @Override
            public int update(final T target)
            {
                final DataFinderLocal<T> fetch = provider.get(Arrays.asList(conditions));
                return statment.execute(fetch.fetcher.fetch(SqlTemplateCategory.DELETE,
                        fetch.parameter.add(target)));
            }
        };
    }

    @Override
    public DataWhere<T> where(final Object... conditions)
    {
        final DataWhereCondition<Object>[] cs = cast(new DataWhereCondition[conditions.length]);
        for (int i = 0, size = conditions.length; i < size; i++)
        {
            final Object target = conditions[i];
            final Class<Object> type;

            final Class<Object> rawtype = cast(target.getClass());
            if (rawtype.isAnonymousClass())
            {
                final Class<?>[] interfaces = rawtype.getInterfaces();
                type = cast(interfaces[0]);
            }
            else
            {
                type = rawtype;
            }
            cs[i] = new DataWhereCondition<Object>(type, target);
        }
        return where(cs);
    }

    /**
     * @param type
     * @param transaction
     * @param logger
     * @param isCache
     * @param templateRepository
     * @param accessorRepository
     * @param instance
     * @param delegateRepository
     */
    public DataAccessorImpl(final Class<T> type,
            final DataTransaction transaction, final StatmentLogger logger,
            final boolean isCache,
            final PojoAccessorRepository accessorRepository,
            final ObjectProvider<String, Template> templateRepository,
            final ObjectReference<T> instance,
            final DataAccessorDelegateRepository delegateRepository)
    {
        this.type = type;
        this.accessorRepository = accessorRepository;
        this.delegateRepository = delegateRepository;
        this.templateRepository = templateRepository;
        this.statment = new StatmentExecutorImpl(transaction, logger);
        this.provider = new DataFinderProvider<T>()
        {
            @Override
            public DataFinderLocal<T> get(final List<DataWhereCondition<?>> cs)
            {
                return get(instance, cs);
            }

            @Override
            public <R> DataFinderLocal<R> get(final ObjectReference<R> factory,
                    final List<DataWhereCondition<?>> cs)
            {
                final String fileName = cs.size() == 0 ? "All" : join("-",
                        iterate(new ObjectReference<String>()
                        {
                            @Override
                            public String get()
                            {
                                if (i < size)
                                {
                                    final Class<?> type = cs.get(i++).type();
                                    return type.getSimpleName();
                                }
                                return null;
                            }

                            int i = 0;
                            int size = cs.size();
                        }));

                final SqlTemplateFetcher sqlFetcher = new SqlTemplateFetcherImpl(accessorRepository,
                        templateRepository,
                        type,
                        fileName);

                final PojoAccessor<Object> parameterObject = new Flod<DataWhereCondition<?>, PojoAccessor<Object>>()
                {
                    @Override
                    public PojoAccessor<Object> get(
                            final ConsCell<DataWhereCondition<?>, PojoAccessor<Object>> c)
                    {
                        final DataWhereCondition<?> p = c.car();
                        final PojoAccessor<Object> r = c.cdr();
                        if (r == null)
                        {
                            return cast(accessorRepository.from(p.value()));
                        }
                        return r.add(p.value());
                    }
                }.apply(cs);

                return new DataFinderLocal<R>(isCache,
                        accessorRepository,
                        factory,
                        sqlFetcher,
                        statment,
                        parameterObject);
            }
        };
    }

    final PojoAccessorRepository accessorRepository;
    final DataAccessorDelegateRepository delegateRepository;
    final DataFinderProvider<T> provider;
    final StatmentExecutor statment;
    final ObjectProvider<String, Template> templateRepository;
    final Class<T> type;
}
