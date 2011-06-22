/**
 *
 */
package cc.aileron.wsgi.mobile;

import static cc.aileron.generic.$.*;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import cc.aileron.generic.Resource;
import cc.aileron.generic.error.NotFoundException;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author aileron
 */
@Singleton
public class MobileHtmlEmojiConvertorImpl implements MobileHtmlEmojiConvertor
{
    @Override
    public String convert(final MobileCarrier org, final MobileCarrier dist,
            final String html)
    {
        final HashMap<String, EnumMap<Emoji, String>> convert = map.get(org);
        if (convert == null)
        {
            return html;
        }
        final StringBuffer buffer = new StringBuffer();
        final Matcher matcher = org.emoji().matcher(html);
        while (matcher.find())
        {
            final String orghex = matcher.group(1);
            final EnumMap<Emoji, String> emoji = convert.get(orghex);
            if (emoji != null)
            {
                final String cnvhex = emoji.get(Emoji.valueOf(dist.name()));
                if (cnvhex != null)
                {
                    // 変換先絵文字が有る場合
                    logger.debug("{}:{} => {}:{}", new Object[] { org, orghex,
                            dist, cnvhex });
                    matcher.appendReplacement(buffer,
                            "&#x" + cnvhex.replace(">", "") + ";");
                }
                else
                {
                    matcher.appendReplacement(buffer, emoji.get(Emoji.TEXT));
                }
            }
            else
            {
                logger.debug("{}:{} => {}:", new Object[] { org, orghex, dist });
                matcher.appendReplacement(buffer, matcher.group());
            }
        }
        return matcher.appendTail(buffer).toString();
    }

    @Override
    public Map<Emoji, String> get(final MobileCarrier carrier, final String code)
    {
        return map.get(carrier).get(code);
    }

    /**
     * @throws NotFoundException
     * @throws XPathExpressionException
     */
    @Inject
    public MobileHtmlEmojiConvertorImpl() throws NotFoundException,
            XPathExpressionException
    {
        final XPath xpath = XPathFactory.newInstance().newXPath();
        final String expression = "//e";

        final InputSource inputSource = new InputSource(some(Resource.Loader.get("emoji4unicode.xml")).toStream());
        final NodeList nodes = (NodeList) xpath.evaluate(expression,
                inputSource,
                XPathConstants.NODESET);

        logger.debug("size:{}", nodes.getLength());

        for (int i = 0, size = nodes.getLength(); i < size; i++)
        {
            final Node item = nodes.item(i);
            final NamedNodeMap attributes = item.getAttributes();

            final Node a = attributes.getNamedItem("kddi");
            final Node s = attributes.getNamedItem("softbank");
            final Node d = attributes.getNamedItem("docomo");
            final Node t = attributes.getNamedItem("text_fallback");

            final String au = a == null ? null : a.getNodeValue();
            final String softbank = s == null ? null : s.getNodeValue();
            final String docomo = d == null ? null : d.getNodeValue();
            final String text = t == null ? null : t.getNodeValue();

            final EnumMap<Emoji, String> e = new EnumMap<Emoji, String>(Emoji.class);
            e.put(Emoji.AU, au);
            e.put(Emoji.SOFTBANK, softbank);
            e.put(Emoji.DOCOMO, docomo);
            e.put(Emoji.TEXT, text);

            logger.debug("{}", e);

            map.get(MobileCarrier.AU).put(au, e);
            map.get(MobileCarrier.DOCOMO).put(docomo, e);
            map.get(MobileCarrier.SOFTBANK).put(softbank, e);
        }

    }

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final EnumMap<MobileCarrier, HashMap<String, EnumMap<Emoji, String>>> map = new EnumMap<MobileCarrier, HashMap<String, EnumMap<Emoji, String>>>(MobileCarrier.class);

    {
        map.put(MobileCarrier.AU, new HashMap<String, EnumMap<Emoji, String>>());
        map.put(MobileCarrier.DOCOMO,
                new HashMap<String, EnumMap<Emoji, String>>());
        map.put(MobileCarrier.SOFTBANK,
                new HashMap<String, EnumMap<Emoji, String>>());
    }
}
