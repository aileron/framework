/**
 *
 */
package cc.aileron.webclient.html;

import java.io.IOException;
import java.net.HttpCookie;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.HttpException;
import org.apache.http.ParseException;
import org.xml.sax.SAXException;

import cc.aileron.webclient.WebResponse;
import cc.aileron.webclient.html.entity.HtmlElement;

/**
 * @author aileron
 */
public interface HtmlPage
{
    /**
     * @return text
     */
    String asText();

    /**
     * @return charset
     */
    String charset();

    /**
     * @return cookies
     */
    List<HttpCookie> cookies();

    /**
     * @param url
     * @return response
     * @throws ParseException
     * @throws URISyntaxException
     * @throws IOException
     * @throws HttpException
     * @throws SAXException
     */
    WebResponse<HtmlPage> directJump(String url)
            throws ParseException, URISyntaxException, IOException,
            HttpException, SAXException;

    /**
     * @param <T>
     * @param xpath
     * @return elements
     */
    <T extends HtmlElement> List<T> getByXPath(String xpath);

    /**
     * 現在表示しているページ(this)から、何がしかの方法で別画面へ遷移する為のリクエストを発行する また、UA等を引き継ぐ
     * 
     * @param url
     * @return {@link WebResponse}
     * @throws SAXException
     * @throws HttpException
     * @throws IOException
     * @throws URISyntaxException
     * @throws ParseException
     */
    WebResponse<HtmlPage> jump(String url)
            throws ParseException, URISyntaxException, IOException,
            HttpException, SAXException;

    /**
     * @return title
     */
    String title();

    /**
     * @return pageUrl
     */
    String url();
}
