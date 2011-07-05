/**
 * 
 */
package cc.aileron.dao.impl;

import java.util.HashMap;

import cc.aileron.dao.DataAccessor;
import cc.aileron.dao.DataAccessorDelegateRepository;
import cc.aileron.dao.DataAccessorObjects;
import cc.aileron.dao.DataFinder;
import cc.aileron.dao.DataTransaction;
import cc.aileron.dao.DataWhere;
import cc.aileron.dao.DataWhereCondition;
import cc.aileron.dao.jdbc.SqlTemplateInstractionRepository;
import cc.aileron.dao.jdbc.StatmentLogger;
import cc.aileron.generic.$;
import cc.aileron.generic.ObjectProvider;
import cc.aileron.generic.ObjectReference;
import cc.aileron.generic.Resource;
import cc.aileron.pojo.PojoAccessorRepository;
import cc.aileron.template.Template;
import cc.aileron.template.TemplateRepository;
import cc.aileron.template.compiler.Symbol;
import cc.aileron.template.impl.TemplateRepositoryImpl;

/**
 * @author aileron
 */
public class DataAccessorObjectsFactoryImpl implements
        DataAccessorObjects.Factory
{
    @Override
    public DataAccessorObjects get(final boolean isCache,
            final ObjectProvider<Class<?>, Object> instanceRepository,
            final DataTransaction transaction, final StatmentLogger logger)
    {
        return get(isCache,
                instanceRepository,
                transaction,
                logger,
                new DataAccessorDelegateRepository()
                {
                    @Override
                    public <T> DataWhere<T> get(final Class<T> type,
                            final DataWhereCondition<?>... conditions)
                    {
                        return null;
                    }
                });
    }

    @Override
    public DataAccessorObjects get(final boolean isCache,
            final ObjectProvider<Class<?>, Object> instanceRepository,
            final DataTransaction transaction, final StatmentLogger logger,
            final DataAccessorDelegateRepository delegateRepository)
    {
        return new DataAccessorObjects()
        {
            @Override
            public <T> DataAccessor<T> from(final Class<T> target)
            {
                return new DataAccessorImpl<T>(target,
                        transaction,
                        logger,
                        isCache,
                        accessorRepository,
                        template,
                        new ObjectReference<T>()
                        {
                            @Override
                            public T get()
                            {
                                final T instance = $.<T> cast(instanceRepository.get(target));
                                return instance;
                            }
                        },
                        delegateRepository);
            }
        };
    }

    final PojoAccessorRepository accessorRepository = PojoAccessorRepository.Factory.get();
    final HashMap<Class<?>, DataFinder<?>> map = new HashMap<Class<?>, DataFinder<?>>();
    final ObjectProvider<String, Template> template = new ObjectProvider<String, Template>()
    {
        @Override
        public Template get(final String path)
        {
            final Template result = map.get(path);
            if (result != null)
            {
                return result;
            }
            synchronized (this)
            {
                final Template newResult = repository.get(Resource.Loader.get(path));
                map.put(path, newResult);
                return newResult;
            }
        }

        final HashMap<String, Template> map = new HashMap<String, Template>();

        final TemplateRepository repository = new TemplateRepositoryImpl(new Symbol()
        {

            @Override
            public String end()
            {
                return "*/";
            }

            @Override
            public String start()
            {
                return "/*";
            }
        },
                new SqlTemplateInstractionRepository());
    };
}
