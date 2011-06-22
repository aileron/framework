package cc.aileron.dao.jdbc;

/**
 * LOG
 * 
 * @author Aileron
 */
public interface StatmentLogger
{
    /**
     * @return isEnable
     */
    boolean isEnable();

    /**
     * @param parameter
     */
    void output(StatmentParameter parameter);
}