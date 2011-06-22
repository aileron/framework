/**
 * 
 */
package cc.aileron.webspider;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpException;
import org.apache.http.ParseException;
import org.xml.sax.SAXException;

import cc.aileron.generic.util.WorkQueue;
import cc.aileron.webclient.WebRequestDefault;
import cc.aileron.webclient.WebResponse;
import cc.aileron.webclient.html.HtmlPage;
import cc.aileron.webclient.html.entity.HtmlAnchorElement;
import cc.aileron.webclient.impl.WebClientImpl;

/**
 * @author aileron
 */
public class WebSpider
{
    /**
     * リンク構造の、元ページと、発リンク先を表現するインタフェース
     */
    interface FromTo
    {
        /**
         * @return from
         */
        String from();

        /**
         * @return to
         */
        String to();
    }

    /**
     * @param args
     * @throws ParseException
     * @throws MalformedURLException
     * @throws IOException
     * @throws HttpException
     * @throws SAXException
     * @throws Exception
     */
    public static void main(final String[] args)
            throws ParseException, MalformedURLException, IOException,
            HttpException, SAXException, Exception
    {
        new WebSpider(args[0]);
    }

    void add(final String from, final List<HtmlAnchorElement> list)
    {
        for (final HtmlAnchorElement anchor : list)
        {
            final String to = anchor.href();
            if (accessedUrls.contains(to) || to.equals("#")
                    || to.indexOf("http") == 0)
            {
                continue;
            }
            accessedUrls.add(to);
            execute(new FromTo()
            {
                @Override
                public String from()
                {
                    return from;
                }

                @Override
                public String to()
                {
                    return to;
                }
            });
        }
    }

    void execute(final FromTo url)
    {
        workQueue.execute(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    final WebResponse<HtmlPage> page = start.jump(url.to());
                    final int statusCode = page.status().statusCode();
                    System.err.println(statusCode + "\t" + url.from() + "\t"
                            + url.to());
                    if (statusCode == 404)
                    {
                        return;
                    }
                    add(url.to(),
                            page.entity().<HtmlAnchorElement> getByXPath("//a"));
                }
                catch (final Exception e)
                {
                    throw new Error(e);
                }
            }
        });
    }

    /**
     * @param startUrl
     * @throws ParseException
     * @throws MalformedURLException
     * @throws IOException
     * @throws HttpException
     * @throws SAXException
     * @throws Exception
     */
    public WebSpider(final String startUrl) throws ParseException,
            MalformedURLException, IOException, HttpException, SAXException,
            Exception
    {
        accessedUrls.add("/");
        start = new WebClientImpl().getPage(new WebRequestDefault()
        {
            @Override
            public URL url()
            {
                return url;
            }

            final URL url = new URL(startUrl);
        }).entity();
        add(startUrl, start.<HtmlAnchorElement> getByXPath("//a"));
    }

    final Set<String> accessedUrls = Collections.synchronizedSet(new HashSet<String>());
    final HtmlPage start;
    final WorkQueue workQueue = new WorkQueue(10);
}
