/**
 * 
 */
package cc.aileron.pojo.meta;

import cc.aileron.pojo.NoSuchPropertyException;
import cc.aileron.pojo.PojoProperty;

/**
 * @author aileron
 */
public interface PojoPropertyFetcher
{
    /**
     * @param key
     * @return object
     * @throws NoSuchPropertyException
     */
    PojoProperty fetch(String key) throws NoSuchPropertyException;
}
