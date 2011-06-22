/**
 * 
 */
package cc.aileron.template.compiler;

/**
 * @author aileron
 */
public interface TemplateInstructionTree
{
    /**
     * @param tree
     */
    void add(TemplateInstructionTree tree);

    /**
     * @return child
     */
    Iterable<TemplateInstructionTree> child();

    /**
     * @return parent
     */
    TemplateInstructionTree parent();

    /**
     * @return self
     */
    TemplateInstruction self();
}
