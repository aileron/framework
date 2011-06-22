/**
 * 
 */
package cc.aileron.generic;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * 複数系
 * 
 * @author aileron
 */
public class Plural
{
    public static class BTree<T>
    {
        static class BNode<T>
        {
            /**
             * @param hash
             */
            public BNode(final int hash)
            {
                id = hash;
            }

            int id;
            BNode<T> lft;
            BNode<T> rht;
            T value;
        }

        void put(final T value)
        {
            final int hash = value.hashCode();
            if (root.id == 0)
            {
                root.id = hash;
                root.value = value;
                return;
            }

            BNode<T> item = root;

            for (;;)
            {
                if (item.id == hash)
                {
                    item.value = value;
                    return;
                }

                if (item.id < hash)
                {
                    final BNode<T> tmp = item.lft;
                    item.lft = tmp != null ? tmp : new BNode<T>(hash);
                    item = item.lft;
                }
                else
                {
                    final BNode<T> tmp = item.rht;
                    item.rht = tmp != null ? tmp : new BNode<T>(hash);
                    item = item.rht;
                }
            }
        }

        BNode<T> root = new BNode<T>(0);
    }

    /**
     * @author aileron
     */
    public static class Bytes
    {
        /**
         * @param value
         */
        public synchronized void add(final byte value)
        {
            if (array.length < size + 1)
            {
                final byte[] newArray = new byte[array.length << 1];
                System.arraycopy(array, 0, newArray, 0, array.length);
                array = newArray;
            }
            array[size++] = value;
        }

        @Override
        public boolean equals(final Object object)
        {
            if (object instanceof byte[])
            {
                return hashCode() == Arrays.hashCode((byte[]) object);
            }
            if (object instanceof Bytes)
            {
                return hashCode() == ((Bytes) object).hashCode();
            }
            return false;
        }

        /**
         * @param idx
         * @return value
         */
        public int get(final int idx)
        {
            return array[idx];
        }

        @Override
        public int hashCode()
        {
            return Arrays.hashCode(array);
        }

        /**
         * @param value
         */
        public synchronized void push(final byte value)
        {
            final byte[] newArray = new byte[array.length << 1];
            System.arraycopy(array, 0, newArray, 1, array.length);
            newArray[0] = value;
            array = newArray;
        }

        /**
         * @return size
         */
        public int size()
        {
            return size;
        }

        /**
         * @return toArray
         */
        public byte[] toArray()
        {
            return array;
        }

        /**
         * @param length
         * @param offset
         * @return toArray
         */
        public byte[] toArray(final int length, final int offset)
        {
            final byte[] result = new byte[length];
            System.arraycopy(array, offset, result, 0, length);
            return result;
        }

        /**
         */
        public Bytes()
        {
            this.array = new byte[128];
        }

        /**
         * @param array
         */
        public Bytes(final byte[] array)
        {
            this.array = array;
        }

        volatile byte[] array;
        volatile int size = 0;
    }

    /**
     * Int を キーにした、ディクショナリ
     * 
     * @author aileron
     * @param <T>
     */
    public static class IntDict<T>
    {
        /**
         * @param id
         * @return value
         */
        public T get(final int id)
        {
            return array[id % array.length];
        }

        /**
         * @param id
         * @param value
         */
        public void put(final int id, final T value)
        {
            if (array.length <= size)
            {
                final T[] self = $.cast(Array.newInstance(type,
                        size >> 1));
                System.arraycopy(array, 0, self, 0, size);
                array = self;
            }
            array[id % array.length] = value;
            size++;
        }

        /**
         * @param type
         */
        public IntDict(final Class<T> type)
        {
            this.type = type;
            this.array = $.cast(Array.newInstance(type, 10));
        }

        private T[] array;
        private int size;
        private final Class<T> type;
    }

    /**
     * Ints 実装
     */
    public static class Ints
    {
        /**
         * @param value
         */
        public synchronized void add(final int value)
        {
            if (array.length < size + 1)
            {
                final int[] newArray = new int[array.length << 1];
                System.arraycopy(array, 0, newArray, 0, array.length);
                array = newArray;
            }
            array[size++] = value;
        }

        /**
         * @param idx
         * @return value
         */
        public int get(final int idx)
        {
            return array[idx];
        }

        /**
         * @param value
         */
        public synchronized void push(final int value)
        {
            final int[] newArray = new int[array.length << 1];
            System.arraycopy(array, 0, newArray, 1, array.length);
            newArray[0] = value;
            array = newArray;
        }

        /**
         * @return size
         */
        public int size()
        {
            return size;
        }

        /**
         * @return toArray
         */
        public int[] toArray()
        {
            return array;
        }

        /**
         * @param length
         * @param offset
         * @return toArray
         */
        public int[] toArray(final int length, final int offset)
        {
            final int[] result = new int[length];
            System.arraycopy(array, offset, result, 0, length);
            return result;
        }

        volatile int[] array = new int[10];
        volatile int size = 0;
    }

}
