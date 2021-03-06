/**
 * 
 */
package cc.aileron.peg;

import cc.aileron.generic.$;
import cc.aileron.generic.ObjectProvider;
import cc.aileron.generic.Procedure;

/**
 * Peg
 */
public abstract class Peg implements PegParser
{
    /**
     * digit
     */
    public static final PegParser digit = chars("0123456789");

    /**
     * BLANK
     */
    static final PegValue<Object> BLANK = new PegValue<Object>()
    {
        @Override
        public Object get()
        {
            return "";
        }

        @Override
        public boolean isArray()
        {
            return false;
        }

        @Override
        public boolean isBlank()
        {
            return true;
        }

        @Override
        public String toString()
        {
            return "Value[BLNAK]";
        }
    };

    /**
     * @param ch
     * @return chars
     */
    public static PegParser chars(final char ch)
    {
        return new Peg()
        {
            @Override
            public PegValue<?> parse(final PegContext context)
            {
                if (!context.hasNext())
                {
                    return null;
                }
                if (context.get() == ch)
                {
                    return new PegStr(context.next());
                }
                return null;
            }
        };
    }

    /**
     * @param chars
     * @return {@link PegParser}
     */
    public static PegParser chars(final String chars)
    {
        return new Peg()
        {
            @Override
            public PegValue<?> parse(final PegContext context)
            {
                if (!context.hasNext())
                {
                    throw new Error("is eos");
                }
                final char token = context.get();
                if (chars.indexOf(token) != -1)
                {
                    return new PegStr(context.next());
                }
                return null;
            }

            @Override
            public String toString()
            {
                return toString;
            }

            final String toString = "Peg.Parser[chars:" + chars + "]";
        };
    }

    /**
     * @param parsers
     * @return {@link PegParser}
     */
    public static PegParser choice(final PegParser... parsers)
    {
        return new Peg()
        {
            @Override
            public PegValue<?> parse(final PegContext context)
            {
                for (final PegParser parser : parsers)
                {
                    final PegValue<?> result = parser.parse(context);
                    if (result != null)
                    {
                        return result;
                    }
                }
                return null;
            }

            @Override
            public String toString()
            {
                return toString;
            }

            final String toString = "Peg.Parser[choice:" + parsers + "]";
        };
    }

    /**
     * @return {@link PegParser}
     */
    public static PegParser digit()
    {
        return digit;
    }

    /**
     * @param parser
     * @return {@link PegParser}
     */
    public static PegParser drop(final PegParser parser)
    {
        return new Peg()
        {
            @Override
            public PegValue<?> parse(final PegContext context)
            {
                final PegValue<?> result = parser.parse(context);
                if (result == null)
                {
                    return null;
                }
                return BLANK;
            }

            @Override
            public String toString()
            {
                return toString;
            }

            final String toString = "Peg.Parser[drop:" + parser + "]";
        };
    }

    /**
     * @param parser
     * @param context
     */
    public static void dump(final PegParser parser, final String context)
    {
        final PegContext _context = context(context);
        final PegValue<?> object = parser.parse(_context);

        final Procedure<PegValue<?>> proc = new Procedure<PegValue<?>>()
        {
            @Override
            public void call(final PegValue<?> value)
            {
                if (value.isBlank())
                {
                    return;
                }
                if (value.isArray() == false)
                {
                    System.out.print(value.get());
                    return;
                }
                System.out.print("[");
                for (final PegValue<?> v : (PegArray) value)
                {
                    call(v);
                    System.out.print(",");
                }
                System.out.print("]");
                return;
            }
        };
        try
        {
            proc.call(object);
        }
        catch (final Exception e)
        {
            throw new Error(e);
        }
    }

    /**
     * @return {@link PegParser}
     */
    public static PegParser eos()
    {
        return new PegParser()
        {
            @Override
            public PegValue<?> parse(final PegContext context)
            {
                return context.hasNext() == false ? BLANK : null;
            }
        };
    }

    /**
     * @param parsers
     * @return {@link PegParser}
     */
    public static PegParser first(final PegParser... parsers)
    {
        return x(0, parsers);
    }

    /**
     * @param function
     * @param parser
     * @return {@link PegParser}
     */
    public static PegParser hook(
            final ObjectProvider<PegValue<?>, PegValue<?>> function,
            final PegParser parser)
    {
        return new PegParser()
        {
            @Override
            public PegValue<?> parse(final PegContext context)
            {
                final PegValue<?> v = parser.parse(context);
                if (v == null)
                {
                    return null;
                }
                return function.get(v);
            }
        };
    }

    /**
     * @param parsers
     * @return {@link PegParser}
     */
    public static PegParser join(final PegParser... parsers)
    {
        return join("", parsers);
    }

    /**
     * @param glue
     * @param parsers
     * @return {@link PegParser}
     */
    public static PegParser join(final String glue, final PegParser... parsers)
    {
        final PegParser parser = seq(parsers);
        return new Peg()
        {
            @Override
            public PegValue<?> parse(final PegContext context)
            {
                final PegValue<?> value = parser.parse(context);
                if (value == null || value.isArray() == false)
                {
                    return null;
                }
                final PegArray list = (PegArray) value;
                final String result = $.join(glue,
                        $.iterate(list.iterator(),
                                new ObjectProvider<PegValue<?>, String>()
                                {
                                    @Override
                                    public String get(final PegValue<?> value)
                                    {
                                        if (value.isArray())
                                        {
                                            final StringBuilder builder = new StringBuilder();
                                            for (final PegValue<?> v : (PegArray) value)
                                            {
                                                builder.append(get(v));
                                            }
                                            return builder.toString();
                                        }
                                        return ((PegStr) value).get();
                                    }
                                }));
                return new PegStr(result);
            }

            @Override
            public String toString()
            {
                return toString;
            }

            final String toString = String.format("Peg.Parser[join:glue[%s],%s]",
                    glue,
                    parsers);
        };
    }

