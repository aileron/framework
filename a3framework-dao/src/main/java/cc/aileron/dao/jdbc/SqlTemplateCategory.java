/**
 * 
 */
package cc.aileron.dao.jdbc;

/**
 * @author aileron
 */
public enum SqlTemplateCategory
{
    /**
     * count
     */
    COUNT("count"),

    /**
     * delete
     */
    DELETE("delete"),

    /**
     * execute
     */
    EXECUTE("execute"),

    /**
     * find
     */
    FIND("find"),

    /**
     * find
     */
    FIND_ONE("find"),

    /**
     * find
     */
    FIND_PAGING("find"),

    /**
     * insert
     */
    INSERT("insert"),

    /**
     * update
     */
    UPDATE("update");

    private SqlTemplateCategory(final String type)
    {
        this.type = type;
    }

    /**
     * type
     */
    public final String type;
}
