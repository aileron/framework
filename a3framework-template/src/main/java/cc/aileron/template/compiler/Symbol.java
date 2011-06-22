/**
 * 
 */
package cc.aileron.template.compiler;

/**
 * @author aileron
 */
public interface Symbol
{
    /**
     * @return 開始タグ終端
     */
    String end();

    /**
     * @return 開始タグ
     */
    String start();
}
