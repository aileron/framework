/**
 * 
 */
package cc.aileron.generic.util;

import java.lang.reflect.Field;
import java.util.HashSet;

/**
 * @author aileron
 */
public class ReflectionToString
{
    /**
     * @param target
     * @return {@link String}
     */
    public static String toString(final Object target)
    {
        if (target == null)
        {
            return null;
        }
        final StringBuilder builder = new StringBuilder();
        final Class<? extends Object> type = target.getClass();
        builder.append(type + "[");

        final HashSet<Field> fields = new HashSet<Field>();
        for (final Field field : type.getDeclaredFields())
        {
            fields.add(field);
        }
        for (final Field field : type.getFields())
        {
            fields.add(field);
        }
        for (final Field field : fields)
        {
            try
            {
                field.setAccessible(true);
                final Object object = field.get(target);
                final String name = field.getName();
                final String value = object == null ? "null"
                        : field.get(target).toString();
                builder.append(name + "=" + value + ",");
            }
            catch (final IllegalArgumentException e)
            {
            }
            catch (final IllegalAccessException e)
            {
            }
        }
        builder.delete(builder.length() - 1, builder.length());
        builder.append("]");
        return builder.toString();
    }

    @Override
    public String toString()
    {
        return string;
    }

    /**
     * @param target
     */
    public ReflectionToString(final Object target)
    {
        this.string = toString(target);
    }

    private final String string;
}
