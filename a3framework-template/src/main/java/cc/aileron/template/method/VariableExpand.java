/**
 *
 */
package cc.aileron.template.method;

import cc.aileron.generic.$;
import cc.aileron.generic.ObjectProvider;
import cc.aileron.generic.util.SkipList;
import cc.aileron.pojo.NoSuchPropertyException;
import cc.aileron.pojo.PojoAccessor;
import cc.aileron.pojo.PojoProperty;

/**
 * @author aileron
 */
public class VariableExpand
{
    static interface VariableArgs
    {
        String dxo();

        boolean isLiteral();

        String value();
    }

    /**
     * @param accessor
     * @return expand-strings
     */
    public String expand(final PojoAccessor<?> accessor)
    {
        final StringBuilder builder = new StringBuilder();
        for (final VariableArgs arg : args)
        {
            final ObjectProvider<Object, String> cnv;
            try
            {
                cnv = arg.dxo().isEmpty() ? null
                        : $.<ObjectProvider<Object, String>> cast(accessor.to(arg.dxo())
                                .get(ObjectProvider.class));
            }
            catch (final NoSuchPropertyException e)
            {
                throw e.error();
            }
            if (arg.isLiteral())
            {
                builder.append(cnv == null ? arg.value() : cnv.get(arg.value()));
                continue;
            }

            try
            {
                final PojoProperty prop = accessor.to(arg.value());
                if (prop == null)
                {
                    continue;
                }
                final Object object = prop.get(Object.class);
                if (object == null)
                {
                    continue;
                }
                if (cnv == null)
                {
                    builder.append(object.toString());
                }
                else
                {
                    builder.append(cnv.get(object));
                }
            }
            catch (final NoSuchPropertyException e)
            {
                throw e.error();
            }
        }
        return builder.toString();
    }

    /**
     * @param token
     */
    public VariableExpand(final String token)
    {
        for (final String arg : token.split("\\|"))
        {
            final String args[] = arg.split("%");
            final boolean isLiteral = args[0].indexOf('\'') == 0;
            final String dxo = args.length == 2 ? args[1] : "";
            final String value = isLiteral ? args[0].substring(1,
                    arg.length() - 1) : args[0];
            this.args.add(new VariableArgs()
            {
                @Override
                public String dxo()
                {
                    return dxo;
                }

                @Override
                public boolean isLiteral()
                {
                    return isLiteral;
                }

                @Override
                public String value()
                {
                    return value;
                }
            });
        }
    }

    final SkipList<VariableArgs> args = new SkipList<VariableArgs>();

}