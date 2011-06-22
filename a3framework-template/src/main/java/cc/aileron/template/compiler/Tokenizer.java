/**
 * 
 */
package cc.aileron.template.compiler;

/**
 * @author aileron
 */
public interface Tokenizer
{
    /**
     * @param symbol
     * @param string
     * @return {@link Iterable}
     */
    Iterable<Token> get(String string);
}
