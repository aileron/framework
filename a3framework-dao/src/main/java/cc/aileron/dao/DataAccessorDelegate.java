/**
 * 
 */
package cc.aileron.dao;

import java.util.List;

/**
 * @author aileron
 * @param <T> 
 */
public interface DataAccessorDelegate<T>
{
    /**
     * @param conditions
     */
    void delete(DataWhereCondition<?>... conditions);

    /**
     * @param range 
     * @param conditions
     * @return list
     */
    List<T> find(DataFinderRange range, DataWhereCondition<?>... conditions);

    /**
     * @param range 
     * @param conditions
     * @return list
     */
    List<T> find(DataWhereCondition<?>... conditions);

    /**
     * @param object
     * @param conditions 
     * @return update count
     */
    int update(T object, DataWhereCondition<?>... conditions);
}