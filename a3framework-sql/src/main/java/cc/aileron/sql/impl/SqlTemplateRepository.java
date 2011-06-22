/**
 * 
 */
package cc.aileron.sql.impl;

/**
 * @author aileron
 */
public interface SqlTemplateRepository
{
    /**
     * @param typeSql
     * @param typeResult
     * @return Object
     */
    Object get(Class<?> typeSql, Class<?> typeResult);
}
