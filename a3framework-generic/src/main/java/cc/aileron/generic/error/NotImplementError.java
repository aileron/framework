/**
 * 
 */
package cc.aileron.generic.error;

/**
 * @author aileron
 * 
 */
public class NotImplementError extends Error
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public NotImplementError()
    {
    }

    /**
     * @param arg0
     */
    public NotImplementError(final String arg0)
    {
        super(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     */
    public NotImplementError(final String arg0, final Throwable arg1)
    {
        super(arg0, arg1);
    }

    /**
     * @param arg0
     */
    public NotImplementError(final Throwable arg0)
    {
        super(arg0);
    }

}
