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
     * execute
     */
    void execute();

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
