/**
 * 
 */
package cc.aileron.dao;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map.Entry;
import java.util.Properties;

import javax.sql.DataSource;

import cc.aileron.dao.impl.DataTransactionImpl;
import cc.aileron.generic.Resource;
import cc.aileron.pojo.NoSuchPropertyException;
import cc.aileron.pojo.PojoAccessor;

/**
 * @author aileron
 */
public class SimpleDataSource implements DataSource
{
    /**
     * @param path
     * @return {@link DataTransaction}
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchPropertyException
     * @throws SQLException
     */
    public static DataTransaction load(final String path)
            throws IOException, InvocationTargetException,
            NoSuchPropertyException, SQLException
    {
        final Properties config = Resource.Loader.get(path).toProperties();
        final PojoAccessor<SimpleDataSource> accessor = PojoAccessor.Repository.from(new SimpleDataSource());
        for (final Entry<Object, Object> e : config.entrySet())
        {
            final String key = (String) e.getKey();
            final String val = (String) e.getValue();
            accessor.to(key).set(val);
        }
        return new DataTransactionImpl(accessor.target());
    }

    /**
     * @param driverClassName
     * @throws ClassNotFoundException
     */
    public void driverClassName(final String driverClassName)
            throws ClassNotFoundException
    {
        Class.forName(driverClassName);
    }

    @Override
    public Connection getConnection() throws SQLException
    {
        return DriverManager.getConnection(url, username, password);
    }

    @Override
    public Connection getConnection(final String arg0, final String arg1)
            throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getLoginTimeout() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isWrapperFor(final Class<?> arg0) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @param password
     */
    public void password(final String password)
    {
        this.password = password;

    }

    @Override
    public void setLoginTimeout(final int logintTimeout) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLogWriter(final PrintWriter logwriter) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T unwrap(final Class<T> arg0) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @param url
     */
    public void url(final String url)
    {
        this.url = url;
    }

    /**
     * @param username
     */
    public void username(final String username)
    {
        this.username = username;
    }

    private String password;
    private String url;
    private String username;
}
