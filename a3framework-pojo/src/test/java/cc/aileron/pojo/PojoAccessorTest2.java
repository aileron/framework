/**
 * 
 */
package cc.aileron.pojo;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

/**
 * PojoAccessorTest
 */
public class PojoAccessorTest2
{
    /**
     * spec
     * 
     * @throws NoSuchPropertyException
     * @throws InvocationTargetException
     * 
     */
    @Test
    public void spec()
            throws NoSuchPropertyException, InvocationTargetException
    {
        final Object parent = new Object()
        {
            public int get(final String key) throws NoSuchPropertyException
            {
                if (!key.equals("z"))
                {
                    throw new NoSuchPropertyException(key, this);
                }
                return 600;
            }

            public int c = 5;
        };

        final Object object = new Object()
        {
            public int get(final String key)
            {
                if (!key.equals("b"))
                {
                    return 0;
                }
                return 200;
            }

            public String a = "100";
        };

        final PojoAccessor<Object> accessor = repository.from(object)
                .add(parent);

        /*
         * 型変換無し
         */
        assertThat(accessor.to("a").get(String.class), is("100"));
        assertThat(accessor.to("b").get(String.class), is("200"));
        assertThat(accessor.to("c").get(String.class), is("5"));
        assertThat(accessor.to("d").get(String.class), is("200"));
    }

    final PojoAccessorRepository repository = PojoAccessor.Repository;
}
