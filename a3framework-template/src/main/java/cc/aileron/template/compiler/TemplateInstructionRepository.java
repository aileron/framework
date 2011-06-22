/**
 * 
 */
package cc.aileron.template.compiler;

import cc.aileron.generic.ObjectProvider;

/**
 * @author aileron
 */
public interface TemplateInstructionRepository
{
    /**
     * @author aileron
     */
    class NotOperateError extends Error
    {
        private static final long serialVersionUID = 1L;

        /**
         * @param path
         */
        public NotOperateError(final String path)
        {
            super(path);
        }
    }

    /**
     * Provider
     */
    interface TemplateOperate extends
            ObjectProvider<Token, TemplateInstruction>
    {
    }

    /**
     * @param name
     * @param parent
     * @param token
     * @return {@link TemplateInstruction}
     * @throws NotOperateError
     */
    TemplateInstruction get(Token token) throws NotOperateError;
}
