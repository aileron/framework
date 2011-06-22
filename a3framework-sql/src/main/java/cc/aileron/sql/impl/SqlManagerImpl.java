/**
 * 
 */
package cc.aileron.sql.impl;

import cc.aileron.sql.SqlExecutor;
import cc.aileron.sql.SqlFetcher;
import cc.aileron.sql.SqlManager;
import cc.aileron.sql.SqlParameter;

/**
 * @author aileron
 * @param <T>
 */
public class SqlManagerImpl<T> implements SqlManager<T>
{
    @Override
    public SqlExecutor<T> from(final Class<T> typeSql)
    {
        return new SqlExecutor<T>()
        {
            @Override
            public SqlParameter execute(final T parameter)
            {
                return new SqlParameter()
                {
                    @Override
                    public int execute()
                    {
                        templateRepository.get(typeSql, null);

                        return 0;
                    }

                    @Override
                    public <E> SqlFetcher<E> fetch(final Class<E> typeResult)
                    {
                        return null;
                    }
                };
            }
        };
    }

    /**
     * @param templateRepository
     */
    public SqlManagerImpl(final SqlTemplateRepository templateRepository)
    {
        this.templateRepository = templateRepository;
    }

    private final SqlTemplateRepository templateRepository;
}
