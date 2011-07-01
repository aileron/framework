/**
 * 
 */
package cc.aileron.wsgi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.Map;

import javax.servlet.http.Cookie;

import cc.aileron.generic.ConsCell;
import cc.aileron.wsgi.Wsgi.Response.PrintWriterProcesser;
import cc.aileron.wsgi.Wsgi.Response.StreamWriteProcesser;

/**
 * Wsgi
 */
public abstract class Wsgi
{
    /**
     * Context
     */
    public static abstract class Context
    {
        /**
         * @return {@link Request}
         */
        public abstract Request request();

        /**
         * @return {@link Response}
         */
        public abstract Response response();

        /**
         * @return {@link Session}
         */
        public abstract Session session();
    }

    /**
     * Wsgi.Method
     */
    public static enum Method
    {
        /**
         * DELETE
         */
        DELETE,

        /**
         * GET
         */
        GET,

        /**
         * HEAD
         */
        HEAD,

        /**
         * POST
         */
        POST,

        /**
         * PUT
         */
        PUT;
    }

    /**
     * リクエスト情報
     */
    public static interface Request
    {
        /**
         * Request-Content
         */
        interface Content
        {
            int length();

            InputStream stream();

            String type();
        }

        /**
         * @return リクエストスコープで保持する為の、属性
         */
        Map<String, Object> attributes();

        /**
         * @return {@link Content}
         */
        Content content();

        /**
         * @return {@link Cookie}
         */
        Cookie[] cookie();

        /**
         * @return リクエストヘッダ
         */
        Map<String, String> header();

        /**
         * @return {@link Method}
         */
        Method method();

        /**
         * @return リクエストパラメータ
         */
        Map<String, Object> parameter();

        /**
         * @return リクエストパス
         */
        String path();

        /**
         * @return クエリーストリングス
         */
        String query();

        /**
         * @return {@link InetAddress}
         */
        InetAddress remoteAddress();
    }

    /**
     * レスポンス
     */
    public static interface Response
    {
        /**
         * Response-Header
         */
        public static interface Header extends
                Iterable<ConsCell<String, String>>
        {
            /**
             * @param cookie
             * @return {@link Header}
             */
            Header add(Cookie cookie);

            /**
             * @param key
             * @param value
             * @return {@link Header}
             */
            Header add(String key, String value);

            /**
             * @param key
             * @param value
             * @return {@link Header}
             */
            Header set(String key, String value);

            /**
             * @return status
             */
            int status();

            /**
             * @param code
             * @return {@link Header}
             */
            Header status(int code);
        }

        /**
         * レスポンスの内容を返す為の、プロセッサ 出力バッファリングをしない為のコールバック
         * 
         * Streamと、ちがって、PrintWriterを使う
         */
        public static interface PrintWriterProcesser
        {
            /**
             * @param writer
             */
            void write(PrintWriter writer);
        }

        /**
         * レスポンスの内容を返す為の、プロセッサ 出力バッファリングをしない為のコールバック
         */
        public static interface StreamWriteProcesser
        {
            /**
             * @param stream
             * @param writer
             * @throws Exception
             */
            void write(OutputStream stream) throws Exception;
        }

        /**
         * @return {@link Header}
         */
        Header header();

        /**
         * @param processer
         */
        void out(PrintWriterProcesser processer);

        /**
         * @param processer
         */
        void out(StreamWriteProcesser processer);

        /**
         * @param location
         * @throws IOException
         */
        void redirect(String location) throws IOException;
    }

    /**
     * Session
     */
    public static interface Session
    {
        /**
         * Session Handler
         */
        interface Handler
        {
            /**
             * ハンドラーよりセッションを取得する
             * 
             * @param request
             * @param response
             * @return {@link Session}
             * @throws IOException
             * @throws ClassNotFoundException
             */
            Session get(Request request, Response response)
                    throws IOException, ClassNotFoundException;

            /**
             * セッションを永続化する
             * 
             * @param session
             * @param request
             * @param response
             * @throws IOException
             */
            void put(Session session, Request request, Response response)
                    throws IOException;
        }

        /**
         * @param <T>
         * @param key
         * @return value
         */
        <T> T get(String key);

        /**
         * @param key
         * @param value
         */
        void put(String key, Object value);
    }

    /**
     * thread-local-context
     */
    public static final ThreadLocal<Context> Context = new ThreadLocal<Context>();

    /**
     * @return {@link Context}
     */
    public static Context Context()
    {
        return Context.get();
    }

    /**
     * @param context
     */
    public static void Context(final Context context)
    {
        Context.set(context);
    }

    /**
     * @param wsgi
     * @throws Exception
     */
    public static void execute(final Wsgi wsgi) throws Exception
    {
        wsgi.execute(Context.get());
    }

    /**
     * @return {@link Request}
     */
    public static Request Request()
    {
        return Context().request();
    }

    /**
     * @return {@link Response}
     */
    public static Response Response()
    {
        return Context().response();
    }

    /**
     * @param processer
     */
    public static void Response(final PrintWriterProcesser processer)
    {
        Response().out(processer);
    }

    /**
     * @param processer
     */
    public static void Response(final StreamWriteProcesser processer)
    {
        Response().out(processer);
    }

    /**
     * @param context
     * @throws Exception
     */
    public abstract void execute(Context context) throws Exception;
}
