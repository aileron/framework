package cc.aileron.web;

import static java.util.regex.Pattern.*;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cc.aileron.generic.ObjectProvider;
import cc.aileron.generic.util.ReflectionToString;
import cc.aileron.generic.util.SkipList;
import cc.aileron.pojo.NoSuchPropertyException;
import cc.aileron.pojo.PojoAccessor;
import cc.aileron.pojo.PojoProperty;

/**
 * URI テンプレート
 */
public class WebUrlTemplate
{
    /**
     * pattern
     */
    static final Pattern pattern = compile(quote("${") + "(.*?)"
            + quote("}"));

    /**
     * @return キーの一覧
     */
    public List<String> extract()
    {
        final Matcher matcher = pattern.matcher(uriTemplate);
        final SkipList<String> list = new SkipList<String>();
        while (matcher.find())
        {
            final String key = matcher.group(1);
            list.add(key);
        }
        return list;
    }

    /**
     * @param accessor
     * @return 置き換え後文字列
     * @throws NoSuchPropertyException
     */
    public String replace(final PojoAccessor<?> accessor)
            throws NoSuchPropertyException
    {
        final Matcher matcher = pattern.matcher(uriTemplate);
        final StringBuffer buffer = new StringBuffer();
        while (matcher.find())
        {
            final String[] token = matcher.group(1).split("%");
            final String key = token[0];

            String val = accessor.to(key).get(String.class);
            val = val == null ? "" : val;
            if (token.length == 2)
            {
                final ObjectProvider<Object, String> cnv = accessor.to(token[1])
                        .get(new PojoProperty.Key<ObjectProvider<Object, String>>()
                        {
                        });
                val = cnv.get(val);
            }
            matcher.appendReplacement(buffer, val != null ? val : "");
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    @Override
    public String toString()
    {
        return ReflectionToString.toString(this);
    }

    /**
     * @param uriTemplate
     */
    public WebUrlTemplate(final String uriTemplate)
    {
        this.uriTemplate = uriTemplate;
    }

    /**
     * uri-template
     */
    public final String uriTemplate;
}