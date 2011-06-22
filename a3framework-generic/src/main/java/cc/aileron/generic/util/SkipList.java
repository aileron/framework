package cc.aileron.generic.util;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * http://d.hatena.ne.jp/kaiseh/20071231/1199122020
 * 
 * @author Kaisei Hamamoto
 * @param <E>
 */
public class SkipList<E> extends AbstractList<E> implements Cloneable,
        Serializable
{
    protected class BackwardEntryIterator implements Iterator<E>
    {
        @Override
        public boolean hasNext()
        {
            return current.prev != head;
        }

        @Override
        public E next()
        {
            if (!hasNext())
            {
                throw new NoSuchElementException();
            }
            current = current.prev;
            return current.value;
        }

        @Override
        public void remove()
        {
            if (expectedSize != size)
            {
                throw new ConcurrentModificationException();
            }
            removeEntry(current);
            expectedSize = size;
        }

        public BackwardEntryIterator(final Entry<E> current)
        {
            this.current = current;
            expectedSize = size;
        }

        private Entry<E> current;

        private int expectedSize;
    }

    protected static class Entry<T> implements Serializable
    {
        private static final long serialVersionUID = 6623755413831454813L;

        public int level()
        {
            return pts != null ? pts.length : 0;
        }

        public Entry()
        {
        }

        @SuppressWarnings("unchecked")
        public Entry(final int level, final Entry<T> prev, final Entry<T> next,
                final T value)
        {
            if (level > 0)
            {
                this.pts = new Pointer[level];
            }
            this.prev = prev;
            this.next = next;
            this.value = value;
        }

        public Entry<T> next;

        public Entry<T> prev;

        public Pointer<T>[] pts;

        public T value;
    }

    protected class EntryIterator implements Iterator<E>
    {
        @Override
        public boolean hasNext()
        {
            return current.next != head;
        }

        @Override
        public E next()
        {
            if (!hasNext())
            {
                throw new NoSuchElementException();
            }
            current = current.next;
            return current.value;
        }

        @Override
        public void remove()
        {
            if (expectedSize != size)
            {
                throw new ConcurrentModificationException();
            }
            removeEntry(current);
            expectedSize = size;
        }

        public EntryIterator(final Entry<E> current)
        {
            this.current = current;
            expectedSize = size;
        }

        private Entry<E> current;

        private int expectedSize;
    }

    protected static class Pointer<T> implements Serializable
    {
        private static final long serialVersionUID = -5260753036548236032L;

        public Pointer(final Entry<T> prev, final Entry<T> next,
                final int distance)
        {
            this.prev = prev;
            this.next = next;
            this.distance = distance;
        }

        public int distance;
        public Entry<T> next;

        public Entry<T> prev;
    }

    private static final long serialVersionUID = 8750206937467686912L;

    /**
     * @param <T>
     * @param objects
     * @return list
     */
    public static <T> SkipList<T> list(final T... objects)
    {
        return new SkipList<T>(objects);
    }

    @Override
    public boolean add(final E element)
    {
        addBefore(element, head);
        return true;
    }

    @Override
    public void add(final int index, final E element)
    {
        if (index == size)
        {
            addBefore(element, head);
        }
        else
        {
            final Entry<E> entry = getEntry(index);
            addBefore(element, entry);
        }
    }

    /**
     * @param e
     * @return this
     */
    public SkipList<E> append(final E e)
    {
        add(e);
        return this;
    }

    /**
     * @return backwardIterator
     */
    public Iterator<E> backwardIterator()
    {
        return new BackwardEntryIterator(head);
    }

    @Override
    public void clear()
    {
        buildHead();
        size = 0;
    }

    @Override
    public E get(final int index)
    {
        return getEntry(index).value;
    }

    @Override
    public int indexOf(final Object o)
    {
        int index = 0;
        Entry<E> e = head.next;
        if (o == null)
        {
            for (; e != head; e = e.next, index++)
            {
                if (e.value == null)
                {
                    return index;
                }
            }
        }
        else
        {
            for (; e != head; e = e.next, index++)
            {
                if (e.value.equals(o))
                {
                    return index;
                }
            }
        }
        return -1;
    }

    @Override
    public Iterator<E> iterator()
    {
        return new EntryIterator(head);
    }

    @Override
    public int lastIndexOf(final Object o)
    {
        int index = size - 1;
        Entry<E> e = head.prev;
        if (o == null)
        {
            for (; e != head; e = e.prev, index--)
            {
                if (e.value == null)
                {
                    return index;
                }
            }
        }
        else
        {
            for (; e != head; e = e.prev, index--)
            {
                if (e.value.equals(o))
                {
                    return index;
                }
            }
        }
        return -1;
    }

    @Override
    public E remove(final int index)
    {
        final Entry<E> entry = getEntry(index);
        removeEntry(entry);
        return entry.value;
    }

    @Override
    public boolean remove(final Object o)
    {
        final int index = indexOf(o);
        if (index == -1)
        {
            return false;
        }
        remove(index);
        return true;
    }

    @Override
    public E set(final int index, final E value)
    {
        final Entry<E> entry = getEntry(index);
        final E oldValue = entry.value;
        entry.value = value;
        return oldValue;
    }

    @Override
    public int size()
    {
        return size;
    }

    @SuppressWarnings("unchecked")
    protected Entry<E> addBefore(final E element, final Entry<E> entry)
    {
        int headLevel = head.level();
        final int level = Math.min(generateRandomLevel(), headLevel + 1);
        if (level > headLevel)
        {
            final Pointer<E>[] pts = new Pointer[level];
            for (int i = 0; i < headLevel; i++)
            {
                pts[i] = head.pts[i];
            }
            for (int i = headLevel; i < level; i++)
            {
                pts[i] = new Pointer<E>(head, head, 0);
            }
            head.pts = pts;
            headLevel = level;
        }

        Entry<E> prev = entry.prev;
        Entry<E> next = entry;
        final Entry<E> e = new Entry<E>(level, prev, next, element);
        next.prev = e;
        prev.next = e;

        int prevDistance = 1;
        int nextDistance = 1;
        for (int i = 0; i < level; i++)
        {
            while (prev.pts == null)
            {
                prevDistance++;
                prev = prev.prev;
            }
            int lv = prev.level();
            while (lv <= i)
            {
                final Pointer<E> prevPt = prev.pts[lv - 1];
                prevDistance += prevPt.prev.pts[lv - 1].distance;
                prev = prevPt.prev;
                lv = prev.pts.length;
            }
            while (next.pts == null)
            {
                nextDistance++;
                next = next.next;
            }
            lv = next.level();
            while (lv <= i)
            {
                final Pointer<E> nextPt = next.pts[lv - 1];
                nextDistance += nextPt.distance;
                next = nextPt.next;
                lv = next.pts.length;
            }

            e.pts[i] = new Pointer<E>(prev, next, nextDistance);

            prev.pts[i].next = e;
            prev.pts[i].distance = prevDistance;
            next.pts[i].prev = e;
        }
        for (int i = level; i < headLevel; i++)
        {
            while (prev.pts == null)
            {
                prev = prev.prev;
            }
            while (prev.pts.length <= i)
            {
                prev = prev.pts[prev.pts.length - 1].prev;
            }
            prev.pts[i].distance++;
        }

        size++;
        return e;
    }

    protected Entry<E> getEntry(final int index)
    {
        if (index < 0 || index >= size)
        {
            throw new IndexOutOfBoundsException("size: " + size + ", index: "
                    + index);
        }
        Entry<E> e = head;
        int level = e.level();
        int curIndex = -1;
        while (curIndex != index)
        {
            if (level == 0)
            {
                e = e.next;
                curIndex++;
            }
            else
            {
                final Pointer<E> p = e.pts[level - 1];
                final int n = curIndex + p.distance;
                if (n <= index)
                {
                    e = p.next;
                    curIndex = n;
                }
                else
                {
                    level--;
                }
            }
        }
        return e;
    }

    protected int getIndex(final Entry<E> entry)
    {
        Entry<E> e = entry;
        int distance = 0;
        while (e != head)
        {
            if (e.pts == null)
            {
                distance++;
                e = e.next;
            }
            else
            {
                final Pointer<E> p = e.pts[e.pts.length - 1];
                distance += p.distance;
                e = p.next;
            }
        }
        return size - distance;
    }

    protected void removeEntry(final Entry<E> entry)
    {
        Entry<E> prev = entry.prev;
        Entry<E> next = entry.next;
        prev.next = next;
        next.prev = prev;
        final int level = entry.level();
        if (level > 0)
        {
            for (int i = 0; i < level; i++)
            {
                final Pointer<E> p = entry.pts[i];
                prev = p.prev;
                next = p.next;
                prev.pts[i].next = next;
                next.pts[i].prev = prev;
                prev.pts[i].distance += p.distance - 1;
            }
            prev = entry.pts[level - 1].prev;
        }
        final int headLevel = head.level();
        for (int i = level; i < headLevel; i++)
        {
            while (prev.pts == null)
            {
                prev = prev.prev;
            }
            while (i >= prev.pts.length)
            {
                prev = prev.pts[prev.pts.length - 1].prev;
            }
            prev.pts[i].distance--;
        }
        size--;
    }

    private void buildHead()
    {
        head = new Entry<E>();
        head.prev = head;
        head.next = head;
    }

    // [ref] java.util.concurrent.ConcurrentSkipListMap
    private int generateRandomLevel()
    {
        int x = randomSeed;
        x ^= x << 13;
        x ^= x >>> 17;
        randomSeed = x ^= x << 5;
        if ((x & 0x8001) != 0)
        {
            return 0;
        }
        int level = 0;
        while (((x >>>= 1) & 1) != 0)
        {
            level++;
        }
        return level;
    }

    /**
     * constractor
     */
    public SkipList()
    {
        final Random rand = new Random();
        randomSeed = rand.nextInt() | 0x100;
        buildHead();
    }

    /**
     * @param objects
     */
    public SkipList(final Collection<E>... objects)
    {
        this();
        for (final Collection<E> object : objects)
        {
            addAll(object);
        }
    }

    /**
     * @param objects
     */
    public SkipList(final Collection<E> objects)
    {
        this();
        if (objects == null || objects.isEmpty())
        {
            return;
        }
        addAll(objects);
    }

    /**
     * @param es
     */
    public SkipList(final E... es)
    {
        this();
        for (final E e : es)
        {
            this.add(e);
        }
    }

    protected Entry<E> head;

    protected int size;

    private int randomSeed;

}