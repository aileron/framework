/**
 *
 */
package cc.aileron.wsgi.mobile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map.Entry;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xpath.XPathAPI;
import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLElement;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.inject.Singleton;

/**
 * @author aileron
 */
@Singleton
public class MobileHtmlCssInlineConvertorImpl implements
        MobileHtmlCssInlineConvertor
{
    @Override
    public String convert(final MobileCarrier carrier, final String html,
            final CssSelectorProperties css)
            throws SAXException, IOException, TransformerException
    {
        final DOMParser parser = new DOMParser();
        parser.setFeature("http://xml.org/sax/features/namespaces", false);
        parser.parse(new InputSource(new ByteArrayInputStream(html.getBytes())));
        final Document document = parser.getDocument();
        final HTMLElement element = (HTMLElement) document.getDocumentElement();

        for (final Entry<String, String> e : css)
        {
            final String xpath = e.getKey(), style = e.getValue();
            final NodeList list = XPathAPI.selectNodeList(element, xpath);
            for (int i = 0, size = list.getLength(); i < size; i++)
            {
                final HTMLElement item = (HTMLElement) list.item(i);
                item.setAttribute("style", style);
            }
        }

        final Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD,
                carrier.doctypeMethod());
        transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC,
                carrier.doctypePublic());
        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,
                carrier.doctypeSystem());

        final StringWriter writer = new StringWriter();
        final StreamResult result = new StreamResult(writer);

        final DOMSource source = new DOMSource(document);
        transformer.transform(source, result);

        return writer.toString();
    }

    /**
     * @throws TransformerConfigurationException
     */
    public MobileHtmlCssInlineConvertorImpl()
            throws TransformerConfigurationException
    {
        transformerFactory = TransformerFactory.newInstance();
    }
    private final TransformerFactory transformerFactory;
}
