/**
 * 
 */
package cc.aileron.sql;

import java.util.List;

/**
 * @author aileron
 * @param <E>
 */
public interface SqlFetcher<E>
{

    /**
     * @return list
     */
    List<E> get();

    /**
     * @param paging
     * @return list
     */
    List<E> get(SqlPaging paging);

    /**
     * @return one
     */
    E one();
}
