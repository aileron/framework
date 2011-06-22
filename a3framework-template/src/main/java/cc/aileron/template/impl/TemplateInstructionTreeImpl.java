/**
 * 
 */
package cc.aileron.template.impl;

import cc.aileron.generic.util.SkipList;
import cc.aileron.template.compiler.TemplateInstruction;
import cc.aileron.template.compiler.TemplateInstructionTree;

/**
 * @author aileron
 */
public class TemplateInstructionTreeImpl implements TemplateInstructionTree
{
    @Override
    public void add(final TemplateInstructionTree tree)
    {
        child.add(tree);
    }

    @Override
    public Iterable<TemplateInstructionTree> child()
    {
        return child;
    }

    @Override
    public TemplateInstructionTree parent()
    {
        return parent;
    }

    @Override
    public TemplateInstruction self()
    {
        return self;
    }

    /**
     * @param parent
     * @param self
     */
    public TemplateInstructionTreeImpl(final TemplateInstructionTree parent,
            final TemplateInstruction self)
    {
        this.parent = parent;
        this.self = self;
    }

    final SkipList<TemplateInstructionTree> child = new SkipList<TemplateInstructionTree>();
    final TemplateInstructionTree parent;
    final TemplateInstruction self;
}
