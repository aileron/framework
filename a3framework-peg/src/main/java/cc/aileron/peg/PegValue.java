package cc.aileron.peg;

/**
 * @author aileron
 * @param <T>
 */
public interface PegValue<T>
{
    /**
     * @return T
     */
    T get();

    /**
     * @return is array
     */
    boolean isArray();

    /**
     * @return is blank
     */
    boolean isBlank();
}