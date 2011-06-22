/**
 * 
 */
package cc.aileron.dao;

/**
 * @author aileron
 */
public interface DataAccessorDelegateRepository
{
    /**
     * @param <T>
     * @param type
     * @param conditions
     * @return {@link DataAccessorDelegate}
     */
    <T> DataWhere<T> get(Class<T> type, DataWhereCondition<?>... conditions);
}
