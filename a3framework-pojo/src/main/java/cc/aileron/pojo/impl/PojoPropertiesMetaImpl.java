/*
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
package cc.aileron.pojo.impl;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import cc.aileron.pojo.meta.ClassAccessor;
import cc.aileron.pojo.meta.ClassAccessorGetter;
import cc.aileron.pojo.meta.ClassAccessorMapSetter;
import cc.aileron.pojo.meta.ClassAccessorSetter;
import cc.aileron.pojo.meta.PojoAccessorGetter;
import cc.aileron.pojo.meta.PojoAccessorSetter;
import cc.aileron.pojo.meta.PojoPropertiesMeta;
import cc.aileron.pojo.meta.PojoPropertiesMetaCategory;

/**
 * @author Aileron
 * @param <T>
 */
public class PojoPropertiesMetaImpl<T> implements PojoPropertiesMeta<T>
{
    /**
     * getter-patterns
     */
    public static final String[] getterPatterns = { "get", "is", "to", "" };

    /**
     * map-accessor-keys-set
     */
    public static final HashMap<String, Void> mapAccessorKeys = new HashMap<String, Void>();

    /**
     * map-accesor-keys-get
     */
    public static final String mapGetAccessorKey = "get";

    /**
     * self
     */
    public static final String SELF = "self";

    /**
     * setter-patterns
     */
    public static final String[] setterPatterns = { "set", "" };

    /**
     * error-keys
     */
    private static final int ERR_KEY_WAIT = "wait".hashCode(),
            ERR_KEY_NOTIFY = "notify".hashCode(),
            ERR_KEY_NOFIFYALL = "notifyall".hashCode();

    static
    {
        for (final String key : new String[] { "set", "put" })
        {
            mapAccessorKeys.put(key, null);
        }
    }

    @Override
    public PojoAccessorGetter get(final T target, final String name)
    {
        if (SELF.equals(name))
        {
            return new PojoAccessorGetter()
            {

                @Override
                public Type genericType()
                {
                    return null;
                }

                @Override
                public Object get()
                {
                    return target;
                }

                @Override
                public PojoPropertiesMetaCategory meta()
                {
                    return PojoPropertiesMetaCategory.SELF;
                }

                @Override
                public Class<?> resultType()
                {
                    return targetClass;
                }
            };
        }
        final ClassAccessorGetter getter = getGetter(name);
        return getter == null ? null : new PojoAccessorGetter()
        {
            @Override
            public Type genericType()
            {
                return getter.genericType();
            }

            @Override
            public Object get()
            {
                try
                {
                    return getter.get(target, name);
                }
                catch (final IllegalArgumentException e)
                {
                    throw new Error(e);
                }
                catch (final IllegalAccessException e)
                {
                    throw new Error(e);
                }
            }

            @Override
            public PojoPropertiesMetaCategory meta()
            {
                return getter.meta();
            }

            @Override
            public Class<?> resultType()
            {
                return getter.resultType();
            }
        };
    }

    /**
     * @param name
     * @return getter
     */
    public ClassAccessorGetter getGetter(final String name)
    {
        /*
         * getter
         */
        final String key = name.toLowerCase();
        for (final String pattern : getterPatterns)
        {
            final String methodName = pattern + key;
            final ClassAccessorGetter accessor = getter.get(methodName);
            if (accessor != null)
            {
                return accessor;
            }
        }

        /*
         * field access
         */
        {
            final ClassAccessorGetter accessor = fields.get(key);
            if (accessor != null)
            {
                return accessor;
            }
        }

        /*
         * get array accessor
         */
        {
            if (isArrayClass)
            {
                return arrayAccessor;
            }
        }

        /*
         * get mapAccessor
         */
        {
            final ClassAccessorGetter accessor = mapGetAccessor;
            if (accessor != null)
            {
                return accessor;
            }
        }

        return null;
    }

    /**
     * @param name
     * @return setter
     */
    public ClassAccessorSetter getSetter(final String name)
    {

        final String key = name.toLowerCase();

        /*
         * scala access
         */
        {
            final ClassAccessorSetter accessorSetter = setter.get(key + "_$eq");
            if (accessorSetter != null)
            {
                return accessorSetter;
            }
        }

        /*
         * setter
         */
        for (final String pattern : setterPatterns)
        {
            final String methodName = pattern + key;
            final ClassAccessorSetter accessor = setter.get(methodName);
            if (accessor != null)
            {
                return accessor;
            }
        }

        /*
         * field access
         */
        {
            final ClassAccessorSetter accessor = fields.get(key);
            if (accessor != null)
            {
                return accessor;
            }
        }

        /*
         * set mapAccessor
         */
        {
            final ClassAccessorMapSetter accessor = mapSetAccessor;
            if (accessor != null)
            {
                return new ClassAccessorSetter()
                {
                    @Override
                    public Class<?> argumentType()
                    {
                        return accessor.argumentType();
                    }

                    @Override
                    public Type genericType()
                    {
                        return accessor.genericType();
                    }

                    @Override
                    public PojoPropertiesMetaCategory meta()
                    {
                        return PojoPropertiesMetaCategory.MAP;
                    }

                    @Override
                    public void set(final Object target, final Object value)
                    {
                        accessor.set(target, key, value);
                    }
                };
            }
        }

        return null;
    }

