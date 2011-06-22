/**
 * 
 */
package cc.aileron.wsgi.template;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author aileron
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface WsgiTemplatePath
{
    /**
     * @return template-path
     */
    String value();
}
