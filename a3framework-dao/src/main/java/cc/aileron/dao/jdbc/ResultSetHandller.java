/**
 * 
 */
package cc.aileron.dao.jdbc;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;

import cc.aileron.pojo.NoSuchPropertyException;

/**
 * @author aileron
 */
public interface ResultSetHandller
{
    /**
     * @param rs
     * @throws SQLException
     * @throws InvocationTargetException
     * @throws NoSuchPropertyException
     * @throws Exception
     */
    void handle(ResultSet rs)
            throws SQLException, InvocationTargetException,
            NoSuchPropertyException, Exception;
}
