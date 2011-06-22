/**
 * 
 */
package cc.aileron.webclient.impl;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.HttpException;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLElement;
import org.xml.sax.SAXException;

import cc.aileron.generic.$;
import cc.aileron.generic.util.SkipList;
import cc.aileron.webclient.WebClient;
import cc.aileron.webclient.WebResponse;
import cc.aileron.webclient.html.HtmlPage;
import cc.aileron.webclient.html.entity.HtmlAnchorElement;
import cc.aileron.webclient.html.entity.HtmlAppletElement;
import cc.aileron.webclient.html.entity.HtmlAreaElement;
import cc.aileron.webclient.html.entity.HtmlBRElement;
import cc.aileron.webclient.html.entity.HtmlBaseElement;
import cc.aileron.webclient.html.entity.HtmlBaseFontElement;
import cc.aileron.webclient.html.entity.HtmlBodyElement;
import cc.aileron.webclient.html.entity.HtmlButtonElement;
import cc.aileron.webclient.html.entity.HtmlDListElement;
import cc.aileron.webclient.html.entity.HtmlDirectoryElement;
import cc.aileron.webclient.html.entity.HtmlDivElement;
import cc.aileron.webclient.html.entity.HtmlElement;
import cc.aileron.webclient.html.entity.HtmlFieldSetElement;
import cc.aileron.webclient.html.entity.HtmlFontElement;
import cc.aileron.webclient.html.entity.HtmlFormElement;
import cc.aileron.webclient.html.entity.HtmlFrameElement;
import cc.aileron.webclient.html.entity.HtmlFrameSetElement;
import cc.aileron.webclient.html.entity.HtmlHRElement;
import cc.aileron.webclient.html.entity.HtmlHeadElement;
import cc.aileron.webclient.html.entity.HtmlHeadingElement;
import cc.aileron.webclient.html.entity.HtmlIFrameElement;
import cc.aileron.webclient.html.entity.HtmlImageElement;
import cc.aileron.webclient.html.entity.HtmlInputElement;
import cc.aileron.webclient.html.entity.HtmlIsIndexElement;
import cc.aileron.webclient.html.entity.HtmlLIElement;
import cc.aileron.webclient.html.entity.HtmlLabelElement;
import cc.aileron.webclient.html.entity.HtmlLegendElement;
import cc.aileron.webclient.html.entity.HtmlLinkElement;
import cc.aileron.webclient.html.entity.HtmlMapElement;
import cc.aileron.webclient.html.entity.HtmlMenuElement;
import cc.aileron.webclient.html.entity.HtmlMetaElement;
import cc.aileron.webclient.html.entity.HtmlModElement;
import cc.aileron.webclient.html.entity.HtmlOListElement;
import cc.aileron.webclient.html.entity.HtmlObjectElement;
import cc.aileron.webclient.html.entity.HtmlOptGroupElement;
import cc.aileron.webclient.html.entity.HtmlOptionElement;
import cc.aileron.webclient.html.entity.HtmlParagraphElement;
import cc.aileron.webclient.html.entity.HtmlParamElement;
import cc.aileron.webclient.html.entity.HtmlPreElement;
import cc.aileron.webclient.html.entity.HtmlQuoteElement;
import cc.aileron.webclient.html.entity.HtmlScriptElement;
import cc.aileron.webclient.html.entity.HtmlSelectElement;
import cc.aileron.webclient.html.entity.HtmlStyleElement;
import cc.aileron.webclient.html.entity.HtmlTableCaptionElement;
import cc.aileron.webclient.html.entity.HtmlTableCellElement;
import cc.aileron.webclient.html.entity.HtmlTableColElement;
import cc.aileron.webclient.html.entity.HtmlTableElement;
import cc.aileron.webclient.html.entity.HtmlTableRowElement;
import cc.aileron.webclient.html.entity.HtmlTableSectionElement;
import cc.aileron.webclient.html.entity.HtmlTextAreaElement;
import cc.aileron.webclient.html.entity.HtmlTitleElement;

