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
package cc.aileron.pojo.type;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import cc.aileron.generic.PrimitiveWrappers.BooleanGetAccessor;
import cc.aileron.generic.PrimitiveWrappers.NumberGetAccessor;

/**
 * @author Aileron
 */
public class TypeConvertorImpl implements TypeConvertor
{
    private static interface C
    {
        Object convert(final java.lang.Number number);
    }

    @Override
    public Object convert(final Object value, final Class<?> from,
            final Class<?> to)
    {
        /*
         * to boolean
         */
        if (Boolean.class.isAssignableFrom(to) || to == Boolean.TYPE)
        {
            if (value == null)
            {
                return false;
            }
            if (Boolean.class.isAssignableFrom(from) || Boolean.TYPE == from)
            {
                return value;
            }
            if (Number.class.isAssignableFrom(from) || from.isPrimitive())
            {
                return ((Number) value).intValue() != 0;
            }
            if (BooleanGetAccessor.class.isAssignableFrom(from))
            {
                final BooleanGetAccessor accessor = (BooleanGetAccessor) value;
                return accessor.toBoolean();
            }
            if (String.class == from)
            {
                return Boolean.parseBoolean(String.class.cast(value));
            }
            return true;
        }

        /*
         * to primitive is null
         */
        if (to.isPrimitive() && value == null)
        {
            return 0;
        }

        /*
         * value is null
         */
        if (value == null)
        {
            return null;
        }

        /*
         * to primitive from string
         */
        if (to.isPrimitive() && String.class == from)
        {
            if (((String) value).isEmpty())
            {
                return 0;
            }
            final Integer number = Integer.valueOf((String) value);
            return numberToPrimitive(number, to);
        }

        /*
         * to primitive from number
         */
        if (to.isPrimitive() && Number.class.isAssignableFrom(from))
        {
            final Number number = (Number) value;
            return numberToPrimitive(number, to);
        }

        /*
         * to number from string
         */
        if (Number.class.isAssignableFrom(to) && String.class == from)
        {
            final String string = (String) value;
            if (Boolean.TYPE == to)
            {
                return Boolean.parseBoolean(string);
            }
            if (Character.TYPE == to)
            {
                throw new UnsupportedOperationException();
            }
            final Number number = Integer.valueOf(string);
            return numberToPrimitive(number, to);
        }

        /*
         * to number from number-get-accessor
         */
        if (Number.class.isAssignableFrom(to)
                && value instanceof NumberGetAccessor)
        {
            final NumberGetAccessor accessor = (NumberGetAccessor) value;
            return accessor.toNumber();
        }

        /*
         * to string from not string
         */
        if (to == String.class && !(value instanceof String))
        {
            return value.toString();
        }

        /*
         * to enum from String
         */
        if (to.isEnum() && from == String.class)
        {
            final String string = (String) value;
            int number;
            boolean isNumber = false;
            try
            {
                number = Integer.parseInt(string);
                isNumber = true;
            }
            catch (final NumberFormatException e)
            {
                number = 0;
            }
            try
            {
                if (isNumber)
                {
                    return to.getMethod("valueOf", Integer.TYPE).invoke(null,
                            number);
                }
                return to.getMethod("valueOf", String.class).invoke(null,
                        string);
            }
            catch (final IllegalArgumentException e)
            {
                throw new Error(e);
            }
            catch (final SecurityException e)
            {
                throw new Error(e);
            }
            catch (final IllegalAccessException e)
            {
                throw new Error(e);
            }
            catch (final InvocationTargetException e)
            {
                throw new Error(e);
            }
            catch (final NoSuchMethodException e)
            {
                throw new Error(e);
            }
        }

        /*
         * to enum from integer
         */
        if (to.isEnum() && (from == Integer.TYPE || from == Integer.class))
        {
            try
            {
                return to.getMethod("valueOf", Integer.TYPE)
                        .invoke(null, value);
            }
            catch (final SecurityException e)
            {
                throw new Error(e);
            }
            catch (final NoSuchMethodException e)
            {
                throw new Error(e);
            }
            catch (final IllegalArgumentException e)
            {
                throw new Error(e);
            }
            catch (final IllegalAccessException e)
            {
                throw new Error(e);
            }
            catch (final InvocationTargetException e)
            {
                throw new Error(e);
            }
        }

        return value;
    }

    /**
     * @param parameter
     * @param type
     * @return convert-value
     */
    private Object numberToPrimitive(final Number parameter, final Class<?> type)
    {
        final C c = map.get(type);
        if (c != null)
        {
            return c.convert(parameter);
        }
        return parameter;
    }

    /**
     * default constractor
     */
    public TypeConvertorImpl()
    {
        map.put(Boolean.TYPE, new C()
        {
            @Override
            public Boolean convert(final java.lang.Number number)
            {
                return number.intValue() != 0;
            }
        });
        map.put(Byte.TYPE, new C()
        {

            @Override
            public Byte convert(final java.lang.Number number)
            {
                return number.byteValue();
            }
        });
        map.put(Short.TYPE, new C()
        {

            @Override
            public Short convert(final java.lang.Number number)
            {
                return number.shortValue();
            }
        });
        map.put(Integer.TYPE, new C()
        {
            @Override
            public Integer convert(final java.lang.Number number)
            {
                return number.intValue();
            }

        });
        map.put(Long.TYPE, new C()
        {

            @Override
            public Long convert(final java.lang.Number number)
            {
                return number.longValue();
            }
        });
        map.put(Float.TYPE, new C()
        {

            @Override
            public Float convert(final java.lang.Number number)
            {
                return number.floatValue();
            }
        });
        map.put(Double.TYPE, new C()
        {

            @Override
            public Double convert(final java.lang.Number number)
            {
                return number.doubleValue();
            }
        });
    }

    private final HashMap<Class<?>, C> map = new HashMap<Class<?>, C>();
}