/**
 * 
 */
package cc.aileron.sql;

/**
 * @author aileron
 * @param <T>
 */
public interface SqlManager<T>
{
    /**
     * @param type
     * @return {@link SqlExecutor}
     */
    SqlExecutor<T> from(Class<T> type);
}
