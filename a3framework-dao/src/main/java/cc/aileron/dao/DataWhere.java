/**
 * 
 */
package cc.aileron.dao;

import cc.aileron.generic.ObjectReference;

/**
 * @author aileron
 * @param <T>
 */
public interface DataWhere<T>
{
    /**
     */
    void delete();

    /**
     * @return {@link DataFinder}
     */
    DataFinder<T> find();

    /**
     * @param factory
     * @param <R>
     * @return {@link DataFinder}
     */
    <R> DataFinder<R> find(ObjectReference<R> factory);

    /**
     * @param target
     * @return update count
     */
    int update(T target);
}
