/**
 * 
 */
package cc.aileron.hier;

import com.google.inject.ImplementedBy;


/**
 * @author aileron
 */
@ImplementedBy(UrlTreeContainerDelimiterDefault.class)
public interface UrlTreeContainerDelimiter
{
    /**
     * @return delimiter
     */
    String value();
}
