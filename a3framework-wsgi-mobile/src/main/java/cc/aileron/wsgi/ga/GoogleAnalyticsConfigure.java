/**
 * 
 */
package cc.aileron.wsgi.ga;

import cc.aileron.web.WebBinder;
import cc.aileron.web.WebConfigure;
import cc.aileron.web.resource.BlankResource;
import cc.aileron.wsgi.Wsgi.Method;

/**
 * @author aileron
 */
public class GoogleAnalyticsConfigure implements WebConfigure
{
    @Override
    public void configure(final WebBinder binder) throws Exception
    {
        binder.bind(BlankResource.class)
                .to(Method.GET, url)
                .process(new GoogleAnalyticsImageProcess());
    }

    /**
     * @param url
     */
    public GoogleAnalyticsConfigure(final String url)
    {
        this.url = url;
    }

    private final String url;
}
