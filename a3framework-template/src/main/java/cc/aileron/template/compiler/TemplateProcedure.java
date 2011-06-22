/**
 * 
 */
package cc.aileron.template.compiler;


/**
 * @author aileron
 */
public interface TemplateProcedure
{
    /**
     * @param context
     * @param instruction
     */
    void call(TemplateContext context);
}
