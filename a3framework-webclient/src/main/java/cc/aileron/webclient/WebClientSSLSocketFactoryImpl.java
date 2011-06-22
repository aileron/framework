/**
 * 
 */
package cc.aileron.webclient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

/**
 * @author aileron
 */
public class WebClientSSLSocketFactoryImpl implements WebClientSSLSocketFactory
{
    @Override
    public Socket get(final String host, final int port)
            throws UnknownHostException, IOException
    {
        return factory.createSocket(InetAddress.getByName(host), port);
    }

    /**
     */
    public WebClientSSLSocketFactoryImpl()
    {
        this.factory = SSLSocketFactory.getDefault();
    }

    /**
     * @param factory
     */
    public WebClientSSLSocketFactoryImpl(final SocketFactory factory)
    {
        this.factory = factory;
    }

    private final SocketFactory factory;
}
