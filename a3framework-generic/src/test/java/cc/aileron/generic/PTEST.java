package cc.aileron.generic;

import java.util.HashMap;

import org.junit.Test;

/**
 * @author aileron
 */
public class PTEST
{
    /**
     * hash
     */
    @Test
    public void hash()
    {
        final HashMap<Integer, Integer> p = new HashMap<Integer, Integer>();
        for (int i = 0; i < 100000; i++)
        {
            p.put(i, i + 2);
        }
    }

    /**
     * spec
     */
    @Test
    public void spec()
    {
    }
}
