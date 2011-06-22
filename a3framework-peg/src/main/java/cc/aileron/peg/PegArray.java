package cc.aileron.peg;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author aileron
 */
public class PegArray implements PegValue<Object>, Iterable<PegValue<?>>
{
    /**
     * @param value
     */
    public void add(final PegValue<?> value)
    {
        if (!value.isBlank())
        {
            array.add(value);
        }
    }

    /**
     * @param array
     */
    public void addAll(final PegArray array)
    {
        this.array.addAll(array.array);
    }

    @Override
    public PegValue<?> get()
    {
        return array.get(0);
    }

    /**
     * @param idx
     * @return {@link PegValue}
     */
    public PegValue<?> get(final int idx)
    {
        return array.get(idx);
    }

    @Override
    public boolean isArray()
    {
        return true;
    }

    @Override
    public boolean isBlank()
    {
        return false;
    }

    /**
     * @return is empty
     */
    public boolean isEmpty()
    {
        return array.isEmpty();
    }

    @Override
    public Iterator<PegValue<?>> iterator()
    {
        return array.iterator();
    }

    /**
     * @param i
     * @param value
     */
    public void set(final int i, final PegValue<?> value)
    {
        array.set(0, value);
    }

    /**
     * @return size
     */
    public int size()
    {
        return array.size();
    }

    @Override
    public String toString()
    {
        return "PegArray" + array;
    }

    final ArrayList<PegValue<?>> array = new ArrayList<PegValue<?>>();

}