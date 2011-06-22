/**
 * 
 */
package cc.aileron.template.impl;

import java.util.Iterator;

import cc.aileron.generic.$;
import cc.aileron.generic.ObjectReference;
import cc.aileron.template.compiler.Symbol;
import cc.aileron.template.compiler.Token;
import cc.aileron.template.compiler.Token.Category;
import cc.aileron.template.compiler.Tokenizer;

/**
 * @author aileron
 */
public class TokenizerImpl implements Tokenizer
{
    /**
     * @author aileron
     */
    enum State
    {
        OPEN_END
        {
            @Override
            public String get(final Symbol symbol)
            {
                return symbol.end();
            }
        },
        OPEN_START
        {
            @Override
            public String get(final Symbol symbol)
            {
                return symbol.start();
            }
        };

        public abstract String get(Symbol symbol);
    }

    @Override
    public Iterable<Token> get(final String string)
    {
        return new Iterable<Token>()
        {
            @Override
            public Iterator<Token> iterator()
            {
                return $.iterate(new ObjectReference<Token>()
                {
                    @Override
                    public Token get()
                    {
                        if (isEnd)
                        {
                            return null;
                        }
                        return next();
                    }

                    Token next()
                    {
                        final State current = next;
                        final int start = idx;
                        final String tag = current.get(symbol);
                        final int len = tag.length();
                        int end = string.indexOf(current.get(symbol), idx);
                        if (end == -1)
                        {
                            end = string.length();
                            isEnd = true;
                        }
                        idx = end + len;

                        final Category category;
                        final String content = string.substring(start, end);
                        String name = "", attr = content;

                        switch (current)
                        {

                        /*
                         * 開始タグを見付けた次にやる事
                         * 
                         * 開始タグの、終端を見つける
                         */
                        case OPEN_START:
                            category = Category.CONTENT;
                            next = State.OPEN_END;
                            break;

                        /*
                         * 開始タグの終端を見付けた次にやる事
                         * 
                         * 終了タグを見付ける
                         */
                        case OPEN_END:

                            Category c = Category.TAG;
                            if (content.isEmpty()) // CLOSE_TAG
                            {
                                c = Category.CLOSE_TAG;
                            }
                            else
                            {
                                // SHORT TAG
                                c = content.charAt(content.length() - 1) == '#' ? Category.STAG
                                        : c;

                                final int p = content.indexOf(' ');
                                final int l = content.lastIndexOf('}');
                                if (p == -1)
                                {
                                    name = content.substring(1, l);
                                }
                                else
                                {
                                    name = content.substring(1, p);
                                    attr = content.substring(p + 1, l);
                                }
                            }
                            category = c;
                            next = State.OPEN_START;
                            break;

                        default:
                            throw new Error("バグ");
                        }

                        final String attribute = attr;
                        final String tokenName = name;
                        return new Token()
                        {
                            @Override
                            public String attribute()
                            {
                                return attribute;
                            }

                            @Override
                            public Category category()
                            {
                                return category;
                            }

                            @Override
                            public String name()
                            {
                                return tokenName;
                            }

                        };
                    }

                    int idx = 0;
                    boolean isEnd = false;
                    State next = State.OPEN_START;
                });
            }
        };
    }

    /**
     * @param symbol
     */
    public TokenizerImpl(final Symbol symbol)
    {
        this.symbol = new Symbol()
        {
            @Override
            public String end()
            {
                return end;
            }

            @Override
            public String start()
            {
                return start;
            }

            final String end = symbol.end();
            final String start = symbol.start() + "#";
        };
    }

    final Symbol symbol;
}
