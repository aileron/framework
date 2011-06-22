/**
 * 
 */
package cc.aileron.dao.jdbc;

import cc.aileron.template.method.VariableExpand;

/**
 * @author aileron
 */
public interface SqlTemplateContext
{
    /**
     * @param name
     */
    void var(final String name);

    /**
     * @param ve
     * @return var
     */
    String rep(VariableExpand ve);
}
