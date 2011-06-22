/**
 * 
 */
package cc.aileron.sql;

/**
 * @author aileron
 */
public interface SqlParameter
{
    /**
     * @return int
     */
    int execute();

    /**
     * @param <E>
     * @param type
     * @return {@link SqlFetcher}
     */
    <E> SqlFetcher<E> fetch(Class<E> type);
}
