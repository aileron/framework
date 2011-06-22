/**
 *
 */
package cc.aileron.wsgi.mobile;

import java.util.Map.Entry;

/**
 * @author aileron
 */
public interface CssSelectorProperties extends Iterable<Entry<String, String>>
{
    /**
     * @param key
     * @return val
     */
    String get(String key);
}
