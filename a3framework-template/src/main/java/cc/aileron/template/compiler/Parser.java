/**
 * 
 */
package cc.aileron.template.compiler;

/**
 * @author aileron
 */
public interface Parser
{
    /**
     * @param path
     * @param tokens
     * @return instruction
     * @throws Exception
     */
    TemplateInstructionTree parse(String path, Iterable<Token> tokens)
            throws Exception;
}
