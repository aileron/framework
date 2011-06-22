/**
 * 
 */
package cc.aileron.generic;

/**
 * String 便利クラス
 * 
 * @author aileron
 */
public class EString
{
    /**
     * @return is empty
     */
    public boolean isEmpty()
    {
        return string == null || string.isEmpty();
    }

    /**
     * @return is null
     */
    public boolean isNull()
    {
        return string == null;
    }

    /**
     * @return length
     */
    public int length()
    {
        return string == null ? 0 : string.length();
    }

    /**
     * @return boolean
     */
    public boolean toBool()
    {
        return string == null ? false : Boolean.parseBoolean(string);
    }

    /**
     * @return toInt
     */
    public int toInt()
    {
        return string == null ? 0 : Integer.parseInt(string);
    }

    /**
     * @return long
     */
    public long toLong()
    {
        return string == null ? 0 : Long.parseLong(string);
    }

    @Override
    public String toString()
    {
        return string;
    }

    /**
     * @param string
     */
    public EString(final String string)
    {
        this.string = string;
    }

    private final String string;
}
