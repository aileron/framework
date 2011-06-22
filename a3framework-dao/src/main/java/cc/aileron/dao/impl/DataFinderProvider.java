/**
 * 
 */
package cc.aileron.dao.impl;

import java.util.List;

import cc.aileron.dao.DataWhereCondition;
import cc.aileron.generic.ObjectReference;

/**
 * @author aileron
 * @param <T>
 */
public interface DataFinderProvider<T>
{
    /**
     * @param cs
     * @return {@link DataFinderLocal}
     */
    DataFinderLocal<T> get(List<DataWhereCondition<?>> cs);

    /**
     * @param <R>
     * @param factory
     * @param cs
     * @return {@link DataFinderLocal}
     */
    <R> DataFinderLocal<R> get(ObjectReference<R> factory,
            List<DataWhereCondition<?>> cs);
}
