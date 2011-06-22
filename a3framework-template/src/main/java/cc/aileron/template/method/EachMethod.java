/**
 * 
 */
package cc.aileron.template.method;

import java.util.Iterator;

import cc.aileron.generic.$;
import cc.aileron.generic.ObjectReference;
import cc.aileron.pojo.NoSuchPropertyException;
import cc.aileron.pojo.PojoAccessor;
import cc.aileron.pojo.PojoProperty;
import cc.aileron.template.EachContextHandller;
import cc.aileron.template.compiler.TemplateContext;
import cc.aileron.template.compiler.TemplateInstructionTree;
import cc.aileron.template.compiler.Token;
import cc.aileron.template.impl.EachContextHandllerDefault;

/**
 * @author aileron
 */
public class EachMethod implements
        TemplateMethod<Iterable<PojoAccessor<Object>>>
{
    @Override
    public Iterable<PojoAccessor<Object>> call(final TemplateContext context,
            final Iterable<TemplateInstructionTree> child)
            throws NoSuchPropertyException
    {
        final PojoAccessor<Object> old = context.object();
        final EachContextHandller<Object, ?> each = on.isEmpty() ? new EachContextHandllerDefault()
                : $.<EachContextHandller<Object, ?>> cast(old.to(on)
                        .get(EachContextHandller.class));

        final PojoProperty oldProp = old.to(name);
        if (oldProp == null)
        {
            return null;
        }
        final Iterable<Object> i = oldProp.iterable(Object.class);
        final Iterable<PojoAccessor<Object>> result = new Iterable<PojoAccessor<Object>>()
        {
            @Override
            public Iterator<PojoAccessor<Object>> iterator()
            {
                final Iterator<Object> ite = i.iterator();
                return $.iterate(new ObjectReference<PojoAccessor<Object>>()
                {
                    @Override
                    public PojoAccessor<Object> get()
                    {
                        if (!ite.hasNext())
                        {
                            return null;
                        }
                        Object object = null;
                        do
                        {
                            final Object raw = ite.next();
                            if (ite.hasNext())
                            {
                                object = each.call(raw);
                            }
                            else
                            {
                                object = each.last(raw);
                            }
                            if (object == null)
                            {
                                continue;
                            }
                            final PojoAccessor<Object> local = old.repository()
                                    .from(raw)
                                    .add(object);

                            return in.isEmpty() ? old.add(local) : old.add(in,
                                    local);
                        } while (ite.hasNext());
                        return null;
                    }
                });
            }
        };
        return result;
    }

    /**
     * @param token
     */
    public EachMethod(final Token token)
    {
        final String args;
        final String on;
        final String[] cs = token.attribute().split("\\|");
        if (cs.length == 2)
        {
            args = cs[0].trim();
            on = cs[1].trim();
        }
        else
        {
            args = token.attribute();
            on = "";
        }

        final String name;
        final String in;
        final String[] is = args.split("->");
        if (is.length == 2)
        {
            name = is[0].trim();
            in = is[1].trim();
        }
        else
        {
            name = is[0].trim();
            in = "";
        }
        this.name = name;
        this.in = in;
        this.on = on;
    }

    final String in;
    final String name;
    final String on;
}
