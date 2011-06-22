package cc.aileron.pager;

import cc.aileron.dao.DataFinderRange;

/**
 * @author aileron
 */
public class HtmlPagerContext implements DataFinderRange
{
    /**
     * @return count
     */
    public int count()
    {
        return count;
    }

    /**
     * @param count
     */
    public void count(final int count)
    {
        this.count = count;
    }

    @Override
    public int limit()
    {
        return pageSize;
    }

    @Override
    public int offset()
    {
        return pageSize * (pageNumber() - 1);
    }

    /**
     * @return pageMax
     */
    public int pageMax()
    {
        final int pageCount = count / pageSize;
        return count % pageSize == 0 ? pageCount : pageCount + 1;
    }

    /**
     * @return pageNumber
     */
    public int pageNumber()
    {
        final int pageMax = pageMax();
        if (pageNumber <= pageMax && 0 < pageNumber)
        {
            return pageNumber;
        }
        return 1;
    }

    /**
     * @param pageNumber
     */
    public void pageNumber(final int pageNumber)
    {
        this.pageNumber = pageNumber;
    }

    /**
     * @param pageSize
     */
    public HtmlPagerContext(final int pageSize)
    {
        this.pageSize = pageSize;
    }

    int pageSize;
    private int count;
    private int pageNumber = 1;
}