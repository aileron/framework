/**
 *
 */
package cc.aileron.web.impl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.aileron.generic.$;
import cc.aileron.generic.util.SkipList;
import cc.aileron.web.WebParam;
import cc.aileron.web.WebProcess;
import cc.aileron.wsgi.Wsgi;

import com.google.inject.Singleton;

/**
 * WorkflowParameterBinder のデフォルト実装
 * 
 * @author aileron
 */
@Singleton
public class WebParameterBinderImpl implements WebParameterBinder
{
    static abstract class MethodInvoker<T>
    {

        /**
         * @param self
         * @param args
         * @throws Exception
         */
        void invoke(final T self, final String[][] args) throws Exception
        {
            final Object[] param = new Object[args.length];
            for (int i = 0, size = args.length; i < size; i++)
            {
                param[i] = convertor[i].get(args[i]);
            }
            try
            {
                method.invoke(self, param);
            }
            catch (final InvocationTargetException e)
            {
                final Throwable cause = e.getCause();
                if (cause instanceof Exception)
                {
                    throw (Exception) cause;
                }
                throw (Error) cause;
            }
        }

        abstract String[] keys();

        /**
         * @param method
         * @throws NoSuchMethodException
         * @throws SecurityException
         */
        public MethodInvoker(final Method method) throws SecurityException,
                NoSuchMethodException
        {
            this.method = method;

            final Class<?>[] types = method.getParameterTypes();
            convertor = new ObjectTypeConvertor[types.length];
            for (int i = 0, size = types.length; i < size; i++)
            {
                final Class<?> type = types[i];
                final Class<?> componentType = type.getComponentType();
                if (type.isPrimitive()
                        || type == String.class
                        || (componentType != null && (componentType.isPrimitive() || componentType == String.class)))
                {
                    convertor[i] = map.get(type);
                    continue;
                }

                final Method valueOf = type.getMethod("valueOf", String.class);
                if (type.isArray())
                {
                    convertor[i] = new ObjectTypeConvertor()
                    {
                        @Override
                        public Object get(final String[] values)
                        {
                            return null;
                        }
                    };
                    continue;
                }

                convertor[i] = new ObjectTypeConvertor()
                {
                    @Override
                    public Object get(final String[] values)
                            throws IllegalArgumentException,
                            IllegalAccessException, InvocationTargetException
                    {
                        return valueOf.invoke(null, values[0]);
                    }
                };
            }
        }

        private final ObjectTypeConvertor[] convertor;
        private final Method method;
    }

    static interface ObjectTypeConvertor
    {
        Object get(String[] values)
                throws IllegalArgumentException, IllegalAccessException,
                InvocationTargetException;
    }

    static final HashMap<Class<?>, ObjectTypeConvertor> map = new HashMap<Class<?>, ObjectTypeConvertor>();

    static
    {
        map.put(new String[] {}.getClass(), new ObjectTypeConvertor()
        {
            @Override
            public Object get(final String[] value)
            {
                return value;
            }
        });
        map.put(new int[] {}.getClass(), new ObjectTypeConvertor()
        {
            @Override
            public Object get(final String[] k)
            {
                if (k == null)
                {
                    return null;
                }
                final int[] args = new int[k.length];
                for (int i = 0, size = k.length; i < size; i++)
                {
                    args[i] = Integer.parseInt(k[i]);
                }
                return args;
            }
        });
        map.put(new boolean[] {}.getClass(), new ObjectTypeConvertor()
        {
            @Override
            public Object get(final String[] k)
            {
                if (k == null)
                {
                    return null;
                }
                final boolean[] args = new boolean[k.length];
                for (int i = 0, size = k.length; i < size; i++)
                {
                    args[i] = Boolean.parseBoolean(k[i]);
                }
                return args;
            }
        });
        map.put(new float[] {}.getClass(), new ObjectTypeConvertor()
        {
            @Override
            public Object get(final String[] k)
            {
                if (k == null)
                {
                    return null;
                }
                final float[] args = new float[k.length];
                for (int i = 0, size = k.length; i < size; i++)
                {
                    args[i] = Float.parseFloat(k[i]);
                }
                return args;
            }
        });
        map.put(new double[] {}.getClass(), new ObjectTypeConvertor()
        {
            @Override
            public Object get(final String[] k)
            {
                if (k == null)
                {
                    return null;
                }
                final double[] args = new double[k.length];
                for (int i = 0, size = k.length; i < size; i++)
                {
                    args[i] = Double.parseDouble(k[i]);
                }
                return args;
            }
        });
        map.put(new short[] {}.getClass(), new ObjectTypeConvertor()
        {
            @Override
            public Object get(final String[] k)
            {
                if (k == null)
                {
                    return null;
                }
                final short[] args = new short[k.length];
                for (int i = 0, size = k.length; i < size; i++)
                {
                    args[i] = Short.parseShort(k[i]);
                }
                return args;
            }
        });
        map.put(Boolean.TYPE, new ObjectTypeConvertor()
        {
            @Override
            public Object get(final String[] k)
            {
                if (k == null)
                {
                    return false;
                }
                return Boolean.parseBoolean(k[0]);
            }
        });
        map.put(Float.TYPE, new ObjectTypeConvertor()
        {
            @Override
            public Object get(final String[] k)
            {
                if (k == null)
                {
                    return 0;
                }
                return Float.parseFloat(k[0]);
            }
        });
        map.put(Double.TYPE, new ObjectTypeConvertor()
        {
            @Override
            public Object get(final String[] k)
            {
                if (k == null)
                {
                    return 0;
                }
                return Double.parseDouble(k[0]);
            }
        });
        map.put(String.class, new ObjectTypeConvertor()
        {

            @Override
            public Object get(final String[] k)
            {
                if (k == null)
                {
                    return null;
                }
                return k[0];
            }
        });
        map.put(Integer.TYPE, new ObjectTypeConvertor()
        {
            @Override
            public Object get(final String[] k)
            {
                if (k == null)
                {
                    return 0;
                }
                return Integer.parseInt(k[0]);
            }
        });
        map.put(Short.TYPE, new ObjectTypeConvertor()
        {
            @Override
            public Object get(final String[] k)
            {
                if (k == null)
                {
                    return 0;
                }
                return Short.parseShort(k[0]);
            }
        });
    }

