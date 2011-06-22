/**
 * 
 */
package cc.aileron.wsgi.ga;

import com.google.inject.Binder;
import com.google.inject.Module;

/**
 * @author aileron
 */
public class GoogleAnalyticsModule implements Module
{
    @Override
    public void configure(final Binder binder)
    {
        binder.bind(GoogleAnalyticsImageUrl.class)
                .toInstance(new GoogleAnalyticsImageUrlImpl(gaUrl, account));
        binder.bind(GoogleAnalyticsConfigure.class)
                .toInstance(new GoogleAnalyticsConfigure(gaUrl));
    }

    /**
     * @param gaUrl
     * @param account
     */
    public GoogleAnalyticsModule(final String gaUrl, final String account)
    {
        this.gaUrl = gaUrl;
        this.account = account;
    }

    private final String account;
    private final String gaUrl;
}
