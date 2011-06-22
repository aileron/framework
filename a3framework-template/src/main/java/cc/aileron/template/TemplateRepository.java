/**
 * 
 */
package cc.aileron.template;

import cc.aileron.generic.ObjectProvider;
import cc.aileron.generic.Resource;
import cc.aileron.template.compiler.Symbol;
import cc.aileron.template.impl.TemplateInstructionRepositoryDefault;
import cc.aileron.template.impl.TemplateRepositoryImpl;

/**
 * @author aileron
 */
public interface TemplateRepository extends ObjectProvider<Resource, Template>
{
    /**
     * JSON (C型式)
     */
    TemplateRepository JSON = new TemplateRepositoryImpl(new Symbol()
    {
        @Override
        public String end()
        {
            return "*/";
        }

        @Override
        public String start()
        {
            return "/*";
        }
    }, new TemplateInstructionRepositoryDefault());

    /**
     * XML 型式の
     */
    TemplateRepository XML = new TemplateRepositoryImpl(new Symbol()
    {
        @Override
        public String end()
        {
            return "-->";
        }

        @Override
        public String start()
        {
            return "<!--";
        }
    }, new TemplateInstructionRepositoryDefault());
}
