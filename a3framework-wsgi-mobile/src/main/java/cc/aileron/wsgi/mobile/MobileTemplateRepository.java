/**
 * 
 */
package cc.aileron.wsgi.mobile;

import cc.aileron.generic.ObjectProvider;
import cc.aileron.template.Template;

/**
 * @author aileron
 */
public interface MobileTemplateRepository
{
    /**
     * @param csspath
     * @return template-repository
     */
    ObjectProvider<String, Template> get(String csspath);
}
