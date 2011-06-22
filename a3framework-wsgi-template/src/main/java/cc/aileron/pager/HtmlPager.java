/**
 *
 */
package cc.aileron.pager;

import java.util.List;

import cc.aileron.dao.DataFinder;
import cc.aileron.generic.util.SkipList;

/**
 * @author aileron
 */
public interface HtmlPager
{
    /**
     * @param baseUrl
     */
    void baseUrl(String baseUrl);

    /**
     * @return {@link HtmlPagerContext}
     */
    HtmlPagerContext context();

    /**
     * @return 件数
     */
    int countAll();

    /**
     * @return 現在表示中のページ、終了件数
     */
    int countEnd();

    /**
     * @return 現在表示中のページ、開始件数
     */
    int countStart();

    /**
     * @param <T>
     * @param finder
     * @return List<T>
     */
    <T> List<T> find(DataFinder<T> finder);

    /**
     * @return まえページが存在するか
     */
    boolean isPageBack();

    /**
     * @return isPageBack span
     */
    boolean isPageBackSpan();

    /**
     * @return つぎページが存在するか
     */
    boolean isPageNext();

    /**
     * @return isPageNext Span
     */
    boolean isPageNextSpan();

    /**
     * @return まえへリンク
     */
    String linkBack();

    /**
     * @return linkSpanBack
     */
    String linkBackSpan();

    /**
     * @return 次へリンク
     */
    String linkNext();

    /**
     * @return linkSpanNext
     */
    String linkNextSpan();

    /**
     * @return リンク
     */
    List<HtmlPageLink> links();

    /**
     * @return ページ件数
     */
    int pageCount();

    /**
     * @return pageNumber
     */
    int pageNumber();

    /**
     * @param pageNumber
     * @return {@link HtmlPagerContext}
     */
    HtmlPagerContext pageNumber(int pageNumber);

    /**
     * @return size
     */
    int size();

    /**
     * @param pageSize
     */
    void size(int pageSize);

    /**
     * factory
     */
    HtmlPagerFactory factory = new HtmlPagerFactory()
    {
        @Override
        public HtmlPager get(final int pagingSize)
        {
            return get(pagingSize, 10);
        }

        @Override
        public HtmlPager get(final int pagingSize, final int pagingLinkSize)
        {
            final HtmlPagerContext paging = new HtmlPagerContext(pagingSize);
            return new HtmlPager()
            {
                @Override
                public void baseUrl(final String baseUrl)
                {
                    this.baseUrl = baseUrl;
                }

                @Override
                public HtmlPagerContext context()
                {
                    return paging;
                }

                @Override
                public int countAll()
                {
                    return paging.count();
                }

                @Override
                public int countEnd()
                {
                    final int countEnd = paging.offset() + paging.limit();
                    return countEnd > countAll() ? countAll() : countEnd;
                }

                @Override
                public int countStart()
                {
                    return paging.count() == 0 ? 0 : paging.offset() + 1;
                }

                @Override
                public <T> List<T> find(final DataFinder<T> finder)
                {
                    context().count(finder.count());
                    return finder.list(context());
                }

                @Override
                public boolean isPageBack()
                {
                    return paging.pageNumber() > 1;
                }

                @Override
                public boolean isPageBackSpan()
                {
                    return paging.pageNumber() - pagingLinkSize > 1;
                }

                @Override
                public boolean isPageNext()
                {
                    return paging.pageNumber() < paging.pageMax();
                }

                @Override
                public boolean isPageNextSpan()
                {
                    return paging.pageNumber() + pagingLinkSize < paging.pageMax();
                }

                @Override
                public String linkBack()
                {
                    return baseUrl + (paging.pageNumber() - 1);
                }

                @Override
                public String linkBackSpan()
                {
                    return baseUrl + (paging.pageNumber() - pagingLinkSize);
                }

                @Override
                public String linkNext()
                {
                    return baseUrl + (paging.pageNumber() + 1);
                }

                @Override
                public String linkNextSpan()
                {
                    return baseUrl + (paging.pageNumber() + pagingLinkSize);
                }

                @Override
                public List<HtmlPageLink> links()
                {
                    final List<HtmlPageLink> links = new SkipList<HtmlPageLink>();
                    final HtmlPagerLocalContext context = new HtmlPagerLocalContext(paging.pageNumber(),
                            paging.pageMax(),
                            pagingLinkSize);
                    final int page = paging.pageNumber();
                    final int start = context.start;
                    final int end = context.end;
                    for (int i = start; i <= end; i++)
                    {
                        final String url = baseUrl + i;
                        final String label = String.valueOf(i);
                        final boolean isSelected = i == page;
                        final boolean isFirstPage = i == 1;
                        links.add(new HtmlPageLink()
                        {
                            @Override
                            public String href()
                            {
                                return url;
                            }

                            @Override
                            public boolean isFirstPage()
                            {
                                return isFirstPage;
                            }

                            @Override
                            public boolean isNotSelected()
                            {
                                return !isSelected;
                            }

                            @Override
                            public boolean isSelected()
                            {
                                return isSelected;
                            }

                            @Override
                            public String label()
                            {
                                return label;
                            }
                        });
                    }
                    return links;
                }

                @Override
                public int pageCount()
                {
                    return paging.pageMax();
                }

                @Override
                public int pageNumber()
                {
                    return paging.pageNumber();
                }

                @Override
                public HtmlPagerContext pageNumber(final int pageNumber)
                {
                    paging.pageNumber(pageNumber);
                    return paging;
                }

                @Override
                public int size()
                {
                    return paging.pageSize;
                }

                @Override
                public void size(final int pageSize)
                {
                    paging.pageSize = pageSize;
                }

                private String baseUrl = "";
            };
        }
    };
}

class HtmlPagerLocalContext
{
    /**
     * @param page
     * @param maxPage
     * @param linkSize
     */
    public HtmlPagerLocalContext(final int page, final int maxPage,
            final int linkSize)
    {
        final int point = page;
        final int max = maxPage;
        final int allocateSize = linkSize / 2;

        final int forthAllocatePointPlan = point - allocateSize;
        final int backAllocatePointPlan = point + allocateSize - 1;

        final boolean isForthAllocate = forthAllocatePointPlan > 0;
        final boolean isBackAllocate = backAllocatePointPlan < max;

        final int forthAllocatePoint = isForthAllocate ? forthAllocatePointPlan
                : 1;
        final int backAllocatePoint = isBackAllocate ? backAllocatePointPlan
                : max;

        final int forthAllocateSize = point - forthAllocatePoint;
        final int backAllocateSize = backAllocatePoint - point;

        final int missForthAllocationSize = allocateSize - forthAllocateSize;
        final int missBackAllocationSize = allocateSize - backAllocateSize;

        if (isBackAllocate)
        {
            this.start = forthAllocatePoint;
        }
        else
        {
            final int tmp = forthAllocatePoint - missBackAllocationSize;
            this.start = tmp < 1 ? 1 : tmp;
        }

        if (isForthAllocate)
        {
            this.end = backAllocatePoint;
        }
        else
        {
            final int tmp = backAllocatePoint + missForthAllocationSize;
            this.end = tmp > max ? max : tmp;
        }
    }

    /**
     * end
     */
    public final int end;

    /**
     * start
     */
    public final int start;
}