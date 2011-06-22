/**
 *
 */
package cc.aileron.webclient;

import java.net.HttpCookie;
import java.net.URL;
import java.util.List;

import org.apache.http.NameValuePair;

/**
 * @author aileron
 */
public interface WebRequest
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
     * @return {@link WebRequestMethod}
     */
    WebRequestMethod method();

    /**
     * @return params
     */
    List<? extends NameValuePair> params();

    /**
     * @return {@link WebRequestProxySetting}
     */
    WebRequestProxySetting proxy();

    /**
     * @return referer
     */
    URL referer();

    /**
     * @return {@link URL}
     */
    URL url();

    /**
     * @return user-agent
     */
    String userAgent();
}
