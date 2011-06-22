/**
 *
 */
package cc.aileron.wsgi.mobile;

import com.google.inject.ImplementedBy;

/**
 * @author aileron
 */
@ImplementedBy(CssSelectorPropertiesFactoryImpl.class)
public interface CssSelectorPropertiesFactory
{
    /**
     * @param css
     * @return {@link CssSelectorProperties}
     */
    CssSelectorProperties parse(String css);
}
