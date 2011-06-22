/**
 * 
 */
package cc.aileron.template.method;

import static java.util.regex.Pattern.*;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cc.aileron.generic.util.SkipList;
import cc.aileron.pojo.NoSuchPropertyException;
import cc.aileron.pojo.PojoAccessor;
import cc.aileron.template.compiler.TemplateContext;
import cc.aileron.template.compiler.TemplateInstructionTree;

/**
 * @author aileron
 * 
 */
public class RepMethod implements TemplateMethod<String>
{
    static class RepKeyAndValue
    {
        static final Pattern repPattern = Pattern.compile("\\*\\{(.*?)\\}");

        /**
         * @param accessor
         * @return {@link Pattern}
         * @throws NoSuchPropertyException
         */
        public Pattern pattern(final PojoAccessor<?> accessor)
                throws NoSuchPropertyException
        {
            final StringBuffer buffer = new StringBuffer();
            final Matcher ma = repPattern.matcher(pattern);
            while (ma.find())
            {
                ma.appendReplacement(buffer,
                        accessor.to(ma.group(1)).get(String.class));
            }
            return Pattern.compile(ma.appendTail(buffer).toString(),
                    Pattern.CASE_INSENSITIVE);
        }

        RepKeyAndValue(final String key, final String pattern)
        {
            this.key = key;
            this.pattern = pattern;
        }

        final String key;
        final String pattern;
    }

    @Override
    public String call(final TemplateContext context,
            final Iterable<TemplateInstructionTree> child)
            throws NoSuchPropertyException
    {
        final StringBuilder builder = new StringBuilder();
        for (final TemplateInstructionTree tree : child)
        {
            tree.self().procedure(tree.child()).call(new TemplateContext()
            {
                @Override
                public PojoAccessor<Object> object()
                {
                    return context.object();
                }

                @Override
                public void object(final PojoAccessor<?> object)
                {
                    context.object(object);
                }

                @Override
                public void write(final String content)
                {
                    builder.append(content);
                }
            });
        }
        String content = builder.toString();
        final PojoAccessor<Object> object = context.object();
        for (final RepKeyAndValue rep : list)
        {
            content = rep(rep, content, object);
        }
        return content;
    }

    /**
     * @param rep
     * @param content
     * @param accessor
     * @return 置き換え後文字列
     * @throws NoSuchPropertyException
     * @throws PojoPropertiesNotFoundException
     * @throws PojoAccessorValueNotFoundException
     */
    String rep(final RepKeyAndValue rep, final String content,
            final PojoAccessor<?> accessor) throws NoSuchPropertyException
    {
        final StringBuffer sb = new StringBuffer();
        final Matcher matcher = rep.pattern(accessor).matcher(content);
        while (matcher.find())
        {
            final CharSequence start = content.subSequence(matcher.start(),
                    matcher.start(1));
            final String value = new VariableExpand(rep.key).expand(accessor);
            final CharSequence end = content.subSequence(matcher.end(1),
                    matcher.end());
            matcher.appendReplacement(sb, start + value + end);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * @param args
     * @return args
     */
    private Iterable<RepKeyAndValue> parseArgs(final String args)
    {
        final int size = args.length();
        return new Iterable<RepKeyAndValue>()
        {
            @Override
            public Iterator<RepKeyAndValue> iterator()
            {
                return new Iterator<RepKeyAndValue>()
                {
                    @Override
                    public boolean hasNext()
                    {
                        return idx < size;
                    }

                    @Override
                    public RepKeyAndValue next()
                    {
                        final String val = args.substring(idx).trim();
                        final Matcher matcher = pattern.matcher(val);
                        if (!matcher.find())
                        {
                            idx = size;
                            return null;
                        }

                        final int start = matcher.start();
                        final int end1, end2, vsize;
                        if (matcher.group(2) == null)
                        {
                            end1 = matcher.end();
                            end2 = val.length();
                            vsize = end2 + 1;
                        }
                        else
                        {
                            end1 = matcher.end(1) + 1;
                            end2 = matcher.end(2) - 1;
                            vsize = end2 + 2;
                        }
                        final String key = matcher.group(1);
                        final String pattern = quote(val.substring(0, start)
                                .trim())
                                + "(.*?)"
                                + quote(val.substring(end1, end2).trim());

                        final RepKeyAndValue rep = new RepKeyAndValue(key,
                                pattern.trim());
                        idx += vsize;
                        return rep;
                    }

                    @Override
                    public void remove()
                    {
                        throw new UnsupportedOperationException();
                    }

                    int idx = 0;
                    final Pattern pattern = compile(quote("${") + "(.*?)"
                            + quote("}") + "(.*? &)?");
                };
            }
        };
    }

    /**
     * @param attribute
     */
    public RepMethod(final String attribute)
    {
        list = new SkipList<RepKeyAndValue>();
        for (final RepKeyAndValue rep : parseArgs(attribute))
        {
            list.add(rep);
        }
    }

    final SkipList<RepKeyAndValue> list;

}
