/**
 * 
 */
package cc.aileron.wsgi;

import java.io.Serializable;
import java.util.HashMap;

import cc.aileron.generic.$;
import cc.aileron.wsgi.Wsgi.Session;

/**
 * Wsgi Session
 */
public class WsgiSession implements Session, Serializable
{
    private static final long serialVersionUID = -5288972364613050920L;

    @Override
    public <T> T get(final String key)
    {
        return $.<T> cast(session.get(key));
    }

    @Override
    public void put(final String key, final Object value)
    {
        session.put(key, value);
    }

    final HashMap<String, Object> session = new HashMap<String, Object>();

}
