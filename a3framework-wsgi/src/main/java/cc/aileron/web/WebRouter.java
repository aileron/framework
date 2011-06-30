/**
 * 
 */
package cc.aileron.web;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Enumeration;
import java.util.HashMap;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.aileron.generic.$;
import cc.aileron.generic.Resource;
import cc.aileron.generic.Resource.Loader;
import cc.aileron.web.WebProcess.Case;
import cc.aileron.wsgi.Wsgi;
import cc.aileron.wsgi.Wsgi.Method;
import cc.aileron.wsgi.Wsgi.Request;

/**
 * @author aileron
 */
public class WebRouter implements Filter
{
    @Override
    public void destroy()
    {
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
                return p;
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

            final HashMap<String, Object> p;
            final String query;

            {
                for (final Enumeration<String> e = $.cast(req.getAttributeNames()); e.hasMoreElements();)
                {
                    final String name = e.nextElement();
                    attr.put(name, req.getAttribute(name));
                }
                cookie = req.getCookies();
                query = req.getQueryString();
                p = $.cast(req.getParameterMap());

                for (final Enumeration<String> e = $.cast(req.getHeaderNames()); e.hasMoreElements();)
                {
                    final String name = e.nextElement();
                    header.put(name, req.getHeader(name));
                }

            }
        };

        class WebResponse implements Wsgi.Response
        {

            @Override
            public Header header()
            {
                return null;
            }

            @Override
            public void out(final PrintWriterProcesser processer)
            {

            }

            @Override
            public void out(final StreamWriteProcesser processer)
            {

            }

            @Override
            public void redirect(final String location) throws IOException
            {

            }
        }
        ;

        // WebContext.set(c, req, res, parameter);
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
            container = new WebModel(Resource.Loader.get("wsgi.properties")
                    .toProperties()).get();
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
}
