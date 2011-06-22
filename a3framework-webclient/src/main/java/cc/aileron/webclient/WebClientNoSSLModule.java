/**
 * 
 */
package cc.aileron.webclient;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.google.inject.Binder;
import com.google.inject.Module;

/**
 * @author aileron
 */
public class WebClientNoSSLModule implements Module
{
    @Override
    public void configure(final Binder binder)
    {
        binder.bind(WebClientSSLSocketFactory.class)
                .toInstance(new WebClientSSLSocketFactoryImpl(sslContext.getSocketFactory()));
    }

    /**
     * @throws Exception 
     */
    public WebClientNoSSLModule() throws Exception
    {
        sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[] { new X509TrustManager()
        {
            @Override
            public void checkClientTrusted(final X509Certificate[] chain,
                    final String authType) throws CertificateException
            {
                if (chain != null)
                {
                    for (int i = 0; i < chain.length; i++)
                    {
                        issuersList.add(chain[i]);
                    }
                }
            }

            @Override
            public void checkServerTrusted(final X509Certificate[] chain,
                    final String authType) throws CertificateException
            {
                if (chain != null)
                {
                    for (int i = 0; i < chain.length; i++)
                    {
                        issuersList.add(chain[i]);
                    }
                }
            }

            @Override
            public X509Certificate[] getAcceptedIssuers()
            {
                return issuersList.toArray(new X509Certificate[0]);
            }

            private final ArrayList<X509Certificate> issuersList = new ArrayList<X509Certificate>();

        } },
                null);
    }

    private SSLContext sslContext;
}
