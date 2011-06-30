/**
 * 
 */
package cc.aileron.wsgi;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Properties;

import cc.aileron.generic.ObjectReference;
import cc.aileron.generic.util.ReflectionToString;
import cc.aileron.generic.util.URLTranslator;
import cc.aileron.generic.util.WorkQueue;
import cc.aileron.web.WebBinder;
import cc.aileron.web.WebModel;
import cc.aileron.wsgi.Wsgi.Context;
import cc.aileron.wsgi.Wsgi.Request;
import cc.aileron.wsgi.Wsgi.Response;
import cc.aileron.wsgi.Wsgi.Session;

/**
 * サーバー
 */
public class Server
{
    /**
     * サーバーの文字コード
     */
    static final Charset ENCODING = Charset.forName("UTF-8");

    /**
     * メッセージリソース
     */
    static final String[][] MESSAGE = {
            { "Continue", "Switching Protocols", "Processing", },
            { "OK", "Created", "Accepted", "Non-Authoritative Information",
                    "No Content", "Reset Content", "Partial Content",
                    "Multi-Status", },
            { "Multiple Choices", "Moved Permanently", "Found", "See Other",
                    "Not Modified", "Use Proxy", "unused",
                    "Temporary Redirect", },
            { "Bad Request", "Authorization Required", "Payment Required",
                    "Forbidden", "Not Found", "Method Not Allowed",
                    "Not Acceptable", "Proxy Authentication Required",
                    "Request Time-out", "Conflict", "Gone", "Length Required",
                    "Precondition Failed", "Request Entity Too Large",
                    "Request-URI Too Large", "Unsupported Media Type",
                    "Requested Range Not Satisfiable", "Expectation Failed",
                    "unused", "unused", "unused", "unused",
                    "Unprocessable Entity", "Locked", "Failed Dependency",
                    "No code", "Upgrade Required", },
            { "Internal Server Error", "Method Not Implemented", "Bad Gateway",
                    "Service Temporarily Unavailable", "Gateway Time-out",
                    "HTTP Version Not Supported", "Variant Also Negotiates",
                    "Insufficient Storage", "unused", "unused", "Not Extended", }, };

    /**
     * ポート
     */
    static final int PORT = Integer.parseInt(System.getProperty("port", "8080"));

    /**
     * URLデコード/エンコード
     */
    static final URLTranslator URL = URLTranslator.factory.get(ENCODING);

    /**
     * 
     * 
     * @param client
     * @param p
     * @param loader
     * @throws Exception
     */
    public static void exec(final Socket client, final Properties p,
            final ClassLoader loader) throws Exception
    {
        final long time = System.currentTimeMillis();
        final Router router = new Router(new WebModel(p, loader));
        final long moduleInitTime = System.currentTimeMillis() - time;
        System.err.println("module initialized " + moduleInitTime + "ms.");
        exec(client, router);
    }

    /**
     * @throws Exception
     */
    public static void main() throws Exception
    {
        /*
         * config
         */
        final Properties p = new Properties();
        p.load(ClassLoader.getSystemResourceAsStream("wsgi.properties"));
        final ObjectReference<WebBinder.Container> hier = new WebModel(p);

        /*
         * application 起動
         */
        final long time = System.currentTimeMillis();
        final Router router = new Router(hier);
        final long moduleInitTime = System.currentTimeMillis() - time;

        /*
         * リッスン開始
         */
        final WorkQueue workQueue = new WorkQueue(10);
        final ServerSocket server;
        try
        {
            server = new ServerSocket(PORT);
        }
        catch (final java.net.BindException e)
        {
            throw new Error(e);
        }
        System.err.println("Http Server " + PORT + " (module initialized "
                + moduleInitTime + "ms.) started. Yeah!!!");
        for (;;)
        {
            final Socket client = server.accept();
            workQueue.execute(new Runnable()
            {
                @Override
                public void run()
                {
                    exec(client, router);
                }
            });
        }
    }

    /**
     * @param args
     */
    public static void main(final String[] args)
    {
        try
        {
            main();
        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }
    }

    static void exec(final Socket client, final Router router)
    {
        try
        {
            final InetAddress address = client.getInetAddress();
            final WsgiRequest req = new WsgiRequest(address,
                    client.getInputStream());
            final WsgiResponse res = new WsgiResponse(client.getOutputStream());

            try
            {
                router.execute(new Context()
                {
                    @Override
                    public Request request()
                    {
                        return req;
                    }

                    @Override
                    public Response response()
                    {
                        return res;
                    }

                    @Override
                    public Session session()
                    {
                        return null;
                    }

                    @Override
                    public String toString()
                    {
                        return ReflectionToString.toString(this);
                    }
                });
                res.commit();
            }
            catch (final Exception e)
            {
                e.printStackTrace();
            }
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                client.close();
            }
            catch (final IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
