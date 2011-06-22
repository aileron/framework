/**
 * 
 */
package cc.aileron.generic;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import cc.aileron.generic.error.NotFoundException;
import cc.aileron.generic.util.SkipList;

/**
 * 関数郡
 */
public abstract class $
{
    /**
     * @author aileron
     * @param <K>
     * @param <V>
     */
    public static abstract class Flod<K, V> implements
            ObjectProvider<ConsCell<K, V>, V>
    {
        /**
         * @return V
         */
        public V apply()
        {
            return $.flod(ite, this);
        }

        /**
         * @param ite
         * @return V
         */
        public V apply(final Iterable<K> ite)
        {
            return $.flod(ite, this);
        }

        /**
         */
        public Flod()
        {
            this(null);
        }

        /**
         * @param ite
         */
        public Flod(final Iterable<K> ite)
        {
            this.ite = ite;
        }

        private final Iterable<K> ite;
    }

    /**
     * index 付き 拡張for文ループ
     * 
     * @param <T>
     */
    public static interface IndexLoopContext<T>
    {
        /**
         * @return index
         */
        int index();

        /**
         * @return value
         */
        T value();
    }

    private static final class DictImpl<K, V> implements Dict<K, V>
    {
        @Override
        public Dict<K, V> add(final Dict<K, V> dict)
        {
            final DictImpl<K, V> newdict = new DictImpl<K, V>();
            for (final ConsCell<K, V> e : this)
            {
                newdict.put(e);
            }
            for (final ConsCell<K, V> e : dict)
            {
                newdict.put(e);
            }
            return newdict;
        }

        @Override
        public V get(final K key)
        {
            return map.get(key);
        }

        @Override
        public Iterator<ConsCell<K, V>> iterator()
        {
            return iterate(new ObjectReference<ConsCell<K, V>>()
            {
                @Override
                public ConsCell<K, V> get()
                {
                    if (!ite.hasNext())
                    {
                        return null;
                    }
                    final Entry<K, V> e = ite.next();
                    return cons(e.getKey(), e.getValue());
                }

                final Iterator<Entry<K, V>> ite = map.entrySet().iterator();
            });
        }

        @Override
        public Dict<K, V> put(final ConsCell<K, V> cell)
        {
            map.put(cell.car(), cell.cdr());
            return this;
        }

        @Override
        public Dict<K, V> put(final K key, final V value)
        {
            map.put(key, value);
            return this;
        }

        /**
         */
        public DictImpl()
        {
        }

        final HashMap<K, V> map = new HashMap<K, V>();
    }

    /**
     * @param <T>
     * @param a
     * @param b
     * @return add
     */
    public static <T> T[] add(final T[] a, final T[] b)
    {
        final Object dist = new Object[a.length + b.length];
        System.arraycopy(a, 0, dist, 0, a.length);
        System.arraycopy(b, 0, dist, a.length, b.length);
        return cast(dist);
    }

    /**
     * @param <T>
     * @param iterable
     * @param procedure
     * @throws Exception
     */
    public static <T> void apply(final Iterable<T> iterable,
            final Procedure<T> procedure) throws Exception
    {
        for (final T t : iterable)
        {
            procedure.call(t);
        }
    }

    /**
     * @param <T>
     * @param object
     * @return T
     */
    @SuppressWarnings("unchecked")
    public static <T> T cast(final Object object)
    {
        return (T) object;
    }

    /**
     * @param <Car>
     * @param <Cdr>
     * @param car
     * @param cdr
     * @return {@link ConsCell}
     */
    public static <Car, Cdr> ConsCell<Car, Cdr> cons(final Car car,
            final Cdr cdr)
    {
        return new ConsCell<Car, Cdr>()
        {
            @Override
            public Car car()
            {
                return car;
            }

            @Override
            public Cdr cdr()
            {
                return cdr;
            }
        };
    }

    /**
     * @param <K>
     * @param <V>
     * @return dict
     */
    public static <K, V> Dict<K, V> dict()
    {
        return new DictImpl<K, V>();
    }

    /**
     * @param <T>
     * @param iterable
     * @param procedure
     * @return {@link Exception}
     */
    public static <T> Exception each(final Iterable<T> iterable,
            final Procedure<T> procedure)
    {
        for (final T value : iterable)
        {
            try
            {
                procedure.call(value);
            }
            catch (final Exception e)
            {
                return e;
            }
        }
        return null;
    }

    /**
     * @param <T>
     * @param ite
     * @param procedure
     * @return {@link Exception}
     */
    public static <T> Exception each(final Iterator<T> ite,
            final Procedure<T> procedure)
    {
        while (ite.hasNext())
        {
            try
            {
                procedure.call(ite.next());
            }
            catch (final Exception e)
            {
                return e;
            }
        }
        return null;
    }

    /**
     * @param <R>
     * @param <P>
     * @param ite
     * @param function
     * @return R
     */
    public static <R, P> R flod(final Iterable<P> ite,
            final ObjectProvider<ConsCell<P, R>, R> function)
    {
        final ConsCell.Value<P, R> value = new ConsCell.Value<P, R>();
        for (final P p : ite)
        {
            value.car = p;
            value.cdr = function.get(value);
        }
        return value.cdr;
    }

