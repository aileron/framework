package cc.aileron.peg;


/**
 * @author aileron
 */
public interface PegParser
{
    /**
     * @param context
     * @return {@link PegValue}
     */
    PegValue<?> parse(PegContext context);
}