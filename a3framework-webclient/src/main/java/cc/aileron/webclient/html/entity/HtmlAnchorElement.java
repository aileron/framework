package cc.aileron.webclient.html.entity;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.HttpException;
import org.apache.http.ParseException;
import org.xml.sax.SAXException;

import cc.aileron.webclient.WebResponse;
import cc.aileron.webclient.html.HtmlPage;

/**
 * @author aileron
 */
public interface HtmlAnchorElement extends HtmlElement
{
    /**
     * @return {@link WebResponse}
     * @throws SAXException
     * @throws HttpException
     * @throws IOException
     * @throws ParseException
     * @throws URISyntaxException
     */
    WebResponse<HtmlPage> click()
            throws ParseException, IOException, HttpException, SAXException,
            URISyntaxException;

    /**
     * @return href
     */
    String href();
}
