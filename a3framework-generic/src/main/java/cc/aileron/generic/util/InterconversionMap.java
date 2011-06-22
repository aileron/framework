/**
 * Copyright (C) 2009 aileron.cc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package cc.aileron.generic.util;

import static cc.aileron.generic.$.*;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

/**
 * enum <=> int 関係を保持する為のmap
 * 
 * @author aileron
 * @param <T>
 * 
 */
public class InterconversionMap<T extends Enum<T>>
{
    /**
     * map
     */
    private static final HashMap<Class<?>, InterconversionMap<?>> _map = new HashMap<Class<?>, InterconversionMap<?>>();

    /**
     * @param <T>
     * @param type
     * @return map
     */
    public static <T extends Enum<T>> InterconversionMap<T> category(
            final Class<T> type)
    {
        final InterconversionMap<?> map = _map.get(type);
        if (map != null)
        {
            return cast(map);
        }
        final InterconversionMap<T> newMap = new InterconversionMap<T>();
        _map.put(type, newMap);
        return newMap;
    }

    /**
     * @param <T>
     * @param enumValue
     * @param intValue
     */
    public static <T extends Enum<T>> void set(final T enumValue,
            final int intValue)
    {
        category(enumValue.getDeclaringClass()).set(intValue, enumValue);
    }

    /**
     * @param <T>
     * @param type
     * @param intValue
     * @return intValue
     */
    public static <T extends Enum<T>> T value(final Class<T> type,
            final int intValue)
    {
        return category(type).convert(intValue);
    }

    /**
     * @param <T>
     * @param enumValue
     * @return intValue
     */
    public static <T extends Enum<T>> int value(final T enumValue)
    {
        return category(enumValue.getDeclaringClass()).convert(enumValue);
    }

    /**
     * int から enum への変換
     * 
     * @param key
     * @return enum
     */
    public T convert(final int key)
    {
        return iMap.get(key);
    }

    /**
     * enum から int への変換
     * 
     * @param key
     * @return int
     */
    public int convert(final T key)
    {
        return eMap.get(key);
    }

    /**
     * int <=> enum の関係を設定
     * 
     * @param intValue
     * @param enumValue
     * @return this
     * 
     */
    public InterconversionMap<T> set(final int intValue, final T enumValue)
    {
        iMap.put(intValue, enumValue);
        eMap.put(enumValue, intValue);
        return this;
    }

    /**
     * @return values
     */
    public Set<Entry<Integer, T>> values()
    {
        return iMap.entrySet();
    }

    /**
     * default constractor
     */
    public InterconversionMap()
    {
    }

    /**
     * @param enums
     */
    public InterconversionMap(final T[] enums)
    {
        for (final T e : enums)
        {
            set(e.ordinal() + 1, e);
        }
    }

    /**
     * enum map
     */
    private final HashMap<T, Integer> eMap = new HashMap<T, Integer>();

    /**
     * int map
     */
    private final HashMap<Integer, T> iMap = new HashMap<Integer, T>();
}