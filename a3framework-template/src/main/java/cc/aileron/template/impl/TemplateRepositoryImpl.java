/**
 * 
 */
package cc.aileron.template.impl;

import static cc.aileron.generic.$.*;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import cc.aileron.generic.Resource;
import cc.aileron.generic.util.SoftHashMap;
import cc.aileron.pojo.PojoAccessor;
import cc.aileron.template.Template;
import cc.aileron.template.TemplateRepository;
import cc.aileron.template.compiler.Symbol;
import cc.aileron.template.compiler.TemplateContext;
import cc.aileron.template.compiler.TemplateInstructionRepository;
import cc.aileron.template.compiler.TemplateInstructionTree;

/**
 * @author aileron
 */
public class TemplateRepositoryImpl implements TemplateRepository
{
    @Override
    public Template get(final Resource resource)
    {
        if (resource == null)
        {
            throw new IllegalArgumentException("resource");
        }
        final String path = resource.path();
        if (resource.isNotFound())
        {
            throw new Error(new FileNotFoundException(path));
        }
        final String string = resource.toString();
        final Template cache = this.caches.get(string.hashCode());
        if (cache != null)
        {
            return cache;
        }

        final TemplateInstructionTree tree;
        try
        {
            tree = parser.parse(path, tokenizer.get(string));
        }
        catch (final Exception e)
        {
            throw new Error(e);
        }
        return new Template()
        {
            @Override
            public void print(final PojoAccessor<?> accessor,
                    final PrintWriter writer)
            {
                final StringBuilder builder = new StringBuilder();
                tree.self().procedure(tree.child()).call(new TemplateContext()
                {
                    @Override
                    public PojoAccessor<Object> object()
                    {
                        return object;
                    }

                    @Override
                    public void object(final PojoAccessor<?> object)
                    {
                        this.object = cast(object);
                    }

                    @Override
                    public void write(final String content)
                    {
                        builder.append(content);
                    }

                    private PojoAccessor<Object> object = cast(accessor);
                });
                writer.print(builder.toString());
                writer.flush();
            }

            @Override
            public String toString()
            {
                return string;
            }
        };
    }

    /**
     * @param symbol
     * @param instructionFactory
     */
    public TemplateRepositoryImpl(final Symbol symbol,
            final TemplateInstructionRepository instructionFactory)
    {
        tokenizer = new TokenizerImpl(symbol);
        parser = new ParserImpl(instructionFactory);
    }

    final SoftHashMap<Integer, Template> caches = new SoftHashMap<Integer, Template>();
    private final ParserImpl parser;
    private final TokenizerImpl tokenizer;
}
