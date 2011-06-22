/**
 * 
 */
package cc.aileron.peg.test;

import static cc.aileron.peg.Peg.*;

import org.junit.Test;

import cc.aileron.generic.ObjectProvider;
import cc.aileron.generic.util.ReflectionToString;
import cc.aileron.peg.PegArray;
import cc.aileron.peg.PegContext;
import cc.aileron.peg.PegParser;
import cc.aileron.peg.PegValue;

/**
 * @author aileron
 */
public class PegTest
{
    /**
     * 
     */
    @Test
    public void date()
    {
        final PegParser years_date = seq(join(digit, digit, digit, digit),
                drop(chars("-")),
                join(digit, digit),
                drop(chars("-")),
                join(digit, digit));

        dump(years_date, "2010-01-01");
    }

    /**
     * 
     */
    @Test
    public void nc()
    {

        final PegParser space = drop(many(chars("\r\n\t ")));
        final PegParser symbol = join(many1(nchars("()\r\n\t ")));

        final PegParser expr = many(seq(space, symbol));

        dump(expr, "    test()    spec()");
    }

    /**
     * 逆ポーランド記法
     */
    @Test
    public void rpn()
    {
        final PegParser parser = new PegParser()
        {
            public PegParser op(final char ope)
            {
                return first(chars(ope), space);
            }

            public PegParser op(final String ope)
            {
                return first(chars(ope), space);
            }

            @Override
            public PegValue<?> parse(final PegContext context)
            {
                return exp.parse(context);
            }

            PegParser listof(final PegParser item, final PegParser glue)
            {
                return hook(fn, seq(item, many(glue, item)));
            }

            PegParser atom;
            PegParser exp;
            PegParser exp1;
            PegParser exp2;
            PegParser exp3;
            ObjectProvider<PegValue<?>, PegValue<?>> fn = new ObjectProvider<PegValue<?>, PegValue<?>>()
            {
                @Override
                public PegValue<?> get(final PegValue<?> v)
                {
                    if (v == null || v.isArray() == false)
                    {
                        return null;
                    }
                    final PegArray list = (PegArray) v;
                    final PegArray first = new PegArray();
                    first.add(list.get(0));
                    if (list.size() == 1)
                    {
                        return first;
                    }
                    final PegArray result = new PegArray();
                    result.add(first);

                    final PegArray array = (PegArray) list.get(1);
                    for (final PegValue<?> p : array)
                    {
                        final PegArray a = (PegArray) p;
                        for (int i = 0, size = a.size(); i < size; i++)
                        {
                            final PegValue<?> vv = a.get(i);
                            if (result.size() <= i)
                            {
                                result.add(vv);
                            }
                            else
                            {
                                result.set(i, vv);
                            }
                        }
                    }
                    return result;
                }
            };
            PegParser notzero;
            PegParser num;
            PegParser paren;
            PegParser space;
            PegParser strnum;
            ObjectProvider<PegValue<?>, PegValue<?>> torpn = new ObjectProvider<PegValue<?>, PegValue<?>>()
            {
                @Override
                public PegValue<?> get(final PegValue<?> value)
                {
                    if (value.isArray() == false)
                    {
                        return null;
                    }
                    final PegArray array = (PegArray) value;
                    final PegValue<?> first = array.get(0);
                    final PegArray result = new PegArray();
                    result.add(first.isArray() ? get(first) : first);
                    for (int i = 1, size = array.size(); i < size; i += 2)
                    {
                        final PegValue<?> op = array.get(i);
                        if (size > i + 1)
                        {
                            final PegValue<?> right = array.get(i + 1);
                            result.add(right.isArray() ? get(right) : right);
                        }
                        result.add(op);
                    }
                    return result;
                }
            };
            PegParser zero;

            {
                space = many(chars("\r\n \t"));
                zero = seq(chars("0"));
                notzero = seq(chars("123456789"), many(digit()));
                strnum = join(choice(notzero, zero));
                num = first(strnum, space);
                paren = second(seq(chars("("), space),
                        exp1,
                        seq(chars(")"), space));
                atom = choice(num, paren);
                exp3 = listof(atom, op('/'));
                exp2 = listof(exp3, op('*'));
                exp1 = listof(exp2, op("+-"));
                exp = second(space, hook(torpn, exp1), eos());
            }
        };
        dump(parser, "1 + 2 * 3");
    }

    /**
     */
    @Test
    public void sexpr()
    {
        final PegParser expr = new PegParser()
        {
            @Override
            public PegValue<?> parse(final PegContext context)
            {
                return sexpr.parse(context);
            }

            PegParser atom;
            PegParser paren;
            PegParser sexpr;
            PegParser space;
            PegParser symbol;
            {
                space = many(chars("\r\n\t "));
                symbol = join(many1(nchars("()\r\n\t ")));
                paren = third(chars("("), space, many(atom), chars(")"));
                atom = first(choice(paren, symbol), space);
                sexpr = second(seq(space, atom));
            }
        };

        final String context = "(a (b (c ())))";

        System.out.println(parse(expr, context));

        dump(expr, "(a (b (c ())))");
    }

    /**
     */
    @Test
    public void spec()
    {
        final String context = "ababab";

        final PegParser parser = many(drop(chars("a")), chars("b"));
        final PegValue<?> v = parse(parser, context);

        System.out.println(ReflectionToString.toString(v));
    }

}
