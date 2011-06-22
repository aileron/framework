package cc.aileron.template.compiler;

import java.io.IOException;

import org.junit.Test;

import cc.aileron.generic.Resource;
import cc.aileron.template.impl.TokenizerImpl;

/**
 * 
 */

/**
 * @author aileron
 */
public class TokenizerTest
{
    /**
     * @throws IOException
     */
    @Test
    public void spec() throws IOException
    {
        for (final Token token : tokenizer.get(Resource.Loader.get("test.html")
                .toString()))
        {
            System.out.println(token.category() + " " + token.name() + ":"
                    + token.attribute().replace('\n', ' '));
        }
    }

    /**
     * @throws IOException
     */
    @Test
    public void spec2() throws IOException
    {
        for (final Token token : tokenizer.get(Resource.Loader.get("test2.html")
                .toString()))
        {
            System.out.println(token.category() + " " + token.name() + ":"
                    + token.attribute().replace('\n', ' '));
        }
    }

    Tokenizer tokenizer = new TokenizerImpl(new Symbol()
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
    });
}
