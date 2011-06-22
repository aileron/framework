/**
 * 
 */
package cc.aileron.template.compiler;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import cc.aileron.generic.ObjectProvider;
import cc.aileron.generic.Resource;
import cc.aileron.pojo.PojoAccessor;
import cc.aileron.pojo.PojoAccessorRepository;
import cc.aileron.template.Template;
import cc.aileron.template.TemplateRepository;
import cc.aileron.template.impl.TemplateInstructionRepositoryDefault;
import cc.aileron.template.impl.TemplateRepositoryImpl;

/**
 * @author aileron
 */
public class TemplateRepositoryTest
{
    /**
     * @author Aileron
     */
    public static enum Category
    {
        /**
         * A
         */
        A

        /**
         * B
         */
        , B

        /**
         * C
         */
        , C

        /**
         * あいう
         */
        , あいう;

        /**
         * @return type
         */
        public String type()
        {
            return "test";
        }
    }

    /**
     * @author Aileron
     */
    public class Sample
    {
        public int a = 10;

        /**
         * aaa
         */
        public String aaa = "bbb";

        public Integer b = 10;

        public float c = 30.5f;

        /**
         * category
         */
        public Category category = Category.A;

        public Float d = 30.5f;

        /**
         * emap
         */
        public EnumMap<Category, Boolean> emap = new EnumMap<Category, Boolean>(Category.class);

        /**
         * is
         */
        public boolean is = false;
        /**
         * list
         */
        public List<String> list = new ArrayList<String>();

        /**
         * nulllist
         */
        public List<String> nulllist;
        /**
         * samples
         */
        public List<SampleOne> samples = new ArrayList<SampleOne>();
        /**
         * select-map
         */
        public ObjectProvider<Category, Map<Category, Boolean>> selectMap = new ObjectProvider<Category, Map<Category, Boolean>>()
        {
            @Override
            public Map<Category, Boolean> get(final Category p)
            {
                final Map<Category, Boolean> map = new HashMap<TemplateRepositoryTest.Category, Boolean>();
                for (final Category c : Category.values())
                {
                    map.put(c, false);
                }
                map.put(p, true);
                return map;
            }
        };

        /**
         * test
         */
        public String test = "sample";

        {
            emap.put(Category.A, true);
            emap.put(Category.B, false);
            emap.put(Category.あいう, true);
        }
    }

    /**
     * @author aileron
     */
    public static interface SampleOne
    {
        /**
         * @return ch
         */
        List<SampleOne> ch();

        /**
         * @return val
         */
        String val();

        /**
         * category
         */
        public final Category category = Category.A;

        /**
         * test
         */
        public final ObjectProvider<SampleOne, String> test = new ObjectProvider<SampleOne, String>()
        {
            @Override
            public String get(final SampleOne p)
            {
                return "hoge";
            }
        };
    }

    /**
     * spec
     * 
     * @throws IOException
     */
    @Test
    public void spec() throws IOException
    {
        final ReadableByteChannel channel = Channels.newChannel(TokenizerTest.class.getClassLoader()
                .getResourceAsStream("test.html"));

        final byte[] buff = new byte[65536];
        channel.read(ByteBuffer.wrap(buff));
        final String content = new String(buff).trim();

        final StringWriter stringWriter = new StringWriter();
        final PrintWriter writer = new PrintWriter(stringWriter);

        final PojoAccessor<Sample> accessor = accessorRepository.from(sample);

        final long t = System.nanoTime();
        final Template template = templateRepository.get(new Resource.Str("test.html",
                content));
        template.print(accessor, writer);
        System.out.println("-------------time----------"
                + (System.nanoTime() - t));

        System.out.println(stringWriter.toString());
    }

    /**
     * @return sample
     */
    private Sample init()
    {
        final Sample sample = new Sample();
        sample.list.add("test1");
        sample.list.add("test2");
        sample.list.add("test3");
        sample.list.add("test4");

        for (int i = 0; i < 10; i++)
        {
            final int idx1 = i;
            sample.samples.add(new SampleOne()
            {
                @Override
                public List<SampleOne> ch()
                {
                    return ch;
                }

                @Override
                public String val()
                {
                    return "aaa" + idx1;
                }

                final List<SampleOne> ch = new ArrayList<SampleOne>();
                {
                    final SampleOne p = this;
                    for (int i = 0; i < 10; i++)
                    {
                        final int idx = i;
                        ch.add(new SampleOne()
                        {
                            @Override
                            public List<SampleOne> ch()
                            {
                                return null;
                            }

                            @Override
                            public String val()
                            {
                                return p.val() + idx;
                            }
                        });
                    }
                }
            });
        }

        return sample;
    }

    PojoAccessorRepository accessorRepository = PojoAccessor.Repository;

    Sample sample = init();

    TemplateRepository templateRepository = new TemplateRepositoryImpl(new Symbol()
    {
        @Override
        public String end()
        {
            return "-->";
        }

        @Override
        public String start()
        {
            return "<!--";
        }
    },
            new TemplateInstructionRepositoryDefault());
}
