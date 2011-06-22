/**
 * 
 */
package cc.aileron.generic.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * 簡易 xml parser
 */
public class XmlParser
{
    /**
     * @author aileron
     */
    public static interface EventHandler
    {
        /**
         * @param tag
         */
        void close(String tag);

        /**
         * @param tag
         */
        void comment(String tag);

        /**
         * @param tag
         */
        void content(String tag);

        /**
         * @param tag
         */
        void open(String tag);
    }

    /**
     * @param stream
     * @param handler
     * @throws IOException
     */
    public static void parse(final ReadableByteChannel stream,
            final EventHandler handler) throws IOException
    {
        final ByteBuffer buffer = ByteBuffer.allocateDirect(4096);
        final CharsetDecoder decoder = Charset.defaultCharset().newDecoder();
        for (int len; (len = stream.read(buffer)) > 0;)
        {
            buffer.flip();
            final CharBuffer charBuffer = decoder.decode(buffer);
            int tagstart = 0, tagend = 0;
            for (int i = 0; i < len; i++)
            {
                final char token = charBuffer.get(i);
                // System.out.println("[" + token + "]");
                if ('<' == token)
                {
                    if (tagstart > 0)
                    {
                        final String content = new StringBuilder(charBuffer.subSequence(tagend,
                                i)).toString();
                        handler.content(content);
                    }
                    tagstart = i;
                    continue;
                }
                if ('>' == token)
                {
                    final String tag = new StringBuilder(charBuffer.subSequence(tagstart,
                            i + 1)).toString();
                    // System.out.println("----------" + tag + "----------");
                    tagend = i + 1;

                    switch (tag.charAt(1))
                    {
                    case '!':
                        handler.comment(tag);
                        break;
                    case '/':
                        handler.close(tag);
                        break;
                    default:
                        handler.open(tag);
                        break;
                    }

                    continue;
                }
            }
        }
    }
}
