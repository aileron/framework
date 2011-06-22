/**
 * 
 */
package cc.aileron.dao;

/**
 * @author aileron
 * @param <T>
 */
public interface DataAccessor<T>
{
    /**
     * @param target
     * @return int
     */
    int insert(T target);

    /**
     * @param conditions
     * @return {@link DataWhere}
     */
    DataWhere<T> where(DataWhereCondition<?>... conditions);

    /**
     * @param conditions
     * @return {@link DataWhere}
     */
    DataWhere<T> where(Object... conditions);
}
