/**
 * 
 */
package cc.aileron.template.compiler;

/**
 * @author aileron
 */
public interface Token
{
    /**
     * トークン種別
     * 
     * @author aileron
     */
    enum Category
    {
        CLOSE_TAG, CONTENT, STAG, TAG
    }

    /**
     * @return attribute
     */
    String attribute();

    /**
     * @return {@link Category}
     */
    Category category();

    /**
     * @return name
     */
    String name();
}
