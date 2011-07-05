/**
 * 
 */
package cc.aileron.pojo;

import cc.aileron.generic.$;
import cc.aileron.generic.ObjectReference;

/**
 * プロパティが見付からない場合の実行時例外
 * 
 * setterしか存在しないプロパティを読みこんだ際
 * 
 * getterしか存在しないプロパティに書きこんだ際
 * 
 * @author aileron
 */
public class NoSuchPropertyError extends Error
{
    private static final long serialVersionUID = 1L;

    /**
     * @param target
     * @param name
     */
    public NoSuchPropertyError(final String name, final Object... target)
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
    }

    /**
     * @param e
     * @param target
     * @param name
     */
    public NoSuchPropertyError(final Throwable e, final String name,
            final Object... target)
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
        }) + "]@" + name, e);
    }
}