    @Override
    public Iterable<String> keys()
    {
        return keys;
    }

    @Override
    public PojoAccessorSetter set(final T target, final String name)
    {
        if (SELF.equals(name))
        {
            return new PojoAccessorSetter()
            {
                @Override
                public Class<?> argumentType()
                {
                    throw new UnsupportedOperationException();
                }

                @Override
                public Type genericType()
                {
                    throw new UnsupportedOperationException();
                }

                @Override
                public PojoPropertiesMetaCategory meta()
                {
                    return PojoPropertiesMetaCategory.SELF;
                }

                @Override
                public void set(final Object value)
                {
                    throw new UnsupportedOperationException();
                }
            };
        }
        final ClassAccessorSetter setter = getSetter(name);
        return setter == null ? null : new PojoAccessorSetter()
        {
            @Override
            public Class<?> argumentType()
            {
                return setter.argumentType();
            }

            @Override
            public Type genericType()
            {
                return setter.genericType();
            }

            @Override
            public PojoPropertiesMetaCategory meta()
            {
                return setter.meta();
            }

            @Override
            public void set(final Object value)
                    throws IllegalArgumentException, IllegalAccessException,
                    InvocationTargetException
            {
                setter.set(target, value);
            }
        };
    }

    /**
     * @param targetClass
     */
    public PojoPropertiesMetaImpl(final Class<T> targetClass)
    {
        this.targetClass = targetClass;
        isArrayClass = this.targetClass.isArray();

        ClassAccessorGetter mapGetter = null;
        ClassAccessorMapSetter mapSetter = null;

        for (final Method method : targetClass.getMethods())
        {
            /*
             * static メソッドは対象外
             */
            if (Modifier.isStatic(method.getModifiers()))
            {
                continue;
            }

            /*
             * アクセス権限を一旦privateでも頑張る
             */
            if (!method.isAccessible())
            {
                try
                {
                    method.setAccessible(true);
                }
                catch (final SecurityException e)
                {
                    continue;
                }
            }

            /*
             * メソッド名
             */
            final String methodName = method.getName().toLowerCase();

            /*
             * pojo-accessorでcallすると誤動作する為 対象から除外
             */
            final int code = methodName.hashCode();
            if (ERR_KEY_NOFIFYALL == code || ERR_KEY_NOTIFY == code
                    || ERR_KEY_WAIT == code)
            {
                continue;
            }

            if (mapAccessorKeys.containsKey(methodName))
            {
                mapSetter = new ClassAccessorMapSetter()
                {

                    @Override
                    public Class<?> argumentType()
                    {
                        return method.getParameterTypes()[1];
                    }

                    @Override
                    public Type genericType()
                    {
                        return method.getGenericParameterTypes()[1];
                    }

                    @Override
                    public PojoPropertiesMetaCategory meta()
                    {
                        return PojoPropertiesMetaCategory.MAP;
                    }

                    @Override
                    public void set(final Object target, final String key,
                            final Object value)
                    {
                        try
                        {
                            method.invoke(target, key, value);
                        }
                        catch (final Exception e)
                        {
                            throw new Error(e);
                        }
                    }

                };
                continue;
            }
            if (mapGetAccessorKey.equals(methodName))
            {
                mapGetter = new ClassAccessorGetter()
                {
                    @Override
                    public Type genericType()
                    {
                        return method.getGenericReturnType();
                    }

                    @Override
                    public Object get(final Object target, final String key)
                    {
                        final Class<?> type = method.getParameterTypes()[0];
                        try
                        {
                            if (String.class.isAssignableFrom(type))
                            {
                                return method.invoke(target, key);
                            }
                            if (Integer.TYPE.isAssignableFrom(type))
                            {
                                final int idx = Integer.parseInt(key);
                                return method.invoke(target, idx);
                            }
                            if (Map.class.isAssignableFrom(target.getClass()))
                            {
                                final Map<?, ?> map = Map.class.cast(target);
                                if (map.isEmpty())
                                {
                                    return null;
                                }
                                final Object val = map.keySet()
                                        .iterator()
                                        .next()
                                        .getClass()
                                        .getMethod("valueOf", String.class)
                                        .invoke(null, key);
                                return method.invoke(target, val);
                            }
                            throw new Error(type + "の型に対しては、getメソッドが対応していません");
                        }
                        catch (final Exception e)
                        {
                            throw new Error(e);
                        }
                    }

                    @Override
                    public PojoPropertiesMetaCategory meta()
                    {
                        return PojoPropertiesMetaCategory.MAP;
                    }

                    @Override
                    public Class<?> resultType()
                    {
                        return method.getReturnType();
                    }

                };

                keys.add(methodName);
            }

            switch (method.getParameterTypes().length)
            {
            case 0:
                getter.put(methodName, new ClassAccessorGetter()
                {

                    @Override
                    public Type genericType()
                    {
                        return method.getGenericReturnType();
                    }

                    @Override
                    public Object get(final Object target, final String key)
                    {
                        try
                        {
                            return method.invoke(target);
                        }
                        catch (final Exception e)
                        {
                            throw new Error(targetClass + "@" + key, e);
                        }
                    }

                    @Override
                    public PojoPropertiesMetaCategory meta()
                    {
                        return PojoPropertiesMetaCategory.PROPERTIES;
                    }

                    @Override
                    public Class<?> resultType()
                    {
                        return method.getReturnType();
                    }

                });
                break;

            case 1:
                setter.put(methodName, new ClassAccessorSetter()
                {

                    @Override
                    public Class<?> argumentType()
                    {
                        return method.getParameterTypes()[0];
                    }

                    @Override
                    public Type genericType()
                    {
                        return method.getGenericParameterTypes()[0];
                    }

                    @Override
                    public PojoPropertiesMetaCategory meta()
                    {
                        return PojoPropertiesMetaCategory.PROPERTIES;
                    }

                    @Override
                    public void set(final Object target, final Object value)
                            throws IllegalArgumentException,
                            IllegalAccessException, InvocationTargetException
                    {
                        method.invoke(target, value);
                    }

                });
                break;
            }
        }
        for (final Field field : targetClass.getFields())
        {
            /*
             * static は対象外
             */
            if (Modifier.isStatic(field.getModifiers()))
            {
                continue;
            }

            if (!field.isAccessible())
            {
                try
                {
                    field.setAccessible(true);
                }
                catch (final SecurityException e)
                {
                    continue;
                }
            }
            final String key = field.getName().toLowerCase();
            fields.put(key, new ClassAccessor()
            {

                @Override
                public Class<?> argumentType()
                {
                    return field.getType();
                }

                @Override
                public Type genericType()
                {
                    return field.getGenericType();
                }

                @Override
                public Object get(final Object target, final String key)
                        throws IllegalArgumentException, IllegalAccessException
                {
                    return field.get(target);
                }

                @Override
                public PojoPropertiesMetaCategory meta()
                {
                    return PojoPropertiesMetaCategory.FIELDS;
                }

                @Override
                public Class<?> resultType()
                {
                    return resultType;
                }

                @Override
                public void set(final Object target, final Object value)
                        throws IllegalArgumentException, IllegalAccessException
                {
                    field.set(target, value);
                }

                final Class<?> resultType = field.getType();
            });
            keys.add(key);
        }
        this.mapGetAccessor = mapGetter;
        this.mapSetAccessor = mapSetter;

        this.arrayAccessor = new ClassAccessorGetter()
        {
            @Override
            public Type genericType()
            {
                return targetClass;
            }

            @Override
            public Object get(final Object target, final String key)
            {
                return Array.get(target, Integer.parseInt(key));
            }

            @Override
            public PojoPropertiesMetaCategory meta()
            {
                return PojoPropertiesMetaCategory.ARRAY;
            }

            @Override
            public Class<?> resultType()
            {
                return targetClass;
            }
        };
    }

    /**
     * フィールドの一覧
     */
    public final HashMap<String, ClassAccessor> fields = new HashMap<String, ClassAccessor>();

    /**
     * getter
     */
    public final HashMap<String, ClassAccessorGetter> getter = new HashMap<String, ClassAccessorGetter>();

    /**
     * map-get-accessor
     */
    public final ClassAccessorGetter mapGetAccessor;

    /**
     * map-accessor
     */
    public final ClassAccessorMapSetter mapSetAccessor;

    /**
     * setter
     */
    public final HashMap<String, ClassAccessorSetter> setter = new HashMap<String, ClassAccessorSetter>();

    /**
     * 対象のクラス
     */
    public final Class<T> targetClass;

    /**
     * Array
     */
    private final ClassAccessorGetter arrayAccessor;

    /**
     * Arrayかどうか
     */
    private final boolean isArrayClass;

    /**
     * keys
     */
    private final HashSet<String> keys = new HashSet<String>();
}