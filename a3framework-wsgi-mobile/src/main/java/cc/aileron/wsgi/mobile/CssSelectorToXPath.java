/**
 *
 */
package cc.aileron.wsgi.mobile;

import com.google.inject.ImplementedBy;

/**
 * @author aileron
 */
@ImplementedBy(CssSelectorToXPathImpl.class)
public interface CssSelectorToXPath
{
    /**
     * @param css
     * @return convert
     */
    String convert(String css);
}
