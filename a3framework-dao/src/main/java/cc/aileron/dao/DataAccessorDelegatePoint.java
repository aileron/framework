/**
 * 
 */
package cc.aileron.dao;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author aileron
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface DataAccessorDelegatePoint
{
    /**
     * @return Class<?>[]
     */
    Class<?>[] condition();

    /**
     * @return Class<? extends DataAccessorDelegate<?>>
     */
    Class<? extends DataAccessorDelegate<?>> value();
}
