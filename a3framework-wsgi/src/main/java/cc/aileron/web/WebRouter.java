/**
 * 
 */
package cc.aileron.web;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.aileron.generic.$;
import cc.aileron.generic.ConsCell;
import cc.aileron.generic.Resource;
import cc.aileron.generic.Resource.Loader;
import cc.aileron.web.WebProcess.Case;
import cc.aileron.wsgi.Wsgi;
import cc.aileron.wsgi.Wsgi.Method;
import cc.aileron.wsgi.Wsgi.Request;
import cc.aileron.wsgi.Wsgi.Response;
import cc.aileron.wsgi.Wsgi.Response.Header;
import cc.aileron.wsgi.Wsgi.Session;

/**
 * @author aileron
 */
public class WebRouter implements Filter
{
    @Override
    public void destroy()
    {
        Wsgi.Context.remove();
    }

    @Override
    public void doFilter(final ServletRequest request,
            final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException
    {
        final HttpServletRequest req = (HttpServletRequest) request;
        final HttpServletResponse res = (HttpServletResponse) response;

        final Method method = Method.valueOf(req.getMethod());
        final String servletPath = req.getServletPath();
        final String pathInfo = req.getPathInfo() != null ? req.getPathInfo()
                : "";
        final String requestURI = servletPath + pathInfo;
        final HashMap<String, Object> parameter = new HashMap<String, Object>($.<Map<String, Object>> cast(req.getParameterMap()));
        final WebBinder.Setting<Object> set = container.get(method,
                requestURI,
                parameter);
        if (set == null)
        {
            chain.doFilter(request, response);
            return;
        }

        final Request r = new Wsgi.Request()
        {
            @Override
            public Map<String, Object> attributes()
            {
                return attr;
            }

            @Override
            public Content content()
            {
                return null;
            }

            @Override
            public Cookie[] cookie()
            {
                return cookie;
            }

            @Override
            public Map<String, String> header()
            {
                return header;
            }

            @Override
            public Method method()
            {
                return method;
            }

            @Override
            public Map<String, Object> parameter()
            {
                return parameter;
            }

            @Override
            public String path()
            {
                return requestURI;
            }

            @Override
            public String query()
            {
                return query;
            }

            @Override
            public InetAddress remoteAddress()
            {
                return null;
            }

            final HashMap<String, Object> attr = new HashMap<String, Object>();
            final Cookie[] cookie;

            final HashMap<String, String> header = new HashMap<String, String>();
            final String query;

            {
                for (final Enumeration<String> e = $.cast(req.getAttributeNames()); e.hasMoreElements();)
                {
                    final String name = e.nextElement();
                    attr.put(name, req.getAttribute(name));
                }
                cookie = req.getCookies();
                query = req.getQueryString();

                for (final Enumeration<String> e = $.cast(req.getHeaderNames()); e.hasMoreElements();)
                {
                    final String name = e.nextElement();
                    header.put(name, req.getHeader(name));
                }

            }
        };

        final Header h = new Wsgi.Response.Header()
        {
            @Override
            public Header add(final Cookie cookie)
            {
                cookies.add(cookie);
                return this;
            }

            @Override
            public Header add(final String key, final String value)
            {
                headers.add(cell(key, value));
                return this;
            }

            public ConsCell<String, String> cell(final String key,
                    final String value)
            {
                return new ConsCell<String, String>()
                {

                    @Override
                    public String car()
                    {
                        return key;
                    }

                    @Override
                    public String cdr()
                    {
                        return value;
                    }

                    @Override
                    public boolean equals(final Object obj)
                    {
                        return obj instanceof ConsCell
                                && $.<ConsCell<String, String>> cast(obj).car() == car();
                    }

                    @Override
                    public int hashCode()
                    {
                        return car().hashCode();
                    }
                };
            }

            @Override
            public Iterator<ConsCell<String, String>> iterator()
            {
                return headers.iterator();
            }

            @Override
            public Header set(final String key, final String value)
            {
                final ConsCell<String, String> cell = cell(key, value);
                headers.remove(cell);
                headers.add(cell);
                return this;
            }

            @Override
            public int status()
            {
                return status;
            }

            @Override
            public Header status(final int status)
            {
                this.status = status;
                return this;
            }

            final LinkedList<Cookie> cookies = new LinkedList<Cookie>();

            final LinkedList<ConsCell<String, String>> headers = new LinkedList<ConsCell<String, String>>();

            int status = 200;
        };

        class Res implements Wsgi.Response
        {
            @Override
            public Header header()
            {
                return h;
            }

            @Override
            public void out(final PrintWriterProcesser processer)
            {
                this.print = processer;
            }

            @Override
            public void out(final StreamWriteProcesser processer)
            {
                stream = processer;
            }

            @Override
            public void redirect(final String location) throws IOException
            {
                this.location = location;
            }

            public String location;

            public PrintWriterProcesser print;

            public StreamWriteProcesser stream;
        }
        final Res l = new Res();
        final Wsgi.Session s = new Wsgi.Session()
        {

            @Override
            public <T> T get(final String key)
            {
                return $.cast(session.getAttribute(key));
            }

            @Override
            public void put(final String key, final Object value)
            {
                session.setAttribute(key, value);
            }

            HttpSession session = req.getSession(true);
        };
        Wsgi.Context(new Wsgi.Context()
        {
            @Override
            public Request request()
            {
                return r;
            }

            @Override
            public Response response()
            {
                return l;
            }

            @Override
            public Session session()
            {
                return s;
            }
        });
        final Object resource = set.resource();
        if (logger.isTraceEnabled())
        {
            logger.trace("{} {} ({}) parameter:{}", new Object[] { method,
                    requestURI, resource.getClass(), parameter });
        }

        try
        {
            for (final WebProcess<Object> process : $.<List<WebProcess<Object>>> cast(set.process()))
            {
                final Case state;
                try
                {
                    state = process.process(resource);
                }
                catch (final Throwable throwable)
                {
                    logger.error(String.format("%s %s (%s) %s",
                            method,
                            requestURI,
                            resource.getClass(),
                            process), throwable);
                    break;
                }
                if (logger.isTraceEnabled())
                {
                    logger.trace("{} {} ({}) {}", new Object[] { method,
                            requestURI, resource.getClass(), process });
                }
                if (state == WebProcess.Case.TERMINATE)
                {
                    return;
                }
            }
        }
        finally
        {
            Wsgi.Context(null);
            if (l.location != null)
            {
                Wsgi.Context(null);
                res.sendRedirect(l.location);
                return;
            }
            if (l.stream != null)
            {
                try
                {
                    l.stream.write(res.getOutputStream());
                    Wsgi.Context(null);
                }
                catch (final Exception e)
                {
                    logger.error("write", e);
                }
                return;
            }
            if (l.print != null)
            {
                l.print.write(new PrintWriter(new OutputStreamWriter(response.getOutputStream(),
                        Charset.forName("UTF-8"))));
                Wsgi.Context(null);
                return;
            }
        }
    }

    @Override
    public void init(final FilterConfig config) throws ServletException
    {
        c = config.getServletContext();
        try
        {
            final String basedir = c.getRealPath("/");
            Loader.append(new Resource.StreamLoader()
            {
                @Override
                public InputStream load(final String path)
                {
                    try
                    {
                        final String location = basedir + "/" + path;
                        return new FileInputStream(location);
                    }
                    catch (final FileNotFoundException e)
                    {
                        return null;
                    }
                }
            });
            model = new WebModel(Resource.Loader.get("wsgi.properties")
                    .toProperties());
            container = model.get();
        }
        catch (final Exception e)
        {
            logger.error("init-error", e);
            throw new ServletException(e);
        }
    }

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private ServletContext c;
    private WebBinder.Container container;
    private WebModel model;
}
