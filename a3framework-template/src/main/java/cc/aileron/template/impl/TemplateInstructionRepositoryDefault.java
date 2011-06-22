/**
 * 
 */
package cc.aileron.template.impl;

import cc.aileron.pojo.NoSuchPropertyException;
import cc.aileron.pojo.PojoAccessor;
import cc.aileron.pojo.PojoProperty;
import cc.aileron.template.compiler.TemplateContext;
import cc.aileron.template.compiler.TemplateInstruction;
import cc.aileron.template.compiler.TemplateInstructionTree;
import cc.aileron.template.compiler.TemplateProcedure;
import cc.aileron.template.compiler.Token;
import cc.aileron.template.method.DefMethod;
import cc.aileron.template.method.EachMethod;
import cc.aileron.template.method.RepMethod;
import cc.aileron.template.method.VariableExpand;

/**
 * @author aileron
 */
public class TemplateInstructionRepositoryDefault extends
        TemplateInstructionRepositoryAsbtract
{
    /**
     */
    public TemplateInstructionRepositoryDefault()
    {
        /*
         * plaintext
         */
        map.put("", new TemplateOperate()
        {
            @Override
            public TemplateInstruction get(final Token token)
            {
                return new TemplateInstruction()
                {
                    @Override
                    public TemplateProcedure procedure(
                            final Iterable<TemplateInstructionTree> child)
                    {
                        return proc;
                    }

                    TemplateProcedure proc = new TemplateProcedure()
                    {
                        @Override
                        public void call(final TemplateContext context)
                        {
                            context.write(token.attribute());
                        }
                    };
                };
            }
        });

        map.put("with", new TemplateOperate()
        {
            @Override
            public TemplateInstruction get(final Token token)
            {
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
                                try
                                {
                                    final PojoProperty eachTarget = context.object()
                                            .to(token.attribute());

                                    if (eachTarget == null)
                                    {
                                        return;
                                    }

                                    final PojoAccessor<Object> eachTargetAccessor = eachTarget.accessor();
                                    if (eachTargetAccessor == null)
                                    {
                                        return;
                                    }

                                    final PojoAccessor<Object> old = context.object();
                                    context.object(old.add(eachTargetAccessor));
                                    for (final TemplateInstructionTree tree : child)
                                    {
                                        tree.self()
                                                .procedure(tree.child())
                                                .call(context);
                                    }
                                    context.object(old);
                                }
                                catch (final NoSuchPropertyException e)
                                {
                                    throw e.error();
                                }
                            }
                        };
                    }

                };
            }
        });

        map.put("def-rep", new TemplateOperate()
        {
            @Override
            public TemplateInstruction get(final Token token)
            {
                final String args = token.attribute();
                final int point = args.indexOf('&');
                final DefMethod def = new DefMethod(args.substring(0, point));
                final RepMethod rep = new RepMethod(args.substring(point + 1));
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
                                try
                                {
                                    if (def.call(context, child))
                                    {
                                        context.write(rep.call(context, child));
                                        return;
                                    }
                                    for (final TemplateInstructionTree tree : child)
                                    {
                                        tree.self()
                                                .procedure(tree.child())
                                                .call(context);
                                    }
                                }
                                catch (final NoSuchPropertyException e)
                                {
                                    throw e.error();
                                }
                            }
                        };
                    }

                };
            }
        });

        map.put("rep", new TemplateOperate()
        {

            @Override
            public TemplateInstruction get(final Token token)
            {
                final RepMethod rep = new RepMethod(token.attribute());
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
                                try
                                {
                                    context.write(rep.call(context, child));
                                }
                                catch (final NoSuchPropertyException e)
                                {
                                    throw e.error();
                                }
                            }
                        };
                    }

                };
            }
        });
        map.put("def", new TemplateOperate()
        {

            @Override
            public TemplateInstruction get(final Token token)
            {
                final DefMethod def = new DefMethod(token.attribute());
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
                                try
                                {
                                    if (!def.call(context, child))
                                    {
                                        return;
                                    }
                                    for (final TemplateInstructionTree tree : child)
                                    {
                                        tree.self()
                                                .procedure(tree.child())
                                                .call(context);
                                    }
                                }
                                catch (final NoSuchPropertyException e)
                                {
                                    throw e.error();
                                }
                            }
                        };
                    }

                };
            }
        });
        map.put("comment", new TemplateOperate()
        {

            @Override
            public TemplateInstruction get(final Token token)
            {
                return new TemplateInstruction()
                {
                    @Override
                    public TemplateProcedure procedure(
                            final Iterable<TemplateInstructionTree> child)
                    {
                        return proc;
                    }

                    TemplateProcedure proc = new TemplateProcedure()
                    {
                        @Override
                        public void call(final TemplateContext context)
                        {
                        }
                    };
                };
            }
        });

        map.put("each", new TemplateOperate()
        {
            @Override
            public TemplateInstruction get(final Token token)
            {
                final EachMethod each = new EachMethod(token);
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
                                final PojoAccessor<Object> old = context.object();
                                try
                                {
                                    for (final PojoAccessor<Object> object : each.call(context,
                                            child))
                                    {
                                        context.object(object);
                                        for (final TemplateInstructionTree c : child)
                                        {
                                            c.self()
                                                    .procedure(c.child())
                                                    .call(context);
                                        }
                                    }
                                }
                                catch (final NoSuchPropertyException e)
                                {
                                    throw e.error();
                                }
                                finally
                                {
                                    context.object(old);
                                }
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
                return new TemplateInstruction()
                {
                    @Override
                    public TemplateProcedure procedure(
                            final Iterable<TemplateInstructionTree> child)
                    {
                        return proc;
                    }

                    TemplateProcedure proc = new TemplateProcedure()
                    {
                        @Override
                        public void call(final TemplateContext context)
                        {
                            context.write(ve.expand(context.object()));
                        }
                    };

                    VariableExpand ve = new VariableExpand(token.attribute());
                };
            }
        });
    }
}
