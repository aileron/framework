/**
 * 
 */
package cc.aileron.pager;

import java.util.Iterator;

/**
 * @author aileron
 * 
 */
public class SimpleHtmlPager implements Iterable<HtmlPageLink>
{
    @Override
    public Iterator<HtmlPageLink> iterator()
    {
        return new Iterator<HtmlPageLink>()
        {
            @Override
            public boolean hasNext()
            {
                return idx <= max;
            }

            @Override
            public HtmlPageLink next()
            {
                try
                {
                    return new HtmlPageLink()
                    {
                        @Override
                        public String href()
                        {
                            return href;
                        }

                        @Override
                        public boolean isFirstPage()
                        {
                            return idx == start();
                        }

                        @Override
                        public boolean isNotSelected()
                        {
                            return !select;
                        }

                        @Override
                        public boolean isSelected()
                        {
                            return select;
                        }

                        @Override
                        public String label()
                        {
                            return label;
                        }

                        final String href = String.valueOf(idx);
                        final String label = SimpleHtmlPager.this.label(idx);
                        final boolean select = current == idx;

                    };
                }
                finally
                {
                    idx++;
                }

            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException();
            }

            int idx = SimpleHtmlPager.this.start();
        };
    }

    protected String label(final int idx)
    {
        return String.valueOf(idx);
    }

    protected int start()
    {
        return 1;
    }

    /**
     * @param current
     * @param max
     */
    public SimpleHtmlPager(final int current, final int max)
    {
        this.current = current;
        this.max = max;
    }

    final int current;
    final int max;
}
