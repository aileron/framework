/**
 * 
 */
package cc.aileron.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import cc.aileron.dao.DataTransaction;
import cc.aileron.dao.jdbc.DbName;

/**
 * @author aileron
 */
public class DataTransactionImpl implements DataTransaction
{
    /**
     * @author aileron
     */
    static class LocalContext
    {
        /**
         * connections
         */
        public Connection connection;

        /**
         * count
         */
        public int count = 0;
    }

    @Override
    public void begin()
    {
        if (local().count == 0)
        {
            try
            {
                local().connection = dataSource.getConnection();
                local().connection.setAutoCommit(false);
            }
            catch (final SQLException e)
            {
                throw new Error(e);
            }
        }
        local().count += 1;
    }

    @Override
    public void commit()
    {
        try
        {
            local().connection.commit();
        }
        catch (final SQLException e)
        {
            throw new Error(e);
        }
    }

    @Override
    public DbName db()
    {
        return db;
    }

    @Override
    public void end()
    {
        if (--local().count != 0)
        {
            return;
        }
        try
        {
            local().connection.close();
        }
        catch (final SQLException e)
        {
            throw new Error(e);
        }
        local(new LocalContext());
    }

    @Override
    public Connection get()
    {
        final Connection connection = local().connection;
        if (connection != null)
        {
            return local().connection;
        }
        try
        {
            return dataSource.getConnection();
        }
        catch (final SQLException e)
        {
            throw new Error(e);
        }
    }

    @Override
    public void rollback()
    {
        try
        {
            local().connection.rollback();
        }
        catch (final SQLException e)
        {
            throw new Error(e);
        }
    }

    /**
     * @return local
     */
    private LocalContext local()
    {
        return local.get();
    }

    /**
     * @param newValue
     */
    private void local(final LocalContext newValue)
    {
        local.set(newValue);
    }

    /**
     * @param dataSource
     * @throws SQLException
     */
    public DataTransactionImpl(final DataSource dataSource) throws SQLException
    {
        this.dataSource = dataSource;
        this.db = DbName.convert(dataSource.getConnection());
    }

    private final DataSource dataSource;

    private final DbName db;

    /**
     * local
     */
    private final ThreadLocal<LocalContext> local = new ThreadLocal<LocalContext>()
    {
        @Override
        protected LocalContext initialValue()
        {
            return new LocalContext();
        }
    };
}