    /**
     * @param parsers
     * @return {@link PegParser}
     */
    public static PegParser many(final PegParser... parsers)
    {
        final PegParser parser = seq(parsers);
        return new Peg()
        {
            @Override
            public PegValue<?> parse(final PegContext context)
            {
                final PegArray result = new PegArray();
                while (context.hasNext())
                {
                    final PegValue<?> value = parser.parse(context);
                    if (value == null)
                    {
                        break;
                    }
                    result.add(value);
                }
                return result;
            }

            @Override
            public String toString()
            {
                return toString;
            }

            final String toString = String.format("Peg.Parser[many:%s]", parser);
        };
    }

    /**
     * @param parsers
     * @return {@link PegParser}
     */
    public static PegParser many1(final PegParser... parsers)
    {
        final PegParser parser = seq(parsers);
        return new Peg()
        {
            @Override
            public PegValue<?> parse(final PegContext context)
            {
                final PegArray result = new PegArray();
                while (context.hasNext())
                {
                    final PegValue<?> value = parser.parse(context);
                    if (value == null)
                    {
                        break;
                    }
                    result.add(value);
                }
                if (result.isEmpty())
                {
                    return null;
                }
                return result;
            }

            @Override
            public String toString()
            {
                return toString;
            }

            final String toString = String.format("Peg.Parser[many1:%s]",
                    parser);
        };
    }

    /**
     * @param ch
     * @return chars
     */
    public static PegParser nchars(final char ch)
    {
        return new Peg()
        {
            @Override
            public PegValue<?> parse(final PegContext context)
            {
                if (!context.hasNext())
                {
                    return null;
                }
                if (context.get() != ch)
                {
                    return new PegStr(context.next());
                }
                return null;
            }
        };
    }

    /**
     * @param chars
     * @return {@link PegParser}
     */
    public static PegParser nchars(final String chars)
    {
        return new Peg()
        {
            @Override
            public PegValue<?> parse(final PegContext context)
            {
                final char token = context.get();
                if (chars.indexOf(token) == -1)
                {
                    return new PegStr(context.next());
                }
                return null;
            }

            @Override
            public String toString()
            {
                return toString;
            }

            final String toString = String.format("Peg.Parser[nchars:%s]",
                    chars);
        };
    }

    /**
     * @param parser
     * @param context
     * @return {@link PegValue}
     */
    public static PegValue<?> parse(final PegParser parser, final String context)
    {
        final PegContext _context = context(context);
        return parser.parse(_context);
    }

    /**
     * @param parsers
     * @return {@link PegParser}
     */
    public static PegParser second(final PegParser... parsers)
    {
        return x(1, parsers);
    }

    /**
     * @param parsers
     * @return {@link PegParser}
     */
    public static PegParser seq(final PegParser... parsers)
    {
        if (parsers.length == 1)
        {
            return parsers[0];
        }
        return new Peg()
        {
            @Override
            public PegValue<?> parse(final PegContext context)
            {
                final PegArray result = new PegArray();
                for (final PegParser parser : parsers)
                {
                    final PegValue<?> value = parser.parse(context);
                    if (value == null)
                    {
                        return null;
                    }
                    result.add(value);
                }
                return result;
            }

            @Override
            public String toString()
            {
                return toString;
            }

            final String toString = String.format("Peg.Parser[seq:%s]",
                    parsers.toString());
        };
    }

    /**
     * @param parsers
     * @return {@link PegParser}
     */
    public static PegParser third(final PegParser... parsers)
    {
        return x(2, parsers);
    }

    /**
     * @param context
     * @return {@link PegContext}
     */
    private static PegContext context(final String context)
    {
        final char[] string = context.toCharArray();
        final int length = string.length;
        return new PegContext()
        {
            @Override
            public char get()
            {
                return string[idx];
            }

            @Override
            public boolean hasNext()
            {
                return length > idx;
            }

            @Override
            public char next()
            {
                return string[idx++];
            }

            @Override
            public int offset()
            {
                return idx;
            }

            @Override
            public char[] string()
            {
                return string;
            }

            int idx = 0;
        };
    }

    /**
     * @param parsers
     * @param x
     * @return {@link PegParser}
     */
    private static PegParser x(final int x, final PegParser... parsers)
    {
        final PegParser parser = seq(parsers);
        return new Peg()
        {
            @Override
            public PegValue<?> parse(final PegContext context)
            {
                final PegValue<?> result = parser.parse(context);
                if (result == null || result.isArray() == false)
                {
                    return null;
                }
                return ((PegArray) result).get(x);

            }

            @Override
            public String toString()
            {
                return toString;
            }

            final String toString = String.format("Peg.Parser[x%d:%s]",
                    x,
                    parser);
        };
    }
}