/**
 * 
 */
package cc.aileron.sql;

/**
 * @author aileron
 * @param <T>
 */
public interface SqlExecutor<T>
{
    /**
     * @param parameter
     * @return {@link SqlParameter}
     */
    SqlParameter execute(T parameter);
}
