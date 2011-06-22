/**
 * 
 */
package cc.aileron.wsgi;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.Cookie;

import cc.aileron.generic.ConsCell;
import cc.aileron.generic.util.SkipList;
import cc.aileron.wsgi.Wsgi.Response;

/**
 * @author aileron
 */
public class WsgiResponse extends Wsgi.Response
{
    /**
     * HeaderItem
     */
    static class HeaderItem implements ConsCell<String, String>
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
        public boolean equals(final Object key)
        {
            return key.equals(this.key);
        }

        @Override
        public int hashCode()
        {
            return this.key.hashCode();
        }

        public HeaderItem(final String key, final String value)
        {
            this.key = key;
            this.value = value;
        }

        final String key;

        final String value;
    }

    /**
     * @throws Exception
     */
    public void commit() throws Exception
    {
        /*
         * header string builder
         */
        final StringBuilder builder = new StringBuilder();

        /*
         * http status code
         */
        builder.append(version)
                .append(' ')
                .append(status)
                .append(' ')
                .append(Server.MESSAGE.getProperty("Wsgi.Response.reasonPhrase."
                        + status))
                .append("\n");

        /*
         * redirect
         */
        if (redirectLocation != null)
        {
            builder.append("Location: ").append(redirectLocation).append('\n');
        }

        /*
         * header
         */
        for (final ConsCell<String, String> cell : header())
        {
            builder.append(cell.car())
                    .append(": ")
                    .append(cell.cdr())
                    .append('\n');
        }

        /*
         * cookie
         */
        for (final Cookie cookie : responseCookies)
        {
        }
        output.write(builder.append("\r\n").toString().getBytes());
        output.flush();

        /*
         * output content
         */
        if (streamProcesser != null)
        {
            streamProcesser.write(output);
        }
        if (printProcesser != null)
        {
            final PrintWriter writer = new PrintWriter(new OutputStreamWriter(output,
                    Server.ENCODING));
            printProcesser.write(writer);
            writer.flush();
        }
        output.flush();
    }

    @Override
    public Header header()
    {
        return header;
    }

    @Override
    public void out(final PrintWriterProcesser processer)
    {
        printProcesser = processer;
        streamProcesser = null;
    }

    @Override
    public void out(final StreamWriteProcesser processer)
    {
        printProcesser = null;
        streamProcesser = processer;
    }

    @Override
    public void redirect(final String location) throws IOException
    {
        status = 307;
        redirectLocation = location;
    }

    /**
     * @param output
     * @param version
     */
    public WsgiResponse(final OutputStream output, final String version)
    {
        this.output = output;
        this.version = version;
        header = new Response.Header()
        {

            @Override
            public Header add(final Cookie cookie)
            {
                responseCookies.add(cookie);
                return this;
            }

            @Override
            public Header add(final String key, final String value)
            {
                responseHeader.add(new HeaderItem(key, value));
                return this;
            }

            @Override
            public Iterator<ConsCell<String, String>> iterator()
            {
                return responseHeader.iterator();
            }

            @Override
            public Header set(final String key, final String value)
            {
                final HeaderItem item = new HeaderItem(key, value);
                responseHeader.remove(item);
                responseHeader.add(item);
                return this;
            }

            @Override
            public int status()
            {
                return status;
            }

            @Override
            public Header status(final int code)
            {
                status = code;
                return this;
            }

            final List<ConsCell<String, String>> responseHeader = new SkipList<ConsCell<String, String>>();
        };
    }

    final Header header;

    final OutputStream output;
    PrintWriterProcesser printProcesser;
    String redirectLocation;
    final ArrayList<Cookie> responseCookies = new ArrayList<Cookie>();
    int status = 200;
    StreamWriteProcesser streamProcesser;
    final String version;
}
