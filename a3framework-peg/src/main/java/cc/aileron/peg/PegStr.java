package cc.aileron.peg;

/**
 * @author aileron
 */
public class PegStr implements PegValue<String>
{
    @Override
    public String get()
    {
        return string;
    }

    @Override
    public boolean isArray()
    {
        return false;
    }

    @Override
    public boolean isBlank()
    {
        return false;
    }

    @Override
    public String toString()
    {
        return "PegStr[" + string + "]";
    }

    /**
     * @param next
     */
    public PegStr(final char next)
    {
        string = String.valueOf(next);
    }

    /**
     * @param string
     */
    public PegStr(final String string)
    {
        this.string = string;
    }

    private final String string;
}