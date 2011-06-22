package cc.aileron.web;

/**
 * 処理
 * 
 * @param <T>
 */
public interface WebProcess<T>
{
    /**
     * 処理結果
     */
    public static enum Case
    {
        /**
         * 継続
         */
        CONTINUE,

        /**
         * 再度
         */
        RETRY,

        /**
         * 終端
         */
        TERMINATE;
    }

    /**
     * @param resource
     * @return {@link Case}
     * @throws Exception
     */
    Case process(T resource) throws Exception;
}