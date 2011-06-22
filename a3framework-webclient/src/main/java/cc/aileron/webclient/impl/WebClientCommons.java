/**
 * 
 */
package cc.aileron.webclient.impl;

import java.io.File;
import java.io.IOException;
import java.net.HttpCookie;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpException;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.xml.sax.SAXException;

import cc.aileron.webclient.WebClient;
import cc.aileron.webclient.WebRequest;
import cc.aileron.webclient.WebRequestDefault;
import cc.aileron.webclient.WebRequestMethod;
import cc.aileron.webclient.WebRequestProxySetting;
import cc.aileron.webclient.WebResponse;
import cc.aileron.webclient.html.HtmlPage;

/**
 * WebClient Commons
 * 
 * @author aileron
 */
public class WebClientCommons
{

    /**
     * 画像のダウンロード
     * 
     * @param client
     * @param request
     * @param src
     * @return {@link File}
     * @throws URISyntaxException
     * @throws HttpException
     * @throws IOException
     */
    public static File getImageFile(final WebClient client,
            final WebResponse<?> request, final String src)
            throws URISyntaxException, IOException, HttpException
    {
        final URL url = request.request().url().toURI().resolve(src).toURL();
        final WebResponse<File> response = client.getFile(new WebRequestDefault(request)
        {
            @Override
            public URL url()
            {
                return url;
            }
        });
        return response.entity();
    }

    /**
     * @param client
     * @param response
     * @param requesturl
     * @return {@link WebResponse}
     * @throws URISyntaxException
     * @throws ParseException
     * @throws IOException
     * @throws HttpException
     * @throws SAXException
     */
    public static WebResponse<HtmlPage> getPage(final WebClient client,
            final WebResponse<?> response, final String requesturl)
            throws URISyntaxException, ParseException, IOException,
            HttpException, SAXException
    {
        final String charset = response.charset();
        final WebRequestProxySetting proxy = response.request().proxy();
        final String userAgent = response.request().userAgent();
        final URL url = response.request().url();
        final URL nexturl = url.toURI().resolve(requesturl).toURL();
        final List<HttpCookie> cookies = response.cookies();
        return client.getPage(new WebRequest()
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
                return WebRequestMethod.GET;
            }

            @Override
            public List<NameValuePair> params()
            {
                return null;
            }

            @Override
            public WebRequestProxySetting proxy()
            {
                return proxy;
            }

            @Override
            public URL referer()
            {
                return url;
            }

            @Override
            public URL url()
            {
                return nexturl;
            }

            @Override
            public String userAgent()
            {
                return userAgent;
            }
        });
    }

    /**
     * @param client
     * @param response
     * @param requesturl
     * @return {@link WebResponse}
     * @throws URISyntaxException
     * @throws ParseException
     * @throws IOException
     * @throws HttpException
     * @throws SAXException
     */
    public static WebResponse<HtmlPage> getPageDirect(final WebClient client,
            final WebResponse<?> response, final String requesturl)
            throws URISyntaxException, ParseException, IOException,
            HttpException, SAXException
    {
        final String charset = response.charset();
        final WebRequestProxySetting proxy = response.request().proxy();
        final String userAgent = response.request().userAgent();
        final URL url = response.request().url();
        final URL nexturl = new URL(requesturl);
        final List<HttpCookie> cookies = response.cookies();
        return client.getPage(new WebRequest()
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
                return WebRequestMethod.GET;
            }

            @Override
            public List<NameValuePair> params()
            {
                return null;
            }

            @Override
            public WebRequestProxySetting proxy()
            {
                return proxy;
            }

            @Override
            public URL referer()
            {
                return url;
            }

            @Override
            public URL url()
            {
                return nexturl;
            }

            @Override
            public String userAgent()
            {
                return userAgent;
            }
        });
    }

    /**
     * @param client
     * @param response 
     * @param requesturl
     * @param methodName
     * @param p
     * @return {@link WebResponse}
     * @throws URISyntaxException
     * @throws ParseException
     * @throws IOException
     * @throws HttpException
     * @throws SAXException
     */
    public static WebResponse<HtmlPage> send(final WebClient client,
            final WebResponse<?> response, final String requesturl,
            final String methodName, final List<? extends NameValuePair> p)
            throws URISyntaxException, ParseException, IOException,
            HttpException, SAXException
    {
        final String charset = response.charset();
        final WebRequestProxySetting proxy = response.request().proxy();
        final String userAgent = response.request().userAgent();
        final URL url = response.request().url();
        final URL nexturl = url.toURI().resolve(requesturl).toURL();
        final List<HttpCookie> cookies = response.cookies();
        final WebRequestMethod method = methodName == null
                || methodName.isEmpty() ? WebRequestMethod.GET
                : WebRequestMethod.valueOf(methodName.toUpperCase());
        return client.getPage(new WebRequest()
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
            public List<? extends NameValuePair> params()
            {
                return p;
            }

            @Override
            public WebRequestProxySetting proxy()
            {
                return proxy;
            }

            @Override
            public URL referer()
            {
                return url;
            }

            @Override
            public URL url()
            {
                return nexturl;
            }

            @Override
            public String userAgent()
            {
                return userAgent;
            }
        });
    }
}
