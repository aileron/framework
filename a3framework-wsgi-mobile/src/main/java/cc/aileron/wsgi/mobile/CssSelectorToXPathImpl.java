/**
 *
 */
package cc.aileron.wsgi.mobile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.aileron.generic.util.ReflectionToString;

import com.google.inject.Singleton;

/**
 * @author aileron
 */
@Singleton
public class CssSelectorToXPathImpl implements CssSelectorToXPath
{
    @Override
    public String convert(final String cssSelector)
    {
        int index = 1;
        final Map<Integer, String> parts = new HashMap<Integer, String>();
        parts.put(0, "//");
        parts.put(1, "*");

        String rule = cssSelector;
        String lastRule = null;

        while (rule.length() > 0 && rule.equals(lastRule) == false)
        {
            lastRule = rule;

            // Trim leading whitespace
            rule = rule.replace("^\\s*|\\s*$", "");
            if (rule.length() == 0)
            {
                break;
            }

            logger.trace("rule:{}", rule);

            // Match the element identifier
            String[] m = exec(regElement, rule);
            if (logger.isTraceEnabled())
            {
                logger.trace("m[{}]", ReflectionToString.toString(m));
                logger.trace("m.length({})", m.length);
            }
            if (m.length != 0)
            {
                if (m[1].isEmpty())
                {
                    logger.trace("m[1] is empty");

                    // XXXjoe Namespace ignored for now
                    if (m.length > 6)
                    {
                        parts.put(index, m[5].toUpperCase());
                    }
                    else
                    {
                        parts.put(index, m[2].toUpperCase());
                    }
                }
                else if (m[1].equals("#"))
                {
                    parts.put(parts.size(), "[@id='" + m[2] + "']");
                }
                else if (m[1].equals("."))
                {
                    parts.put(parts.size(), "[contains(@class, '" + m[2]
                            + "')]");
                }

                rule = rule.substring(m[0].length());
            }

            logger.trace(join(parts));

            // Match attribute selectors
            m = exec(regAttr2, rule);
            if (m.length != 0)
            {
                if (m[2].equals("~="))
                {
                    parts.put(parts.size(), "[contains(@" + m[1] + ", '" + m[3]
                            + "')]");
                }
                else
                {
                    parts.put(parts.size(), "[@" + m[1] + "='" + m[3] + "']");
                }

                rule = rule.substring(m[0].length());
            }
            else
            {
                m = exec(regAttr1, rule);
                if (m.length != 0)
                {
                    parts.put(parts.size(), "[@" + m[1] + "]");
                    rule = rule.substring(m[0].length());
                }
            }

            // Skip over pseudo-classes and pseudo-elements, which are of no use
            // to us
            m = exec(regPseudo, rule);
            while (m.length != 0)
            {
                rule = rule.substring(m[0].length());
                m = exec(regPseudo, rule);
            }

            // Match combinators
            m = exec(regCombinator, rule);
            if (m.length != 0 && m[0].length() != 0)
            {
                if (m[0].indexOf(">") != -1)
                {
                    parts.put(parts.size(), "/");
                }
                else if (m[0].indexOf("+") != -1)
                {
                    parts.put(parts.size(), "/following-sibling::");
                }
                else
                {
                    parts.put(parts.size(), "//");
                }
                index = parts.size();
                parts.put(parts.size(), "*");
                rule = rule.substring(m[0].length());
            }

            m = exec(regComma, rule);
            if (m.length != 0)
            {
                parts.put(parts.size(), " | ");
                parts.put(parts.size(), "//");
                parts.put(parts.size(), "*");
                index = parts.size() - 1;
                rule = rule.substring(m[0].length());

            }
        }

        return join(parts);
    }

    private String[] exec(final Pattern p, final String v)
    {
        final ArrayList<String> list = new ArrayList<String>();
        final Matcher m = p.matcher(v);
        if (!m.find())
        {
            return zeroArray;
        }
        for (int i = 0, size = m.groupCount(); i <= size; i++)
        {
            final String val = m.group(i);
            list.add(val != null ? val : "");
        }
        return list.toArray(new String[] {});
    }

    private final String join(final Map<Integer, String> map)
    {
        final StringBuilder builder = new StringBuilder();
        for (final Entry<Integer, String> e : map.entrySet())
        {
            logger.trace("e(key:{},val:{})", e.getKey(), e.getValue());
            builder.append(e.getValue());
        }
        return builder.toString();
    }

    /**
     * @param pattern
     * @return {@link Pattern}
     */
    private final Pattern p(final String pattern)
    {
        return Pattern.compile(pattern);
    }

    final Logger logger = LoggerFactory.getLogger(this.getClass());
    final Pattern regAttr1 = p("^\\[([^\\]]*)\\]");
    final Pattern regAttr2 = p("^\\[\\s*([^~=\\s]+)\\s*(~?=)\\s*\"([^\"]+)\"\\s*\\]");
    final Pattern regCombinator = p("^(\\s*[>+\\s])?");
    final Pattern regComma = p("^\\s*,/i");
    final Pattern regElement = p("^([#.]?)([a-zA-Z0-9\\*_-]*)((\\|)([a-zA-Z0-9\\*_-]*))?");
    final Pattern regPseudo = p("^:([a-z_-])+");
    final String[] zeroArray = new String[] {};
}
