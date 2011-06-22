/**
 *
 */
package cc.aileron.pager;

/**
 * @author aileron
 */
public interface HtmlPageLink
{
    /**
     * @return href
     */
    String href();

    /**
     * @return isFirstPage
     */
    boolean isFirstPage();

    /**
     * @return is not selected
     */
    boolean isNotSelected();

    /**
     * @return is selected
     */
    boolean isSelected();

    /**
     * @return label
     */
    String label();
}
