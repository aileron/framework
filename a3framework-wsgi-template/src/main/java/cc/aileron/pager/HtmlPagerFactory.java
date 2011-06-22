/**
 *
 */
package cc.aileron.pager;

/**
 * html-pager のファクトリ
 * 
 * @author aileron
 */
public interface HtmlPagerFactory
{
    /**
     * @param pagingSize
     * @return {@link HtmlPager}
     */
    HtmlPager get(int pagingSize);

    /**
     * @param pagingSize
     * @param pagingLinkSize
     * @return {@link HtmlPager}
     */
    HtmlPager get(final int pagingSize, final int pagingLinkSize);
}
