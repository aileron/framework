/**
 *
 */
package cc.aileron.template;

/**
 * each コンテキスト
 * 
 * @author aileron
 */
public interface EachContext
{
    /**
     * @return index counter
     */
    int i();

    /**
     * @return 終端フラグ
     * @throws UnsupportedOperationException
     *             対応しない場合、例外が送出される
     * 
     */
    boolean z() throws UnsupportedOperationException;
}
