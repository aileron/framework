/**
 * 
 */
package cc.aileron.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

import cc.aileron.dao.DataFinder;
import cc.aileron.dao.DataFinderRange;
import cc.aileron.dao.jdbc.ResultSetHandller;
import cc.aileron.dao.jdbc.SqlTemplateCategory;
import cc.aileron.dao.jdbc.SqlTemplateFetcher;
import cc.aileron.dao.jdbc.StatmentExecutor;
import cc.aileron.generic.ObjectContainer;
import cc.aileron.generic.ObjectReference;
import cc.aileron.generic.Procedure;
import cc.aileron.generic.util.SkipList;
import cc.aileron.pojo.NoSuchPropertyException;
import cc.aileron.pojo.PojoAccessor;
import cc.aileron.pojo.PojoAccessorRepository;

/**
 * @author aileron
 * @param <T>
 */
public class DataFinderLocal<T> implements DataFinder<T>
{
    /**
     * object に fetch させないカラム名に付与する為のプレフィックス
     */
    static final char UNSET_COLUMN_MARKER_PREFIX = '#';

    @Override
    public void bind(final T object)
    {
        final PojoAccessor<T> accessor = repository.from(object);
        statment.execute(fetcher.fetch(SqlTemplateCategory.FIND_ONE, parameter),
                new ResultSetHandller()
                {
                    @Override
                    public void handle(final ResultSet rs)
                            throws SQLException, InvocationTargetException,
                            NoSuchPropertyException
                    {
                        final String[] meta = getMeta(rs.getMetaData());
                        if (!rs.next())
                        {
                            return;
                        }
                        for (int i = 1, size = meta.length; i < size; i++)
                        {
                            final String key = meta[i];
                            if (key.charAt(0) == UNSET_COLUMN_MARKER_PREFIX)
                            {
                                continue;
                            }
                            final Object val = rs.getObject(i);
                            accessor.to(key).set(val);
                        }
                    }
                });
    }

    @Override
    public int count()
    {
        final ObjectContainer<Integer> c = new ObjectContainer<Integer>(0);
        statment.execute(fetcher.fetch(SqlTemplateCategory.COUNT, parameter),
                new ResultSetHandller()
                {
                    @Override
                    public void handle(final ResultSet rs)
                            throws SQLException, InvocationTargetException,
                            NoSuchPropertyException
                    {
                        if (rs.next())
                        {
                            c.value = rs.getInt(1);
                        }
                    }
                });
        return c.value;
    }

    @Override
    public void each(final Procedure<T> procedure)
    {
        statment.execute(fetcher.fetch(SqlTemplateCategory.FIND, parameter),
                new ResultSetHandller()
                {
                    @Override
                    public void handle(final ResultSet rs) throws Exception
                    {
                        final String[] meta = getMeta(rs.getMetaData());
                        while (rs.next())
                        {
                            final PojoAccessor<T> object = instance.get();
                            for (int i = 1, size = meta.length; i < size; i++)
                            {
                                final String key = meta[i];
                                if (key.charAt(0) == UNSET_COLUMN_MARKER_PREFIX)
                                {
                                    continue;
                                }
                                final Object val = rs.getObject(i);
                                object.to(key).set(val);
                            }
                            procedure.call(object.target());
                        }
                    }
                });
    }

    @Override
    public List<T> list()
    {
        final SkipList<T> result = new SkipList<T>();
        statment.execute(fetcher.fetch(SqlTemplateCategory.FIND, parameter),
                new ResultSetHandller()
                {
                    @Override
                    public void handle(final ResultSet rs)
                            throws SQLException, InvocationTargetException,
                            NoSuchPropertyException
                    {
                        bind(rs, result);
                    }
                });
        return result;
    }

    @Override
    public List<T> list(final DataFinderRange paging)
    {
        final SkipList<T> result = new SkipList<T>();
        statment.execute(fetcher.fetch(SqlTemplateCategory.FIND_PAGING,
                parameter.add(paging)), new ResultSetHandller()
        {
            @Override
            public void handle(final ResultSet rs)
                    throws SQLException, InvocationTargetException,
                    NoSuchPropertyException
            {
                bind(rs, result);
            }
        });
        return result;
    }

    @Override
    public T one()
    {
        final SkipList<T> result = new SkipList<T>();
        statment.execute(fetcher.fetch(SqlTemplateCategory.FIND_ONE, parameter),
                new ResultSetHandller()
                {
                    @Override
                    public void handle(final ResultSet rs)
                            throws SQLException, InvocationTargetException,
                            NoSuchPropertyException
                    {
                        bind(rs, result);
                    }
                });
        if (result.isEmpty())
        {
            return null;
        }
        return result.get(0);
    }

    /**
     * @param rs
     * @param meta
     * @return T
     * @throws SQLException
     * @throws InvocationTargetException
     * @throws NoSuchPropertyException
     */
    List<T> bind(final ResultSet rs, final List<T> result)
            throws SQLException, InvocationTargetException,
            NoSuchPropertyException
    {
        final String[] meta = getMeta(rs.getMetaData());
        while (rs.next())
        {
            final PojoAccessor<T> object = instance.get();
            for (int i = 1, size = meta.length; i < size; i++)
            {
                final String key = meta[i];
                if (key.charAt(0) == UNSET_COLUMN_MARKER_PREFIX)
                {
                    continue;
                }
                final Object val = rs.getObject(i);
                object.to(key).set(val);
            }
            result.add(object.target());
        }
        return result;
    }

    /**
     * @param resultSet
     * @return ラベルの集合
     * @throws SQLException
     */
    String[] getMeta(final ResultSetMetaData meta) throws SQLException
    {
        final int count = meta.getColumnCount() + 1;
        final String[] result = new String[count];
        for (int i = 1; i < count; i++)
        {
            result[i] = meta.getColumnLabel(i);
        }
        return result;
    }

    /**
     * @param isCache
     * @param parameter
     * @param repository
     * @param instance
     * @param fetcher
     * @param statmentExecutor
     */
    public DataFinderLocal(final boolean isCache,
            final PojoAccessorRepository repository,
            final ObjectReference<T> instance,
            final SqlTemplateFetcher fetcher,
            final StatmentExecutor statmentExecutor,
            final PojoAccessor<?> parameter)
    {
        this.isCacheable = isCache;
        this.statment = statmentExecutor;
        this.parameter = parameter;
        this.repository = repository;
        this.instance = new ObjectReference<PojoAccessor<T>>()
        {
            @Override
            public PojoAccessor<T> get()
            {
                return repository.from(instance.get());
            }
        };
        this.fetcher = fetcher;
    }

    final SqlTemplateFetcher fetcher;
    final ObjectReference<PojoAccessor<T>> instance;
    final boolean isCacheable;
    final PojoAccessor<?> parameter;
    final PojoAccessorRepository repository;
    final StatmentExecutor statment;
}