/**
 * @author aileron
 */
public class HtmlElementImpl implements HtmlAnchorElement, HtmlAppletElement,
        HtmlAreaElement, HtmlBaseElement, HtmlBaseFontElement, HtmlBodyElement,
        HtmlBRElement, HtmlButtonElement, HtmlDirectoryElement, HtmlDivElement,
        HtmlDListElement, HtmlFieldSetElement, HtmlFontElement,
        HtmlFormElement, HtmlFrameElement, HtmlFrameSetElement,
        HtmlHeadElement, HtmlHeadingElement, HtmlHRElement, HtmlIFrameElement,
        HtmlImageElement, HtmlInputElement, HtmlIsIndexElement,
        HtmlLabelElement, HtmlLegendElement, HtmlLIElement, HtmlLinkElement,
        HtmlMapElement, HtmlMenuElement, HtmlMetaElement, HtmlModElement,
        HtmlObjectElement, HtmlOListElement, HtmlOptGroupElement,
        HtmlOptionElement, HtmlParagraphElement, HtmlParamElement,
        HtmlPreElement, HtmlQuoteElement, HtmlScriptElement, HtmlSelectElement,
        HtmlStyleElement, HtmlTableCaptionElement, HtmlTableCellElement,
        HtmlTableColElement, HtmlTableElement, HtmlTableRowElement,
        HtmlTableSectionElement, HtmlTextAreaElement, HtmlTitleElement
{
    @Override
    public String attr(final String name)
    {
        return raw.getAttribute(name);
    }

    @Override
    public void attr(final String name, final String value)
    {
        raw.setAttribute(name, value);
    }

    @Override
    public <T extends HtmlElement> List<T> children()
    {
        final SkipList<T> result = new SkipList<T>();
        final NodeList list = raw.getChildNodes();
        for (int i = 0, size = list.getLength(); i < size; i++)
        {
            if (!(list.item(i) instanceof HTMLElement))
            {
                continue;
            }
            final HTMLElement e = (HTMLElement) list.item(i);
            result.add($.<T> cast(new HtmlElementImpl(client,
                    response,
                    page,
                    e)));
        }
        return result;
    }

    @Override
    public WebResponse<HtmlPage> click()
            throws ParseException, IOException, HttpException, SAXException,
            URISyntaxException
    {
        return WebClientCommons.getPage(client, response, href());
    }

    @Override
    public <T extends HtmlElement> List<T> getByXPath(final String xpath)
    {
        return XPath.<T> xpath(client, response, page, raw, xpath);
    }

    @Override
    public File getFile() throws URISyntaxException, IOException, HttpException
    {
        return WebClientCommons.getImageFile(client, response, attr("src"));
    }

    @Override
    public String href()
    {
        return raw.getAttribute("href");
    }

    @Override
    public String id()
    {
        return raw.getId();
    }

    @Override
    public HtmlPage page()
    {
        return page;
    }

    @Override
    public <T extends HTMLElement> T raw()
    {
        return $.<T> cast(raw);
    }

    @Override
    public WebResponse<HtmlPage> submit(
            final List<? extends NameValuePair> values)
            throws ParseException, URISyntaxException, IOException,
            HttpException, SAXException
    {
        return WebClientCommons.send(client,
                response,
                attr("action"),
                attr("method"),
                values);
    }

    @Override
    public String text()
    {
        return raw.getTextContent();
    }

    /**
     * @param client
     * @param response
     * @param page
     * @param raw
     */
    public HtmlElementImpl(final WebClient client,
            final WebResponse<?> response, final HtmlPage page,
            final HTMLElement raw)
    {
        this.client = client;
        this.response = response;
        this.page = page;
        this.raw = raw;
    }

    private final WebClient client;
    private final HtmlPage page;
    private final HTMLElement raw;
    private final WebResponse<?> response;
}
