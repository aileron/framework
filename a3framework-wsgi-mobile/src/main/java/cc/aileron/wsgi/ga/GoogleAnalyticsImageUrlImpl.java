/**
 * 
 */
package cc.aileron.wsgi.ga;

import java.nio.charset.Charset;

import cc.aileron.generic.util.URLTranslator;
import cc.aileron.wsgi.Wsgi;
import cc.aileron.wsgi.Wsgi.Request;

/**
 * @author aileron
 */
public class GoogleAnalyticsImageUrlImpl implements GoogleAnalyticsImageUrl
{
    @Override
    public String toString()
    {
        return googleAnalyticsGetImageUrl(Wsgi.Request());
    }

    /**
     * @param source
     * @return enc
     */
    private String enc(final String source)
    {
        final byte b = source.getBytes()[0];
        if ((b ^ 0x20) - 0xA1 < (120 / 2))
        {
            return urlTranslatorSjis.encode(source);
        }
        return urlTranslatorUtf8.encode(source);
    }

    private String googleAnalyticsGetImageUrl(final Request request)
    {
        final StringBuilder url = new StringBuilder();
        url.append(gaurl + "?");
        url.append("utmac=").append(account);
        url.append("&utmn=")
                .append(Integer.toString((int) (Math.random() * 0x7fffffff)));
        String referer = request.header().get("referer");
        final String query = request.query();
        String path = request.path();
        if (referer == null || "".equals(referer))
        {
            referer = "-";
        }
        url.append("&utmr=").append(enc(referer));
        if (path != null)
        {
            if (query != null)
            {
                path += "?" + query;
            }
            url.append("&utmp=").append(enc(path));
        }
        url.append("&guid=ON");
        return url.toString().replace("&", "&amp;");
    }

    /**
     * @param gaurl
     * @param account
     */
    public GoogleAnalyticsImageUrlImpl(final String gaurl, final String account)
    {
        this.gaurl = gaurl;
        this.account = account;
    }

    /**
     * GoogleAnalyticsImageUrl
     */
    public final String gaurl;
    private final String account;
    private final URLTranslator urlTranslatorSjis = URLTranslator.factory.get(Charset.forName("SJIS"));
    private final URLTranslator urlTranslatorUtf8 = URLTranslator.factory.get(Charset.forName("UTF-8"));
}
