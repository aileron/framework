/**
 *
 */
package cc.aileron.hier;

import java.util.Map;
import java.util.Set;

import cc.aileron.wsgi.Wsgi.Method;

/**
 * @author aileron
 */
public interface UrlTreeContainer
{
    /**
     * バインドされているurlツリー
     * 
     * @return tree
     */
    Map<String, Integer> all();

    /**
     * @param uri
     * @param ext
     * @param method
     * @param uriparameter
     * @param requestParameter
     * @param names
     * @param key
     * @return id
     */
    int get(String uri, Method method, Map<String, Object> uriparameter,
            Set<String> requestParameter);

    /**
     * @param uri
     * @param ext
     * @param method
     * @param overrideKey
     * @param key
     * @param id
     */
    void put(String uri, Method method, int id);

    /**
     * @param uri
     * @param ext
     * @param method
     * @param overrideKey
     * @param key
     * @param id
     */
    void put(String uri, Method method, String overrideKey, int id);
}
