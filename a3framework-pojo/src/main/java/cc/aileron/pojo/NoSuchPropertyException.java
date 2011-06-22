/**
 * 
 */
package cc.aileron.pojo;

import cc.aileron.generic.$;
import cc.aileron.generic.ObjectReference;

/**
 * プロパティが見付からない場合の例外
 * 
 * @author aileron
 */
public class NoSuchPropertyException extends Exception
{
    private static final long serialVersionUID = 1L;

    /**
     * @return error
     */
    public NoSuchPropertyError error()
    {
        return new NoSuchPropertyError(name, target);
    }

    /**
     * @param target
     * @param name
     */
    public NoSuchPropertyException(final String name, final Object... target)
    {
        super("[" + $.join(",", new ObjectReference<String>()
        {
            @Override
            public String get()
            {
                if (i < len)
                {
                    return target[i++].getClass().getName();
                }
                return null;
            }

            int i = 0;
            int len = target.length;
        }) + "]@" + name);
        this.target = target;
        this.name = name;
    }

    /**
     * target
     */
    public final Object[] target;
    private final String name;
}
