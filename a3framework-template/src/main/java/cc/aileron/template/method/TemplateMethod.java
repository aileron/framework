/**
 * 
 */
package cc.aileron.template.method;

import cc.aileron.pojo.NoSuchPropertyException;
import cc.aileron.template.compiler.TemplateContext;
import cc.aileron.template.compiler.TemplateInstructionTree;

/**
 * @author aileron
 * @param <T>
 */
public interface TemplateMethod<T>
{
    /**
     * @param context
     * @param child
     * @return result
     * @throws NoSuchPropertyException
     */
    T call(TemplateContext context, Iterable<TemplateInstructionTree> child)
            throws NoSuchPropertyException;
}
