/**
 * 
 */
package cc.aileron.dao;

import java.util.List;

import cc.aileron.generic.Procedure;

/**
 * @author aileron
 * @param <T>
 */
public interface DataFinder<T>
{
    /**
     * @param object
     */
    void bind(T object);

    /**
     * @return count
     */
    int count();

    /**
     * @param t
     */
    void each(Procedure<T> t);

    /**
     * @return list
     */
    List<T> list();

    /**
     * @param range
     * @return list
     */
    List<T> list(DataFinderRange range);

    /**
     * @return T
     */
    T one();
}
