/**
 *
 */
package cc.aileron.wsgi.mobile;

import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import com.google.inject.ImplementedBy;

/**
 * @author aileron
 */
@ImplementedBy(MobileHtmlCssInlineConvertorImpl.class)
public interface MobileHtmlCssInlineConvertor
{
    /**
     * @param carrier
     * @param html
     * @param css
     * @return convert-html
     * @throws IOException
     * @throws SAXException
     * @throws TransformerException
     */
    String convert(MobileCarrier carrier, String html, CssSelectorProperties css)
            throws SAXException, IOException, TransformerException;
}
