/**
 * 
 */
package cc.aileron.pojo;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.junit.Test;

import cc.aileron.generic.util.SkipList;

/**
 * PojoAccessorTest
 */
public class PojoAccessorTest
{
    /**
     * Sample
     */
    public static class Sample
    {
        public String a()
        {
            return "[" + a + "]";
        }

        @Override
        public String toString()
        {
            return a + ":" + b;
        }

        public String a = "test";

        public int b = 10;

        public boolean c = false;

        public SampleC category = null;

        public Sample ch;

        public List<String> list = new SkipList<String>("a", "B", "c", "D");

    }

    /**
     * sample
     */
    public static enum SampleC
    {
        /**
         * 
         */
        A, /**
           * 
           */
        B, /**
           * 
           */
        C
    }

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

        final Sample sample = new Sample();
        final PojoAccessor<Sample> accessor = repository.from(sample);

        accessor.to("b").set("100");
        accessor.to("c").set("true");
        accessor.to("ch").set(new Sample());

        accessor.to("ch.b").set("100");
        accessor.to("ch.c").set("true");
        accessor.to("ch.ch").set(new Sample());

        accessor.to("ch.ch.b").set("100");
        accessor.to("ch.ch.c").set("true");

        /*
         * 型変換無し
         */
        assertThat(accessor.to("a").get(String.class), is("[test]"));
        assertThat(accessor.to("b").get(Integer.class), is(100));
        assertThat(accessor.to("b").get(String.class), is("100"));
        assertThat(accessor.to("string").get(String.class), is("test:100"));
        assertThat(accessor.to("c").get(Boolean.class), is(true));
        assertThat(accessor.to("c").get(String.class), is("true"));

        assertThat(accessor.to("ch.a").get(String.class), is("[test]"));
        assertThat(accessor.to("ch.b").get(Integer.class), is(100));
        assertThat(accessor.to("ch.c").get(Boolean.class), is(true));

        assertThat(accessor.to("ch.ch.a").get(String.class), is("[test]"));
        assertThat(accessor.to("ch.ch.b").get(Integer.class), is(100));
        assertThat(accessor.to("ch.ch.c").get(Boolean.class), is(true));

        for (final String e : accessor.to("list").iterable(String.class))
        {
            System.out.println(e);
        }

        for (final PojoAccessor<?> e : accessor.to("list")
                .accessorIterable(String.class))
        {
            System.out.println(e.to("string").get());
        }
    }

    final PojoAccessorRepository repository = PojoAccessor.Repository;
}
