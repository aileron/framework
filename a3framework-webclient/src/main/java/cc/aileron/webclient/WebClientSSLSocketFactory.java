/**
 * 
 */
package cc.aileron.webclient;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.google.inject.ImplementedBy;

/**
 * @author aileron
 */
@ImplementedBy(WebClientSSLSocketFactoryImpl.class)
public interface WebClientSSLSocketFactory
{
    /**
     * @param host 
     * @param port 
     * @return {@link Socket}
     * @throws IOException 
     * @throws UnknownHostException 
     */
    Socket get(String host, int port) throws UnknownHostException, IOException;
}
