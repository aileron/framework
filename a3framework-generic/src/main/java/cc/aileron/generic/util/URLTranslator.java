/**
 * 
 */
package cc.aileron.generic.util;

import java.io.CharArrayWriter;
import java.nio.charset.Charset;
import java.util.BitSet;

/**
 * @author aileron
 */
public interface URLTranslator
{
    /**
     * @author aileron
     */
    interface Factory
    {
        URLTranslator get(Charset charset);
    }

    /**
     * @param source
     * @return dist
     */
    String decode(String source);

    /**
     * @param source
     * @return dist
     */
    String encode(String source);

    /**
     * factory
     */
    final Factory factory = new Factory()
    {
        @Override
        public URLTranslator get(final Charset charset)
        {
            return new URLTranslator()
            {
                @Override
                public String decode(final String s)
                {
                    boolean needToChange = false;
                    final int numChars = s.length();
                    final StringBuffer sb = new StringBuffer(numChars > 500 ? numChars / 2
                            : numChars);
                    int i = 0;

                    char c;
                    byte[] bytes = null;
                    while (i < numChars)
                    {
                        c = s.charAt(i);
                        switch (c)
                        {
                        case '+':
                            sb.append(' ');
                            i++;
                            needToChange = true;
                            break;
                        case '%':
                            /*
                             * Starting with this instance of %, process all
                             * consecutive substrings of the form %xy. Each
                             * substring %xy will yield a byte. Convert all
                             * consecutive bytes obtained this way to whatever
                             * character(s) they represent in the provided
                             * encoding.
                             */

                            try
                            {

                                // (numChars-i)/3 is an upper bound for the
                                // number
                                // of remaining bytes
                                if (bytes == null)
                                {
                                    bytes = new byte[(numChars - i) / 3];
                                }
                                int pos = 0;

                                while (((i + 2) < numChars) && (c == '%'))
                                {
                                    bytes[pos++] = (byte) Integer.parseInt(s.substring(i + 1,
                                            i + 3),
                                            16);
                                    i += 3;
                                    if (i < numChars)
                                    {
                                        c = s.charAt(i);
                                    }
                                }

                                // A trailing, incomplete byte encoding such as
                                // "%x" will cause an exception to be thrown

                                if ((i < numChars) && (c == '%'))
                                {
                                    throw new IllegalArgumentException("URLDecoder: Incomplete trailing escape (%) pattern");
                                }

                                sb.append(new String(bytes, 0, pos, charset));
                            }
                            catch (final NumberFormatException e)
                            {
                                throw new IllegalArgumentException("URLDecoder: Illegal hex characters in escape (%) pattern - "
                                        + e.getMessage());
                            }
                            needToChange = true;
                            break;
                        default:
                            sb.append(c);
                            i++;
                            break;
                        }
                    }

                    return (needToChange ? sb.toString() : s);
                }

                @Override
                public String encode(final String s)
                {
                    boolean needToChange = false;
                    final StringBuffer out = new StringBuffer(s.length());

                    final CharArrayWriter charArrayWriter = new CharArrayWriter();
                    for (int i = 0; i < s.length();)
                    {
                        int c = s.charAt(i);
                        // System.out.println("Examining character: " + c);
                        if (dontNeedEncoding.get(c))
                        {
                            if (c == ' ')
                            {
                                c = '+';
                                needToChange = true;
                            }
                            // System.out.println("Storing: " + c);
                            out.append((char) c);
                            i++;
                        }
                        else
                        {
                            // convert to external encoding before hex
                            // conversion
                            do
                            {
                                charArrayWriter.write(c);
                                /*
                                 * If this character represents the start of a
                                 * Unicode surrogate pair, then pass in two
                                 * characters. It's not clear what should be
                                 * done if a bytes reserved in the surrogate
                                 * pairs range occurs outside of a legal
                                 * surrogate pair. For now, just treat it as if
                                 * it were any other character.
                                 */
                                if (c >= 0xD800 && c <= 0xDBFF)
                                {
                                    /*
                                     * System.out.println(Integer.toHexString(c)
                                     * + " is high surrogate");
                                     */
                                    if ((i + 1) < s.length())
                                    {
                                        final int d = s.charAt(i + 1);
                                        /*
                                         * System.out.println("\tExamining " +
                                         * Integer.toHexString(d));
                                         */
                                        if (d >= 0xDC00 && d <= 0xDFFF)
                                        {
                                            /*
                                             * System.out.println("\t" +
                                             * Integer.toHexString(d) +
                                             * " is low surrogate");
                                             */
                                            charArrayWriter.write(d);
                                            i++;
                                        }
                                    }
                                }
                                i++;
                            } while (i < s.length()
                                    && !dontNeedEncoding.get((c = s.charAt(i))));

                            charArrayWriter.flush();
                            final String str = new String(charArrayWriter.toCharArray());
                            final byte[] ba = str.getBytes(charset);
                            for (int j = 0; j < ba.length; j++)
                            {
                                out.append('%');
                                char ch = Character.forDigit((ba[j] >> 4) & 0xF,
                                        16);
                                // converting to use uppercase letter as part of
                                // the hex value if ch is a letter.
                                if (Character.isLetter(ch))
                                {
                                    ch -= caseDiff;
                                }
                                out.append(ch);
                                ch = Character.forDigit(ba[j] & 0xF, 16);
                                if (Character.isLetter(ch))
                                {
                                    ch -= caseDiff;
                                }
                                out.append(ch);
                            }
                            charArrayWriter.reset();
                            needToChange = true;
                        }
                    }

                    return (needToChange ? out.toString() : s);
                }

            };
        }

        final int caseDiff = ('a' - 'A');
        final BitSet dontNeedEncoding;
        {
            /*
             * The list of characters that are not encoded has been determined
             * as follows:
             * 
             * RFC 2396 states: ----- Data characters that are allowed in a URI
             * but do not have a reserved purpose are called unreserved. These
             * include upper and lower case letters, decimal digits, and a
             * limited set of punctuation marks and symbols.
             * 
             * unreserved = alphanum | mark
             * 
             * mark = "-" | "_" | "." | "!" | "~" | "*" | "'" | "(" | ")"
             * 
             * Unreserved characters can be escaped without changing the
             * semantics of the URI, but this should not be done unless the URI
             * is being used in a context that does not allow the unescaped
             * character to appear. -----
             * 
             * It appears that both Netscape and Internet Explorer escape all
             * special characters from this list with the exception of "-", "_",
             * ".", "*". While it is not clear why they are escaping the other
             * characters, perhaps it is safest to assume that there might be
             * contexts in which the others are unsafe if not escaped.
             * Therefore, we will use the same list. It is also noteworthy that
             * this is consistent with O'Reilly's "HTML: The Definitive Guide"
             * (page 164).
             * 
             * As a last note, Intenet Explorer does not encode the "@"
             * character which is clearly not unreserved according to the RFC.
             * We are being consistent with the RFC in this matter, as is
             * Netscape.
             */

            dontNeedEncoding = new BitSet(256);
            int i;
            for (i = 'a'; i <= 'z'; i++)
            {
                dontNeedEncoding.set(i);
            }
            for (i = 'A'; i <= 'Z'; i++)
            {
                dontNeedEncoding.set(i);
            }
            for (i = '0'; i <= '9'; i++)
            {
                dontNeedEncoding.set(i);
            }
            dontNeedEncoding.set(' '); /*
                                        * encoding a space to a + is done in the
                                        * encode() method
                                        */
            dontNeedEncoding.set('-');
            dontNeedEncoding.set('_');
            dontNeedEncoding.set('.');
            dontNeedEncoding.set('*');
        }
    };
}