    /**
     * iterable Object を index 付き iterable に変換する
     * 
     * @param <T>
     * @param value
     * @return {@link Iterable}
     */
    public static <T> Iterable<IndexLoopContext<T>> indexLoop(
            final Iterable<T> value)
    {
        return new Iterable<IndexLoopContext<T>>()
        {
            @Override
            public Iterator<IndexLoopContext<T>> iterator()
            {
                final Iterator<T> iterator = value.iterator();
                return new Iterator<IndexLoopContext<T>>()
                {
                    @Override
                    public boolean hasNext()
                    {
                        return iterator.hasNext();
                    }

                    @Override
                    public IndexLoopContext<T> next()
                    {
                        return new IndexLoopContext<T>()
                        {
                            @Override
                            public int index()
                            {
                                return index++;
                            }

                            @Override
                            public T value()
                            {
                                return iterator.next();
                            }
                        };
                    }

                    @Override
                    public void remove()
                    {
                        iterator.remove();
                    }

                    int index = 0;
                };
            }
        };
    }

    /**
     * iterator オブジェクトから Value オブジェクトを取得する
     * 
     * @param <E>
     * @param e
     * @return {@link ObjectReference}
     */
    public static <E> ObjectReference<E> iterate(final Iterator<E> e)
    {
        return new ObjectReference<E>()
        {
            @Override
            public E get()
            {
                if (!e.hasNext())
                {
                    return null;
                }
                return e.next();
            }
        };
    }

    /**
     * @param <E>
     * @param <T>
     * @param iterator
     * @param convertor
     * @return {@link Iterator}
     */
    public static <E, T> Iterator<T> iterate(final Iterator<E> iterator,
            final ObjectProvider<E, T> convertor)
    {
        return iterate(new ObjectReference<T>()
        {
            @Override
            public T get()
            {
                if (!iterator.hasNext())
                {
                    return null;
                }
                return convertor.get(iterator.next());
            }
        });
    }

    /**
     * iteratarオブジェクトの作成
     * 
     * @param <E>
     * @param value
     * @return {@link Iterator}
     */
    public static <E> Iterator<E> iterate(final ObjectReference<E> value)
    {
        return new Iterator<E>()
        {
            @Override
            public boolean hasNext()
            {
                return next != null;
            }

            @Override
            public E next()
            {
                final E result = next;
                next = value.get();
                return result;
            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException();
            }

            E next = value.get();
        };
    }

    /**
     * @param <T>
     * @param array
     * @return Iterable
     */
    public static <T> Iterable<T> iterate(final T[] array)
    {
        return Arrays.asList(array);
    }

    /**
     * @param key
     * @param ite
     * @return joined-strings
     */
    public static final String join(final String key, final Iterable<String> ite)
    {
        if (key == null || ite == null)
        {
            throw new IllegalArgumentException(String.format("key=[%s],ite=[%s]",
                    key,
                    ite));
        }
        return join(key, ite.iterator());
    }

    /**
     * @param key
     * @param ite
     * @return join strings
     */
    public static final String join(final String key, final Iterator<String> ite)
    {
        final StringBuilder builder = new StringBuilder();
        while (ite.hasNext())
        {
            final String next = ite.next();
            builder.append(next);
            if (ite.hasNext())
            {
                builder.append(key);
            }
        }
        return builder.toString();
    }

    /**
     * @param key
     * @param ite
     * @return join strings
     */
    public static final String join(final String key,
            final ObjectReference<String> ite)
    {
        return join(key, iterate(ite));
    }

    /**
     * @param key
     * @param values
     * @return joined strings
     */
    public static String join(final String key, final String[] values)
    {
        final StringBuilder builder = new StringBuilder().append(values[0]);
        for (int i = 1, size = values.length; i < size; i++)
        {
            builder.append(key);
            builder.append(values[i]);
        }
        return builder.toString();
    }

    /**
     * シーケンスに関数を適応する
     * 
     * @param <T>
     * @param <R>
     * @param ts
     * @param function
     * @return 適応済みリスト
     */
    public static <T, R> List<R> map(final Iterable<T> ts,
            final ObjectProvider<T, R> function)
    {
        final List<R> results = new SkipList<R>();
        for (final T t : ts)
        {
            final R result = function.get(t);
            if (result != null)
            {
                results.add(result);
            }
        }
        return results;
    }

    /**
     * @param <T>
     * @param function
     * @return {@link Procedure}
     */
    public static <T> Procedure<T> recur(final ObjectProvider<T, T> function)
    {
        return new Procedure<T>()
        {
            @Override
            public void call(final T object)
            {
                T context = object;
                while (context != null)
                {
                    context = function.get(context);
                }
            }
        };
    }

    /**
     * @param <T>
     * @param object
     * @return {@link ObjectReference}
     */
    public static <T> ObjectReference<T> ref(final T object)
    {
        return new ObjectReference<T>()
        {
            @Override
            public T get()
            {
                return object;
            }
        };
    }

    /**
     * @param <T>
     * @param t
     * @return t
     * @throws NotFoundException
     */
    public static <T extends Resource> T some(final T t)
            throws NotFoundException
    {
        if (t.isNotFound())
        {
            throw new NotFoundException(t.path());
        }
        return t;
    }
}
