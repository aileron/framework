/**
 * 
 */
package cc.aileron.template.impl;

import cc.aileron.generic.ObjectProvider;
import cc.aileron.template.EachContext;
import cc.aileron.template.EachContextHandller;

/**
 * @author aileron
 */
public class EachContextHandllerDefault implements
        EachContextHandller<Object, EachContext>
{
    @Override
    public EachContext call(final Object object)
    {
        idx++;
        return context;
    }

    @Override
    public EachContext last(final Object object)
    {
        idx++;
        z = true;
        return context;
    }

    final EachContext context = new EachContext()
    {
        @Override
        public int i()
        {
            return idx;
        }

        @Override
        public boolean z() throws UnsupportedOperationException
        {
            return z;
        }

        /**
         * 偶数
         */
        @SuppressWarnings("unused")
        public final ObjectProvider<Integer, Boolean> even = new ObjectProvider<Integer, Boolean>()
        {
            @Override
            public Boolean get(final Integer p)
            {
                return (p & 1) == 0;
            }
        };

        /**
         * 奇数
         */
        @SuppressWarnings("unused")
        public final ObjectProvider<Integer, Boolean> odd = new ObjectProvider<Integer, Boolean>()
        {
            @Override
            public Boolean get(final Integer p)
            {
                return (p & 1) == 1;
            }
        };
    };

    int idx = -1;

    boolean z;
}
