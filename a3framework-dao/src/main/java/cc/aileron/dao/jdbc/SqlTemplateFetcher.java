/**
 * 
 */
package cc.aileron.dao.jdbc;

import cc.aileron.pojo.PojoAccessor;

/**
 * @author aileron
 */
public interface SqlTemplateFetcher
{
    /**
     * @param type
     * @param category
     * @param pojo
     * @return {@link StatmentParameter}
     */
    StatmentParameter fetch(SqlTemplateCategory category, PojoAccessor<?> pojo);
}
