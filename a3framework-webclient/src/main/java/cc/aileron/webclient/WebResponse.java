/**
 *
 */
package cc.aileron.webclient;

import java.net.HttpCookie;
import java.util.List;

/**
 * @author aileron
 * @param <T>
 */
public interface WebResponse<T>
{
    /**
     * @return charset
     */
    String charset();

    /**
     * @return cookies
     */
    List<HttpCookie> cookies();

    /**
     * @return entity
     */
    T entity();

    /**
     * @return {@link WebRequest}
     */
    WebRequest request();

    /**
     * @return {@link WebResponseStatus}
     */
    WebResponseStatus status();
}
