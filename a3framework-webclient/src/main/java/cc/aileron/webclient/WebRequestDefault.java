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
public abstract class WebRequestDefault implements WebRequest
{
    @Override
    public String charset()
    {
        return charset;
    }

    @Override
    public List<HttpCookie> cookies()
    {
        return cookies;
    }

    @Override
    public WebRequestMethod method()
    {
        return method;
    }

    @Override
    public List<NameValuePair> params()
    {
        return params;
    }

    @Override
    public WebRequestProxySetting proxy()
    {
        return proxy;
    }

    @Override
    public URL referer()
    {
        return referer;
    }

    @Override
    public String userAgent()
    {
        return userAgent;
    }

    /**
     */
    public WebRequestDefault()
    {
    }

    /**
     * @param r
     */
    public WebRequestDefault(final WebRequest r)
    {
        this.charset = r.charset();
        this.cookies = r.cookies();
        this.proxy = r.proxy();
        this.referer = r.url();
        this.userAgent = r.userAgent();
    }

    /**
     * @param r
     */
    public WebRequestDefault(final WebResponse<?> r)
    {
        this.charset = r.charset();
        this.cookies = r.cookies();
        this.proxy = r.request().proxy();
        this.referer = r.request().url();
        this.userAgent = r.request().userAgent();
    }

    private String charset = "UTF-8";

    private List<HttpCookie> cookies;

    private final WebRequestMethod method = WebRequestMethod.GET;

    private List<NameValuePair> params;

    private WebRequestProxySetting proxy;

    private URL referer;

    private String userAgent = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.6; ja-JP-mac; rv:1.9.1.4) Gecko/20091016 Firefox/3.5.4 GTB7.0";
}
