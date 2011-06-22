/**
 *
 */
package cc.aileron.wsgi.mobile;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author aileron
 */
@Singleton
public class CssSelectorPropertiesFactoryImpl implements
        CssSelectorPropertiesFactory
{

    @Override
    public CssSelectorProperties parse(final String rawcss)
    {
        final String css = rawcss.replace('\n', ' ')
                .replaceAll(Pattern.quote("/*") + ".*?" + Pattern.quote("*/"),
                        "");

        final Map<String, String> p = new HashMap<String, String>();
        for (final Matcher matcher = pattern.matcher(css); matcher.find();)
        {
            final String key = matcher.group(1).trim();
            final String val = matcher.group(2)
                    .replace('\n', ' ')
                    .replaceAll("[\\s]+", " ")
                    .trim();
            p.put(css2xpath.convert(key), val);
        }
        return new CssSelectorProperties()
        {
            @Override
            public String get(final String key)
            {
                return p.get(key);
            }

            @Override
            public Iterator<Entry<String, String>> iterator()
            {
                return p.entrySet().iterator();
            }
        };
    }

    /**
     * @param css2xpath
     */
    @Inject
    public CssSelectorPropertiesFactoryImpl(final CssSelectorToXPath css2xpath)
    {
        this.css2xpath = css2xpath;
    }

    final Pattern pattern = Pattern.compile("(.*?)" + Pattern.quote("{")
            + "(.*?)" + Pattern.quote("}"));

    private final CssSelectorToXPath css2xpath;
}
