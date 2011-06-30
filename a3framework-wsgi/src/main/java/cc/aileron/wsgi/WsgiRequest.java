/**
 * 
 */
package cc.aileron.wsgi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;

import cc.aileron.generic.util.ReflectionToString;
import cc.aileron.wsgi.Wsgi.Method;
import cc.aileron.wsgi.Wsgi.Request;

/**
 * @author aileron
 */
public class WsgiRequest implements Wsgi.Request
{
    static class WsgiInputStream extends javax.servlet.ServletInputStream
    {
        @Override
        public int available() throws IOException
        {
            return this.inData.available();
        }

        /**
         * 
         */
        public void finishRequest()
        {
            // this.inData = null;
            // byte content[] = this.dump.toByteArray();
            // com.rickknowles.winstone.ajp13.Ajp13Listener.packetDump(content,
            // content.length);
        }

        /**
         * @return {@link InputStream}
         */
        public InputStream getRawInputStream()
        {
            return this.inData;
        }

        @Override
        public int read() throws IOException
        {
            if (this.contentLength == null)
            {
                final int data = this.inData.read();
                this.dump.write(data);
                // System.out.println("Char: " + (char) data);
                return data;
            }
            else if (this.contentLength.intValue() > this.readSoFar)
            {
                this.readSoFar++;
                final int data = this.inData.read();
                this.dump.write(data);
                // System.out.println("Char: " + (char) data);
                return data;
            }
            else
            {
                return -1;
            }
        }

        /**
         * Wrapper for the servletInputStream's readline method
         * 
         * @return line
         * @throws IOException
         */
        public String readLine() throws IOException
        {
            // System.out.println("ReadLine()");
            final byte buffer[] = new byte[BUFFER_SIZE];
            int charsRead = super.readLine(buffer, 0, BUFFER_SIZE);
            if (charsRead == -1)
            {
                return "";
            }
            if (buffer[charsRead - 1] == '\n')
            {
                charsRead -= 1;
            }
            if (buffer[charsRead - 1] == '\r')
            {
                charsRead -= 1;
            }
            final byte outBuf[] = new byte[charsRead];
            System.arraycopy(buffer, 0, outBuf, 0, charsRead);
            final String result = new String(outBuf);
            return result;
        }

        /**
         * @param length
         */
        public void setContentLength(final int length)
        {
            this.contentLength = new Integer(length);
            this.readSoFar = 0;
        }

        /**
         * @param inData
         */
        public WsgiInputStream(final byte inData[])
        {
            this(new ByteArrayInputStream(inData));
        }

        /**
         * @param inData
         */
        public WsgiInputStream(final InputStream inData)
        {
            super();
            this.inData = inData;
            this.dump = new ByteArrayOutputStream();
        }

        final int BUFFER_SIZE = 4096;

        private Integer contentLength;

        private final ByteArrayOutputStream dump;

        private final InputStream inData;

        private int readSoFar;
    }

    @Override
    public Map<String, Object> attributes()
    {
        return attributes;
    }

    @Override
    public Content content()
    {
        return reqContent;
    }

    @Override
    public Cookie[] cookie()
    {
        return requestCookie;
    }

    @Override
    public Map<String, String> header()
    {
        return requestHeader;
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
        return remoteAddress;
    }

    @Override
    public String toString()
    {
        return ReflectionToString.toString(this);
    }

    /**
     * @param remoteAddress
     * @param stream
     * @throws IOException
     */
    public WsgiRequest(final InetAddress remoteAddress, final InputStream stream)
            throws IOException
    {
        final WsgiInputStream in = new WsgiInputStream(stream);

        /*
         * remoteAddress
         */
        this.remoteAddress = remoteAddress;

        /*
         * header
         */
        requestHeader = new HashMap<String, String>();
        final String requestLine = in.readLine();
        for (String line; (line = in.readLine()).isEmpty() == false;)
        {
            final int idx = line.indexOf(':');
            requestHeader.put(line.substring(0, idx), line.substring(idx + 2));
        }

        /*
         * content
         */
        reqContent = new Request.Content()
        {
            @Override
            public int length()
            {
                return length;
            }

            @Override
            public InputStream stream()
            {
                return in;
            }

            @Override
            public String type()
            {
                return type;
            }

            final int length;
            final String type;

            {

                final String strType = requestHeader.remove("Content-Type");
                type = strType == null ? "" : strType;
                final String strLength = requestHeader.remove("Content-Length");
                length = strLength == null ? 0 : Integer.parseInt(strLength);
            }
        };

        /*
         * parameter
         */
        final String[] reqToken = requestLine.split(" ");
        method = Method.valueOf(reqToken[0]);
        final int qidx = reqToken[1].indexOf('?');
        final String rpath = qidx == -1 ? reqToken[1]
                : reqToken[1].substring(qidx);
        path = Server.URL.decode(rpath);
        query = qidx == -1 ? "" : reqToken[1].substring(qidx);
        parameter = Parameter.post(query, reqContent);

        /*
         * cookies
         */
        final String cookieHeader = requestHeader.get("Cookie");
        final String[] cookieTokens = cookieHeader == null ? new String[] {}
                : cookieHeader.split("; ");
        requestCookie = new Cookie[cookieTokens.length];
        for (int i = 0, length = requestCookie.length; i < length; i++)
        {
            final String token = cookieTokens[i];
            final int idx = token.indexOf('=');
            final String name = token.substring(0, idx);
            final String value = token.substring(idx + 1);
            final Cookie cookie = new Cookie(name, value);
            requestCookie[i] = cookie;
        }
    }

    final HashMap<String, Object> attributes = new HashMap<String, Object>();
    final Method method;
    final Map<String, Object> parameter;
    final String path;
    final String query;
    final InetAddress remoteAddress;
    final Content reqContent;
    final Cookie[] requestCookie;
    final HashMap<String, String> requestHeader;

}
