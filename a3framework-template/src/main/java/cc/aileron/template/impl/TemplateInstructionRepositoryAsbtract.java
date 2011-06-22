/**
 * 
 */
package cc.aileron.template.impl;

import static cc.aileron.generic.$.*;

import java.util.HashMap;

import cc.aileron.template.compiler.TemplateInstruction;
import cc.aileron.template.compiler.TemplateInstructionRepository;
import cc.aileron.template.compiler.Token;

/**
 * @author aileron
 */
public class TemplateInstructionRepositoryAsbtract implements
        TemplateInstructionRepository
{
    @Override
    public TemplateInstruction get(final Token token) throws NotOperateError
    {
        final TemplateOperate method = map.get(token.name());
        if (method == null)
        {
            throw new NotOperateError(token.name());
        }
        return cast(method.get(token));
    }

    protected final HashMap<String, TemplateOperate> map = new HashMap<String, TemplateInstructionRepository.TemplateOperate>();

}
