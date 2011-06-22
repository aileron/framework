/**
 * 
 */
package cc.aileron.webclient.impl;

import java.util.List;

import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLElement;

import cc.aileron.generic.$;
import cc.aileron.generic.util.SkipList;
import cc.aileron.webclient.WebClient;
import cc.aileron.webclient.WebResponse;
import cc.aileron.webclient.html.HtmlPage;
import cc.aileron.webclient.html.entity.HtmlElement;

/**
 * @author aileron
 */
class XPath
{
    /**
     * @param args
     */
    public static void main(final String... args)
    {
        System.out.println(xpath2nekoxpath("//img[@src='images/navpfeilrechts.gif']/.."));
        System.out.println(xpath2nekoxpath("//div[@id='contentHead']//div[@class='linkNum01']/a[position()=1]"));
        System.out.println(xpath2nekoxpath("//td[@class='td1'][@onmouseout='return nd();']/img/"));
        System.out.println(xpath2nekoxpath("//td[@class='td1'][@onmouseout='return nd();']/img/parent::a"));
        System.out.println(xpath2nekoxpath("//div[not(@id='anch_20')]/div[div[a]][div[@style='color: rgb(0, 128, 0); font-size: 12px;']]"));
        System.out.println(xpath2nekoxpath("//div[@id='iframeWrap']/iframe"));
    }

    /**
     * @param client
     * @param <T>
     * @param response
     * @param page
     * @param node
     * @param xpath
     * @return list
     */
    public static <T extends HtmlElement> List<T> xpath(final WebClient client,
            final WebResponse<?> response, final HtmlPage page,
            final Node node, final String xpath)
    {
        if (xpath == null)
        {
            throw new IllegalArgumentException();
        }

        final NodeList list;
        try
        {
            final String nekoxpath = xpath2nekoxpath(xpath);
            list = XPathAPI.selectNodeList(node, nekoxpath);
        }
        catch (final TransformerException e)
        {
            throw new Error(e);
        }

        final SkipList<T> result = new SkipList<T>();
        for (int i = 0, size = list.getLength(); i < size; i++)
        {
            final HTMLElement e = (HTMLElement) list.item(i);
            result.add($.<T> cast(new HtmlElementImpl(client,
                    response,
                    page,
                    e)));
        }
        return result;
    }

    private static String substr(final String xpath, final int from,
            final int to)
    {
        return xpath.substring(from, to);
    }

    /**
     * @param xpath
     * @return nekoxpath
     */
    private static String xpath2nekoxpath(final String xpath)
    {
        final StringBuffer result = new StringBuffer();
        final char[] chs = xpath.toCharArray();
        boolean attrcontext = false;
        boolean quote = false;
        int idx = 0;
        for (int i = 0, size = chs.length; i < size; i++)
        {
            final char ch = chs[i];
            switch (ch)
            {
            case '\'':
                result.append(substr(xpath, idx, i));
                idx = i;
                quote = !quote;
                break;
            case '/':
                if (attrcontext || quote)
                {
                    break;
                }
                result.append(substr(xpath, idx, i).toUpperCase());
                idx = i;
                break;

            case '[':
                if (quote)
                {
                    break;
                }
                result.append(substr(xpath, idx, i).toUpperCase());
                attrcontext = true;
                idx = i;
                break;

            case ']':
                if (quote)
                {
                    break;
                }
                {
                    final String tmp = substr(xpath, idx, i);
                    result.append(tmp.matches("^\\[[a-z|A-Z]*$") ? tmp.toUpperCase()
                            : tmp);
                }
                attrcontext = false;
                idx = i;
                break;

            case ':':
                if (attrcontext || quote)
                {
                    break;
                }
                result.append(substr(xpath, idx, i));
                idx = i;
                break;

            case ')':
                if (quote)
                {
                    break;
                }
                result.append(substr(xpath, idx, i));
                idx = i;
                break;
            }
        }
        result.append(substr(xpath, idx, xpath.length()).toUpperCase());
        return result.toString();
    }
}