package cc.aileron.peg;

/**
 * @author aileron
 */
public interface PegContext
{
    /**
     * @return char
     */
    char get();

    /**
     * @return eos
     */
    boolean hasNext();

    /**
     * @return char
     */
    char next();

    /**
     * @return offset
     */
    int offset();

    /**
     * @return string
     */
    char[] string();
}