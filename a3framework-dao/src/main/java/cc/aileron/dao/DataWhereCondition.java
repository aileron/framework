/**
 * 
 */
package cc.aileron.dao;

/**
 * @author aileron
 * @param <T>
 */
public class DataWhereCondition<T>
{
    /**
     * @param <T>
     * @param type
     * @param value
     * @return {@link DataWhereCondition}
     */
    public static <T> DataWhereCondition<T> condition(final Class<T> type,
            final T value)
    {
        return new DataWhereCondition<T>(type, value);
    }

    /**
     * @return target
     */
    public T value()
    {
        return value;
    }

    /**
     * @return type
     */
    public Class<T> type()
    {
        return type;
    }

    /**
     * @param type
     * @param value
     */
    public DataWhereCondition(final Class<T> type, final T value)
    {
        this.type = type;
        this.value = value;
    }

    private final T value;
    private final Class<T> type;
}
