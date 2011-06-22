package cc.aileron.dao.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * DB名の列挙型
 * 
 * @author Aileron
 */
public enum DbName
{
    /**
     * h2
     */
    H2,

    /**
     * mysql
     */
    MYSQL,

    /**
     * postgresql
     */
    POSTGRESQL,

    /**
     * unknown
     */
    UNKNOWN;

    /**
     * @param connection
     * @return DbName
     */
    public static DbName convert(final Connection connection)
    {
        final String name;
        try
        {
            name = connection.getMetaData().getDatabaseProductName();
        }
        catch (final SQLException e)
        {
            throw new Error(e);
        }
        try
        {
            return valueOf(name.toUpperCase());
        }
        catch (final Exception e)
        {
            throw new Error(name);
        }
    }

}