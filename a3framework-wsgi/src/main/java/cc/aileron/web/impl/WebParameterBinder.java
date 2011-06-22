/**
 * 
 */
package cc.aileron.web.impl;

import java.util.List;

import cc.aileron.web.WebProcess;

import com.google.inject.ImplementedBy;

/**
 * @author aileron
 */
@ImplementedBy(WebParameterBinderImpl.class)
public interface WebParameterBinder
{
    /**
     * @param <T>
     * @param type
     * @return process
     * @throws NoSuchMethodException
     * @throws SecurityException
     */
    <T> List<WebProcess<T>> bind(Class<? super T> type)
            throws SecurityException, NoSuchMethodException;

}
