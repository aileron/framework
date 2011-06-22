/**
 * 
 */
package cc.aileron.dao;

import java.sql.Connection;

import cc.aileron.dao.jdbc.DbName;
import cc.aileron.generic.ObjectReference;

/**
 * @author aileron
 */
public interface DataTransaction extends ObjectReference<Connection>
{
    /**
     */
    void begin();

    /**
     */
    void commit();

    /**
     * @return {@link DbName}
     */
    DbName db();

    /**
     * 
     */
    void end();

    /**
     */
    void rollback();
}
