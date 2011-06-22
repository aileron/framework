package cc.aileron.webclient.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpCookie;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.impl.DefaultHttpClientConnection;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.util.EntityUtils;
import org.cyberneko.html.parsers.DOMParser;
import org.mozilla.intl.chardet.nsDetector;
import org.mozilla.intl.chardet.nsICharsetDetectionObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.html.HTMLDocument;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import cc.aileron.generic.$;
import cc.aileron.generic.ObjectContainer;
import cc.aileron.generic.Procedure;
import cc.aileron.generic.util.SkipList;
import cc.aileron.webclient.WebClient;
import cc.aileron.webclient.WebClientSSLSocketFactory;
import cc.aileron.webclient.WebClientSSLSocketFactoryImpl;
import cc.aileron.webclient.WebRequest;
import cc.aileron.webclient.WebRequestDefault;
import cc.aileron.webclient.WebRequestMethod;
import cc.aileron.webclient.WebRequestProxySetting;
import cc.aileron.webclient.WebResponse;
import cc.aileron.webclient.WebResponseStatus;
import cc.aileron.webclient.html.HtmlPage;
import cc.aileron.webclient.html.entity.HtmlElement;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author aileron
 */
@Singleton
public class WebClientImpl implements WebClient
{
    @Override
    public WebResponse<byte[]> getBytes(final WebRequest r)
            throws IOException, HttpException
    {
        final ObjectContainer<byte[]> b = new ObjectContainer<byte[]>();
        final ObjectContainer<String> c = new ObjectContainer<String>();
        final WebResponse<Void> res = request(r, new Procedure<HttpEntity>()
        {
            @Override
            public void call(final HttpEntity e)
            {
                try
                {
                    b.value = EntityUtils.toByteArray(e);
                    c.value = EntityUtils.getContentCharSet(e);
                }
                catch (final ParseException e1)
                {
                }
                catch (final IOException e1)
                {
                }
            }
        });
        final WebRequest request = res.request();
        return new WebResponse<byte[]>()
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
            public byte[] entity()
            {
                return entity;
            }

            @Override
            public WebRequest request()
            {
                return request;
            }

            @Override
            public WebResponseStatus status()
            {
                return status;
            }

            final String charset = c.value;
            final List<HttpCookie> cookies = res.cookies();
            final byte[] entity = b.value;
            final WebResponseStatus status = res.status();
        };
    }

    @Override
    public WebResponse<File> getFile(final WebRequest r)
            throws IOException, HttpException
    {
        final ObjectContainer<File> c = new ObjectContainer<File>();
        final ObjectContainer<String> d = new ObjectContainer<String>();
        final WebResponse<Void> res = request(r, new Procedure<HttpEntity>()
        {
            @Override
            public void call(final HttpEntity e)
            {
                try
                {
                    final String filepath = r.url()
                            .getFile()
                            .replaceAll(".*/(.*?)$", "$1");

                    final File file = File.createTempFile("WebClient", filepath);
                    file.deleteOnExit();

                    final FileOutputStream out = new FileOutputStream(file);
                    final InputStream in = e.getContent();

                    int len = -1;
                    final byte[] b = new byte[1024 * 20];
                    try
                    {
                        while ((len = in.read(b, 0, b.length)) != -1)
                        {
                            out.write(b, 0, len);
                        }
                        out.flush();
                    }
                    finally
                    {
                        if (in != null)
                        {
                            try
                            {
                                in.close();
                            }
                            catch (final IOException ie)
                            {
                            }
                        }
                    }
                    c.value = file;
                    d.value = EntityUtils.getContentCharSet(e);
                }
                catch (final IOException e1)
                {
                }
            }
        });
        return new WebResponse<File>()
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
            public File entity()
            {
                return entity;
            }

            @Override
            public WebRequest request()
            {
                return r;
            }

            @Override
            public WebResponseStatus status()
            {
                return status;
            }

            final String charset = d.value;
            final List<HttpCookie> cookies = res.cookies();
            final File entity = c.value;
            final WebResponseStatus status = res.status();
        };
    }

    @Override
    public WebResponse<HtmlPage> getPage(final WebRequest request)
            throws IOException, HttpException, ParseException, SAXException
    {
        final WebClient client = this;
        final WebResponse<byte[]> response = getBytes(request);
        final WebResponseStatus status = response.status();
        if (response.status().statusCode() == 404)
        {
            return new WebResponse<HtmlPage>()
            {

                @Override
                public String charset()
                {
                    return response.charset();
                }

                @Override
                public List<HttpCookie> cookies()
                {
                    return response.cookies();
                }

                @Override
                public HtmlPage entity()
                {
                    return null;
                }

                @Override
                public WebRequest request()
                {
                    return request;
                }

                @Override
                public WebResponseStatus status()
                {
                    return status;
                }

                @Override
                public String toString()
                {
                    return null;
                }
            };
        }

        final byte[] entity = response.entity();
        final String convertToCharset;

        if (response.charset() != null)
        {
            convertToCharset = response.charset();
        }
        else
        {
            final String tmpContent = new String(entity);
            final Matcher matcher;
            {
                final Matcher em = extractEncodingPattern.matcher(tmpContent);
                matcher = em != null ? em
                        : extractCharsetPattern.matcher(tmpContent);
            }

            if (matcher.find())
            {
                convertToCharset = matcher.group(1);
            }
            else
            {
                final nsDetector det = new nsDetector();
                class WebClientCharDetectionObserver implements
                        nsICharsetDetectionObserver
                {
                    public String detectedCharset()
                    {
                        return detectedCharset;
                    }

                    @Override
                    public void Notify(final String charset)
                    {
                        detectedCharset = charset;
                    }

                    private String detectedCharset = null;
                }

                final WebClientCharDetectionObserver observer = new WebClientCharDetectionObserver();

                det.Init(observer);
                if (det.isAscii(entity, entity.length))
                {
                    convertToCharset = "US-ASCII";
                }
                else
                {
                    det.DoIt(entity, entity.length, false);
                    convertToCharset = observer.detectedCharset() != null ? observer.detectedCharset()
                            : request.charset();
                }
                det.DataEnd();
            }
        }

        final String contentString = convertToCharset == null ? new String(entity)
                : new String(entity, convertToCharset);
        final DOMParser parser = new DOMParser();
        parser.setFeature("http://xml.org/sax/features/namespaces", false);
        parser.parse(new InputSource(new StringReader(contentString)));
        final HTMLDocument node = (HTMLDocument) parser.getDocument();

        final String url;
        try
        {
            url = response.request().url().toURI().toASCIIString();
        }
        catch (final URISyntaxException e)
        {
            throw new Error(e);
        }
        final HtmlPage page = new HtmlPage()
        {
            @Override
            public String asText()
            {
                return contentString;
            }

            @Override
            public String charset()
            {
                return convertToCharset;
            }

            @Override
            public List<HttpCookie> cookies()
            {
                return response.cookies();
            }

            @Override
            public WebResponse<HtmlPage> directJump(final String url)
                    throws ParseException, URISyntaxException, IOException,
                    HttpException, SAXException
            {
                return WebClientCommons.getPageDirect(client, response, url);
            }

            @Override
            public <T extends HtmlElement> List<T> getByXPath(final String xpath)
            {
                return XPath.<T> xpath(client, response, this, node, xpath);
            }

            @Override
            public WebResponse<HtmlPage> jump(final String url)
                    throws ParseException, URISyntaxException, IOException,
                    HttpException, SAXException
            {
                return WebClientCommons.getPage(client, response, url);
            }

            @Override
            public String title()
            {
                return node.getTitle();
            }

            @Override
            public String toString()
            {
                return contentString;
            }

            @Override
            public String url()
            {
                return url;

            }
        };

        return new WebResponse<HtmlPage>()
        {

            @Override
            public String charset()
            {
                return response.charset();
            }

            @Override
            public List<HttpCookie> cookies()
            {
                return response.cookies();
            }

            @Override
            public HtmlPage entity()
            {
                return page;
            }

            @Override
            public WebRequest request()
            {
                return request;
            }

            @Override
            public WebResponseStatus status()
            {
                return status;
            }

            @Override
            public String toString()
            {
                return contentString;
            }
        };
    }

    @Override
    public WebResponse<String> getString(final WebRequest r)
            throws IOException, HttpException
    {
        final WebResponse<byte[]> response = getBytes(r);
        final String charset;
        if (response.charset() != null)
        {
            charset = response.charset();
        }
        else if (r.charset() != null)
        {
            charset = r.charset();
        }
        else
        {
            charset = "ISO-8859-1"; // HTTP default charset
        }

        final String content = new String(response.entity(), charset);
        return new WebResponse<String>()
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
            public String entity()
            {
                return content;
            }

            @Override
            public WebRequest request()
            {
                return r;
            }

            @Override
            public WebResponseStatus status()
            {
                return status;
            }

            @Override
            public String toString()
            {
                return content;
            }

            final List<HttpCookie> cookies = response.cookies();
            final WebResponseStatus status = response.status();
        };
    }

    private List<HttpCookie> mergeCookies(final List<HttpCookie> request,
            final List<HttpCookie> response)
    {
        final HashMap<String, HttpCookie> cookieMap = new HashMap<String, HttpCookie>();
        if (request != null)
        {
            for (final HttpCookie cookie : request)
            {
                cookieMap.put(cookie.getName(), cookie);
            }
        }
        for (final HttpCookie cookie : response)
        {
            cookieMap.put(cookie.getName(), cookie);
        }
        final List<HttpCookie> cookies = new SkipList<HttpCookie>(cookieMap.values());
        return cookies;
    }

    private WebResponse<Void> request(final WebRequest r,
            final Procedure<HttpEntity> p)
            throws UnknownHostException, IOException, HttpException
    {
        return request(r, p, 0);
    }

    private WebResponse<Void> request(final WebRequest r,
            final Procedure<HttpEntity> p, final int deps)
            throws UnknownHostException, IOException, HttpException
    {
        if (r.url() == null)
        {
            throw new Error("URLが指定されていません");
        }

        final HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, r.charset());
        HttpProtocolParams.setUserAgent(params, r.userAgent());
        HttpProtocolParams.setUseExpectContinue(params, true);

        final BasicHttpProcessor httpproc = new BasicHttpProcessor();
        // Required protocol interceptors
        httpproc.addInterceptor(new RequestContent());
        httpproc.addInterceptor(new RequestTargetHost());
        // Recommended protocol interceptors
        httpproc.addInterceptor(new RequestConnControl());
        httpproc.addInterceptor(new RequestUserAgent());
        httpproc.addInterceptor(new RequestExpectContinue());

        if (logger.isDebugEnabled())
        {
            httpproc.addRequestInterceptor(new HttpRequestInterceptor()
            {
                @Override
                public void process(final HttpRequest request,
                        final HttpContext context)
                        throws HttpException, IOException
                {
                    final StringBuilder builder = new StringBuilder();
                    builder.append("Request "
                            + request.getRequestLine().toString() + "\n");
                    for (final Header header : request.getAllHeaders())
                    {
                        builder.append(header.getName() + ": "
                                + header.getValue() + "\n");
                    }

                    if (request instanceof BasicHttpEntityEnclosingRequest)
                    {
                        final HttpEntity entity = ((BasicHttpEntityEnclosingRequest) request).getEntity();

                        final byte[] buff = new byte[(int) entity.getContentLength()];
                        entity.getContent().read(buff);
                        builder.append(new String(buff));
                        builder.append("\n");
                    }

                    logger.debug(builder.toString());
                }
            });
            httpproc.addResponseInterceptor(new HttpResponseInterceptor()
            {
                @Override
                public void process(final HttpResponse response,
                        final HttpContext context)
                        throws HttpException, IOException
                {
                    final StringBuilder builder = new StringBuilder();
                    builder.append("Response " + response.getStatusLine()
                            + "\n");
                    for (final Header header : response.getAllHeaders())
                    {
                        builder.append(header.getName() + ": "
                                + header.getValue() + "\n");
                    }
                    logger.debug(builder.toString());
                }
            });
        }

        final HttpRequestExecutor httpexecutor = new HttpRequestExecutor();
        final DefaultHttpClientConnection conn = new DefaultHttpClientConnection();

        final HttpHost host;
        {
            final String hostname = r.url().getHost();
            final int portnumber = r.url().getPort();
            final int port = portnumber == -1 ? r.url().getDefaultPort()
                    : portnumber;
            host = new HttpHost(hostname, port);
        }

        final HttpContext context = new BasicHttpContext(null);
        if (r.proxy() != null)
        {
            final WebRequestProxySetting proxy = r.proxy();
            context.setAttribute(ExecutionContext.HTTP_PROXY_HOST,
                    new HttpHost(proxy.hostname(), proxy.port()));
            params.setParameter("Host", host.getHostName());
        }

        params.setParameter("Accept-Language", "ja,en-us;q=0.7,en;q=0.3");
        params.setParameter("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");

        context.setAttribute(ExecutionContext.HTTP_CONNECTION, conn);
        context.setAttribute(ExecutionContext.HTTP_TARGET_HOST, host);

        try
        {
            if (!conn.isOpen())
            {

                final Socket socket;
                {
                    if (r.proxy() != null)
                    {
                        socket = new Socket(r.proxy().hostname(), r.proxy()
                                .port());
                    }
                    else if (host.getPort() == 443)
                    {
                        socket = factory.get(host.getHostName(), 443);
                    }
                    else
                    {
                        socket = new Socket(host.getHostName(), host.getPort());
                    }
                }
                conn.bind(socket, params);
            }

            final String path = r.url().getPath() == null ? "/" : r.url()
                    .getPath();
            final String query = r.url().getQuery() == null ? "" : r.url()
                    .getQuery();

            final BasicHttpRequest request;
            if (r.method() == WebRequestMethod.GET)
            {
                final String q = r.params() == null || r.params().isEmpty() ? ""
                        : new BufferedReader(new InputStreamReader(new UrlEncodedFormEntity(r.params(),
                                r.charset()).getContent())).readLine();

                final String requestpath = path
                        + (query.isEmpty() ? "?" : "?" + query + "&") + q;

                final String requestUrl;
                if (r.proxy() != null)
                {
                    requestUrl = host.toURI().toString() + requestpath;
                }
                else
                {
                    requestUrl = requestpath;
                }

                request = new BasicHttpRequest(r.method().name(), requestUrl);
            }
            else
            {
                final String requestUrl = path
                        + (query.isEmpty() ? "" : "?" + query);
                final BasicHttpEntityEnclosingRequest req = new BasicHttpEntityEnclosingRequest(r.method()
                        .name(),
                        requestUrl);
                req.setEntity(new UrlEncodedFormEntity(r.params(), r.charset()));
                request = req;
            }

            /*
             * http params
             */
            request.setParams(params);

            /*
             * Cookie
             */
            if (r.cookies() != null && r.cookies().isEmpty() == false)
            {
                final List<String> builder = new SkipList<String>();
                for (final HttpCookie cookie : r.cookies())
                {
                    final String domain = r.url().getHost();
                    final String cdomain = cookie.getDomain() == null ? domain
                            : cookie.getDomain().indexOf('.') == 0 ? cookie.getDomain()
                                    .substring(1)
                                    : cookie.getDomain();

                    if (!domain.endsWith(cdomain))
                    {
                        continue;
                    }
                    builder.add(cookie.getName() + "=" + cookie.getValue());
                }

                final String cookie = $.join("; ", builder);
                request.addHeader("Cookie", cookie);
            }

            /*
             * Referer
             */
            if (r.referer() != null)
            {
                request.addHeader("Referer", r.referer().toString());
            }

            httpexecutor.preProcess(request, httpproc, context);
            final HttpResponse response = httpexecutor.execute(request,
                    conn,
                    context);
            response.setParams(params);
            httpexecutor.postProcess(response, httpproc, context);

            final List<HttpCookie> responseCookies = new SkipList<HttpCookie>();
            for (final Header h : response.getHeaders("Set-Cookie"))
            {
                final String cookie = h.getValue();
                try
                {
                    responseCookies.addAll(HttpCookie.parse(cookie));
                }
                catch (final IllegalArgumentException e)
                {
                }
            }
            final List<HttpCookie> cookies = mergeCookies(r.cookies(),
                    responseCookies);

            /*
             * リダイレクト
             */
            final Header location = response.getFirstHeader("Location");
            if (deps < 10 && location != null)
            {
                final URL locationUrl;
                {
                    URL tmp;
                    try
                    {
                        tmp = new URL(location.getValue());
                    }
                    catch (final MalformedURLException e)
                    {
                        final int nport = r.url().getPort();
                        tmp = new URL(r.url().getProtocol() + "://"
                                + r.url().getHost()
                                + (nport == -1 ? "" : ":" + nport)
                                + location.getValue());
                    }
                    locationUrl = tmp;
                }
                return request(new WebRequestDefault(r)
                {
                    @Override
                    public List<HttpCookie> cookies()
                    {
                        return cookies;
                    }

                    @Override
                    public URL url()
                    {
                        return locationUrl;
                    }
                }, p, deps + 1);
            }

            final StatusLine statusLine = response.getStatusLine();
            final int statusCode = statusLine.getStatusCode();
            final String protocolVersion = statusLine.getProtocolVersion()
                    .toString();
            final String reasonPhrase = statusLine.getReasonPhrase();

            try
            {
                p.call(response.getEntity());
            }
            catch (final Exception e)
            {
                throw new Error(e);
            }

            return new WebResponse<Void>()
            {
                @Override
                public String charset()
                {
                    return null;
                }

                @Override
                public List<HttpCookie> cookies()
                {
                    return cookies;
                }

                @Override
                public Void entity()
                {
                    return null;
                }

                @Override
                public WebRequest request()
                {
                    return r;
                }

                @Override
                public WebResponseStatus status()
                {
                    return status;
                }

                WebResponseStatus status = new WebResponseStatus()
                {
                    @Override
                    public String protocolVersion()
                    {
                        return protocolVersion;
                    }

                    @Override
                    public String reasonPhrase()
                    {
                        return reasonPhrase;
                    }

                    @Override
                    public int statusCode()
                    {
                        return statusCode;
                    }
                };
            };
        }
        finally
        {
            conn.close();
        }
    }

    /**
     * @throws Exception
     */
    public WebClientImpl() throws Exception
    {
        this.factory = new WebClientSSLSocketFactoryImpl();
    }

    /**
     * @param factory
     * @throws Exception
     */
    @Inject
    public WebClientImpl(final WebClientSSLSocketFactory factory)
            throws Exception
    {
        this.factory = factory;
    }

    final Pattern extractCharsetPattern = Pattern.compile("charset=(.*?)\"");
    final Pattern extractEncodingPattern = Pattern.compile("encoding=['\"](.*?)['\"]");
    final WebClientSSLSocketFactory factory;
    final Logger logger = LoggerFactory.getLogger(this.getClass());

}
