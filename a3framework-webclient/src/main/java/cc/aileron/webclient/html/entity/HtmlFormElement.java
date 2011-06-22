package cc.aileron.webclient.html.entity;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.HttpException;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.xml.sax.SAXException;

import cc.aileron.webclient.WebResponse;
import cc.aileron.webclient.html.HtmlPage;

/**
 * @author aileron
 */
public interface HtmlFormElement extends HtmlElement
{
    /**
     * @param values
     * @return {@link WebResponse}
     * @throws SAXException
     * @throws HttpException
     * @throws IOException
     * @throws URISyntaxException
     * @throws ParseException
     */
    WebResponse<HtmlPage> submit(List<? extends NameValuePair> values)
            throws ParseException, URISyntaxException, IOException,
            HttpException, SAXException;
}
