/**
 * 
 */
package cc.aileron.dao.jdbc;

import java.util.List;

/**
 * 実行可能SQL
 * 
 * @author aileron
 */
public interface StatmentParameter
{
    /**
     * @return parameters
     */
    List<Object> arguments();

    /**
     * @return {@link SqlTemplateCategory}
     */
    SqlTemplateCategory category();

    /**
     * @return name
     */
    String name();

    /**
     * @return sql
     */
    String sql();
}
