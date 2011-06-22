/**
 * 
 */
package cc.aileron.dao.jdbc;

/**
 * SQLを実行する。
 * 
 * @author aileron
 */
public interface StatmentExecutor
{
    /**
     * @param template
     * @return update-count or generate id
     */
    int execute(final StatmentParameter template);

    /**
     * @param template
     * @param handller
     */
    void execute(final StatmentParameter template,
            final ResultSetHandller handller);
}
