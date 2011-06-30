/**
 * 
 */
package cc.aileron.web;

import static cc.aileron.web.WebProcess.Case.*;

import java.net.InetAddress;
import java.util.Map;

import javax.servlet.http.Cookie;

import cc.aileron.generic.$;
import cc.aileron.pojo.PojoAccessor;
import cc.aileron.pojo.PojoAccessorRepository;
import cc.aileron.wsgi.Parameter;
import cc.aileron.wsgi.Wsgi;
import cc.aileron.wsgi.Wsgi.Context;
import cc.aileron.wsgi.Wsgi.Method;
import cc.aileron.wsgi.Wsgi.Request;
import cc.aileron.wsgi.Wsgi.Response;
import cc.aileron.wsgi.Wsgi.Session;

/**
 * WEB の 遷移に関して
 */
public class WebTransition
{
    /**
     * NotFound
     */
    public static final WebProcess<Object> notfound = new WebProcess<Object>()
    {
        @Override
        public cc.aileron.web.WebProcess.Case process(final Object resource)
                throws Exception
        {
            final Response response = Wsgi.Response();
            response.header().status(404);
            return TERMINATE;
        }

        @Override
        public String toString()
        {
            return "notfound";
        }
    };

    /**
     * @return パス
     */
    public static String beforeRequestPath()
    {
        final Request request = Wsgi.Request();
        try
        {
            return (String) request.attributes()
                    .get("WebTransition.beforeRequestPath");
        }
        finally
        {
            request.attributes().put("WebTransition.beforeRequestPath",
                    request.path());
        }
    }

    /**
     * @param path
     * @return {@link WebProcess}
     */
    public static final WebProcess<Object> forward(final String path)
    {
        final WebUrlTemplate url = new WebUrlTemplate(path);
        return new WebProcess<Object>()
        {
            @Override
            public cc.aileron.web.WebProcess.Case process(final Object resource)
                    throws Exception
            {
                beforeRequestPath();
                final String requestPath = url.replace(accessorRepository.from(resource));
                final Context context = Wsgi.Context();
                final String[] token = requestPath.split("\\?");
                final String path = token[0];
                final String query = token.length == 2 ? token[1] : "";
                final Map<String, Object> parameter = Parameter.query(query);
                final Request request = new Request()
                {
                    @Override
                    public Map<String, Object> attributes()
                    {
                        return old.attributes();
                    }

                    @Override
                    public Content content()
                    {
                        return old.content();
                    }

                    @Override
                    public Cookie[] cookie()
                    {
                        return old.cookie();
                    }

                    @Override
                    public Map<String, String> header()
                    {
                        return old.header();
                    }

                    @Override
                    public Method method()
                    {
                        return old.method();
                    }

                    @Override
                    public Map<String, Object> parameter()
                    {
                        return parameter;
                    }

                    @Override
                    public String path()
                    {
                        return path;
                    }

                    @Override
                    public String query()
                    {
                        return query;
                    }

                    @Override
                    public InetAddress remoteAddress()
                    {
                        return old.remoteAddress();
                    }

                    final Request old = context.request();

                };
                Wsgi.Context(new Context()
                {
                    @Override
                    public Request request()
                    {
                        return request;
                    }

                    @Override
                    public Response response()
                    {
                        return response;
                    }

                    @Override
                    public Session session()
                    {
                        return session;
                    }

                    final Response response = context.response();
                    final Session session = context.session();
                });

                return Case.RETRY;
            }

            @Override
            public String toString()
            {
                return "forward[" + path + "]";
            }

            PojoAccessorRepository accessorRepository = PojoAccessor.Repository;
        };
    }

    /**
     * @param <T>
     * @param judge
     * @param process
     * @return {@link WebProcess}
     */
    public static <T> WebProcess<T> judge(final WebProcess<? super T> judge,
            final WebProcess<? super T> process)
    {
        final WebProcess<T> j = $.cast(judge);
        final WebProcess<T> p = $.cast(process);
        return new WebProcess<T>()
        {
            @Override
            public cc.aileron.web.WebProcess.Case process(final T resource)
                    throws Exception
            {
                if (j.process(resource) == CONTINUE)
                {
                    return CONTINUE;
                }
                return p.process(resource);
            }

            @Override
            public String toString()
            {
                return "judge[" + judge + "," + process + "]";
            }
        };
    }

    /**
     * @return notfound
     */
    public static final WebProcess<Object> notfound()
    {
        return notfound;
    }

    /**
     * @param path
     * @return {@link WebProcess} リダイレクト
     */
    public static final WebProcess<Object> redirect(final String path)
    {
        final WebUrlTemplate url = new WebUrlTemplate(path);
        return new WebProcess<Object>()
        {
            @Override
            public Case process(final Object resource) throws Exception
            {
                final String location = url.replace(accessorRepository.from(resource));
                Wsgi.Response().redirect(location);
                return TERMINATE;
            }

            @Override
            public String toString()
            {
                return "redirect[" + path + "]";
            }

            PojoAccessorRepository accessorRepository = PojoAccessor.Repository;
        };
    }

}