    @Override
    public <T> List<WebProcess<T>> bind(final Class<? super T> type)
            throws SecurityException, NoSuchMethodException
    {
        final SkipList<WebProcess<T>> results = new SkipList<WebProcess<T>>();
        for (final Field field : type.getFields())
        {
            final WebParam param = field.getAnnotation(WebParam.class);
            if (param == null)
            {
                continue;
            }
            final String[] keys;
            if (param.value().length > 1)
            {
                throw new Error("WebParamアノテーションをフィールドに付与する際は、キーは一つしか使用出来ません");
            }
            if (param.value().length != 0)
            {
                keys = param.value();
            }
            else
            {
                keys = new String[] { field.getName() };
            }
            final ObjectTypeConvertor cnv = map.get(field.getType());
            results.add(new WebProcess<T>()
            {
                @Override
                public cc.aileron.web.WebProcess.Case process(final T resource)
                        throws Exception
                {
                    final String[][] args = args(keys);
                    field.set(resource, cnv.get(args[0]));
                    return Case.CONTINUE;
                }

            });
        }
        for (final Method method : type.getMethods())
        {
            final MethodInvoker<T> invoker = invoker(method);
            if (invoker == null)
            {
                continue;
            }
            final String[] keys = invoker.keys();
            final WebProcess<T> p = new WebProcess<T>()
            {
                @Override
                public cc.aileron.web.WebProcess.Case process(final T resource)
                        throws Exception
                {
                    final String[][] args = args(keys);
                    invoker.invoke(resource, args);
                    return Case.CONTINUE;
                }

                @Override
                public String toString()
                {
                    return String.format("parameter-bind:%s[%s]",
                            type,
                            $.join(",", keys));
                }
            };
            results.add(p);
        }
        return results;
    }

    String[][] args(final String[] keys)
    {
        final Map<String, Object> parameter = Wsgi.Request().parameter();
        final String[][] args = new String[keys.length][];
        for (int i = 0, size = args.length; i < size; i++)
        {
            final Object values = parameter.get(keys[i]);
            if (values == null)
            {
                args[i] = null;
                continue;
            }
            if (values.getClass().isArray())
            {
                args[i] = (String[]) values;
                continue;
            }
            args[i] = new String[] { (String) values };
        }
        return args;
    }

    private <T> MethodInvoker<T> invoker(final Method method)
            throws SecurityException, NoSuchMethodException
    {
        /*
         * 引数が無いメソッドは、対象外
         */
        if (method.getParameterTypes().length == 0)
        {
            return null;
        }

        /*
         * webパラムアノテーションが付与されているメソッドだけ対象
         */
        final WebParam methodParameter = method.getAnnotation(WebParam.class);
        if (methodParameter == null)
        {
            return null;
        }

        /*
         * webParamに、名前指定が有る場合
         */
        if (methodParameter.value().length != 0)
        {
            final String[] keys = methodParameter.value();
            if (method.getParameterTypes().length != keys.length)
            {
                throw new Error("WebParamの名前指定は、引数の数と同一では無いといけません");
            }
            return new MethodInvoker<T>(method)
            {
                @Override
                public String[] keys()
                {
                    return keys;
                }
            };
        }

        /*
         * webParamに、名前指定が無い場合
         */
        final String[] keys = new String[] { method.getName() };
        return new MethodInvoker<T>(method)
        {
            @Override
            public String[] keys()
            {
                return keys;
            }
        };
    }
}
