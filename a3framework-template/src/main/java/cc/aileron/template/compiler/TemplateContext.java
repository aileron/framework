/**
 * 
 */
package cc.aileron.template.compiler;

import cc.aileron.pojo.PojoAccessor;

/**
 * @author aileron
 */
public interface TemplateContext
{
    /**
     * @return object
     */
    PojoAccessor<Object> object();

    /**
     * @param object
     */
    void object(PojoAccessor<?> object);

    /**
     * @param content
     */
    void write(String content);
}
