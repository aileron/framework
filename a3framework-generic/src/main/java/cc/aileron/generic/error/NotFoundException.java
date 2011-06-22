/**
 * 
 */
package cc.aileron.generic.error;

/**
 * オブジェクトが存在しない場合の例外
 * 
 * @author aileron
 */
public class NotFoundException extends Exception
{
    private static final long serialVersionUID = 1L;

    /**
     * @param path
     */
    public NotFoundException(final String path)
    {
        super(path);
    }
}
