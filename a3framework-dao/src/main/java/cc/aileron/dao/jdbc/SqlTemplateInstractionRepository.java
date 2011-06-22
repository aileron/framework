/**
 * 
 */
package cc.aileron.dao.jdbc;

import static cc.aileron.generic.$.*;
import cc.aileron.pojo.PojoAccessor;
import cc.aileron.template.compiler.TemplateContext;
import cc.aileron.template.compiler.TemplateInstruction;
import cc.aileron.template.compiler.TemplateInstructionTree;
import cc.aileron.template.compiler.TemplateProcedure;
import cc.aileron.template.compiler.Token;
import cc.aileron.template.impl.TemplateInstructionRepositoryDefault;
import cc.aileron.template.method.VariableExpand;

/**
 * @author aileron
 */
public class SqlTemplateInstractionRepository extends
        TemplateInstructionRepositoryDefault
{
    /**
     */
    public SqlTemplateInstractionRepository()
    {
        map.put("rep", new TemplateOperate()
        {
            @Override
            public TemplateInstruction get(final Token token)
            {
                final String name = token.attribute();
                final VariableExpand ve = new VariableExpand(name);
                return new TemplateInstruction()
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
                                final PojoAccessor<SqlTemplateContext> p = cast(context.object());
                                context.write(p.target().rep(ve));
                            }
                        };
                    }
                };
            }
        });

        map.put("var", new TemplateOperate()
        {
            @Override
            public TemplateInstruction get(final Token token)
            {
                final String name = token.attribute();
                return new TemplateInstruction()
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
                                final PojoAccessor<SqlTemplateContext> p = cast(context.object());
                                p.target().var(name);
                                context.write("?");
                            }
                        };
                    }

                };
            }
        });
    }
}
