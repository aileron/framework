/**
 * 
 */
package cc.aileron.template.impl;

import static cc.aileron.generic.$.*;
import cc.aileron.generic.Procedure;
import cc.aileron.template.compiler.Parser;
import cc.aileron.template.compiler.TemplateContext;
import cc.aileron.template.compiler.TemplateInstruction;
import cc.aileron.template.compiler.TemplateInstructionRepository;
import cc.aileron.template.compiler.TemplateInstructionTree;
import cc.aileron.template.compiler.TemplateProcedure;
import cc.aileron.template.compiler.Token;

/**
 * @author aileron
 */
public class ParserImpl implements Parser
{
    @Override
    public TemplateInstructionTree parse(final String path,
            final Iterable<Token> tokens) throws Exception
    {
        final TemplateInstructionTree root = new TemplateInstructionTreeImpl(null,
                new TemplateInstruction()
                {
                    @Override
                    public TemplateProcedure procedure(
                            final Iterable<TemplateInstructionTree> child)
                    {
                        return new TemplateProcedure()
                        {
                            @Override
                            public void call(final TemplateContext context)
                            {
                                for (final TemplateInstructionTree e : child)
                                {
                                    final TemplateInstruction self = e.self();
                                    self.procedure(e.child()).call(context);

                                }
                            }
                        };
                    }

                });
        apply(tokens, new Procedure<Token>()
        {
            @Override
            public void call(final Token token)
            {
                switch (token.category())
                {
                case CONTENT:
                    tree.add(new TemplateInstructionTreeImpl(tree,
                            factory.get(token)));
                    break;

                case STAG:
                    tree.add(new TemplateInstructionTreeImpl(tree,
                            factory.get(token)));
                    break;

                case TAG:
                {
                    final TemplateInstructionTree parent = tree;
                    tree = new TemplateInstructionTreeImpl(parent,
                            factory.get(token));
                    parent.add(tree);
                    break;
                }

                case CLOSE_TAG:
                    tree = tree.parent();
                    break;
                }
            }

            TemplateInstructionTree tree = root;
        });

        return root;
    }

    /**
     * @param factory
     */
    public ParserImpl(final TemplateInstructionRepository factory)
    {
        this.factory = factory;
    }

    final TemplateInstructionRepository factory;
}
