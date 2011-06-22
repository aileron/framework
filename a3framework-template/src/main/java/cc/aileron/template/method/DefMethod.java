/**
 * 
 */
package cc.aileron.template.method;

import java.util.Collection;
import java.util.EnumSet;

import cc.aileron.pojo.NoSuchPropertyException;
import cc.aileron.pojo.PojoAccessor;
import cc.aileron.pojo.PojoAccessor.Category;
import cc.aileron.pojo.PojoProperty;
import cc.aileron.template.compiler.TemplateContext;
import cc.aileron.template.compiler.TemplateInstructionTree;

/**
 * @author aileron
 */
public class DefMethod implements TemplateMethod<Boolean>
{
    @Override
    public Boolean call(final TemplateContext context,
            final Iterable<TemplateInstructionTree> child)
            throws NoSuchPropertyException
    {
        final String[] token = args.split(" @ ");
        final String key = token[0].replace("!", "").trim();
        final String compKey;
        if (token.length >= 2)
        {
            compKey = token[1].replace("!", "").trim();
        }
        else
        {
            compKey = null;
        }
        final boolean isReverse = args.trim().endsWith("!");
        final PojoAccessor<?> pojo = context.object();
        final PojoProperty accessor = pojo.to(key);
        if (accessor == null || !accessor.exist(Category.GET))
        {
            return isReverse;
        }
        final Object object = accessor.get();
        final Class<?> type = accessor.type(Category.GET);
        if (type.isEnum() || object instanceof Enum)
        {
            final boolean enumValue = enumValue(accessor, compKey);
            return isReverse ? !enumValue : enumValue;
        }
        if (compKey != null && object instanceof EnumSet)
        {
            final EnumSet<?> set = accessor.get(EnumSet.class);
            if (set.isEmpty())
            {
                return isReverse;
            }
            try
            {
                final Object v = set.iterator()
                        .next()
                        .getClass()
                        .getMethod("valueOf", String.class)
                        .invoke(null, compKey);
                final boolean result = set.contains(v);
                return isReverse ? !result : result;
            }
            catch (final Exception e)
            {
                throw new Error(e);
            }
        }
        if (Collection.class.isAssignableFrom(type))
        {
            final Collection<?> collection = accessor.get(Collection.class);
            final boolean exist = collection == null ? false
                    : !collection.isEmpty();
            return isReverse ? !exist : exist;
        }
        if (compKey != null
                && (Integer.class.isAssignableFrom(type) || Integer.TYPE.equals(type)))
        {
            final Integer value = accessor.get(Integer.class);
            final Integer compValue = Integer.parseInt(compKey);
            final boolean comp = compValue.equals(value);
            return isReverse ? !comp : comp;
        }
        if (compKey != null
                && (Float.class.isAssignableFrom(type) || Float.TYPE.equals(type)))
        {
            final Float value = accessor.get(Float.class);
            final Float compValue = Float.parseFloat(compKey);
            final boolean comp = compValue.equals(value);
            return isReverse ? !comp : comp;
        }

        final boolean boolValue = boolValue(accessor, compKey);
        final boolean result = isReverse ? !boolValue : boolValue;
        return result;
    }

    /**
     * @param accessor
     * @param compKey
     * @return bool
     */
    private boolean boolValue(final PojoProperty accessor, final String compKey)
    {
        final boolean value = accessor.get(Boolean.class);
        if (compKey == null)
        {
            return value;
        }
        final Boolean compValue = Boolean.parseBoolean(compKey);
        return compValue.equals(value);
    }

    /**
     * @param accessor
     * @param compKey
     * @return bool
     */
    private boolean enumValue(final PojoProperty accessor, final String compKey)
    {
        final Enum<?> value = accessor.get(Enum.class);
        if (compKey == null)
        {
            return value != null;
        }
        if (value == null)
        {
            return false;
        }
        return compKey.equals(value.name());
    }

    /**
     * @param attribute
     */
    public DefMethod(final String attribute)
    {
        args = attribute;
    }

    final String args;
}
