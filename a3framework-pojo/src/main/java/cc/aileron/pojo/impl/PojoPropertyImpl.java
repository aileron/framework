package cc.aileron.pojo.impl;

import static cc.aileron.generic.$.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Iterator;

import cc.aileron.generic.$;
import cc.aileron.generic.ObjectReference;
import cc.aileron.pojo.NoSuchPropertyError;
import cc.aileron.pojo.PojoAccessor;
import cc.aileron.pojo.PojoAccessor.Category;
import cc.aileron.pojo.PojoAccessorRepository;
import cc.aileron.pojo.PojoProperty;
import cc.aileron.pojo.meta.PojoAccessorGetter;
import cc.aileron.pojo.meta.PojoAccessorSetter;
import cc.aileron.pojo.type.TypeConvertor;

/**
 * @author aileron
 */
public final class PojoPropertyImpl implements PojoProperty
{
    @Override
    public PojoAccessor<Object> accessor()
    {
        final Object object = getter.get();
        if (object == null)
        {
            return null;
        }
        return repository.from(object);
    }

    @Override
    public <E> Iterable<PojoAccessor<E>> accessorIterable(final Class<E> type)
    {
        final Object iterable = get(Object.class);
        if (iterable == null)
        {
            return Collections.emptyList();
        }
        if (iterable instanceof Iterable == false)
        {
            throw new ClassCastException(toString());
        }
        return new Iterable<PojoAccessor<E>>()
        {
            @Override
            public Iterator<PojoAccessor<E>> iterator()
            {
                final Iterator<E> ite = $.<Iterable<E>> cast(iterable)
                        .iterator();
                return iterate(new ObjectReference<PojoAccessor<E>>()
                {
                    @Override
                    public PojoAccessor<E> get()
                    {
                        if (!ite.hasNext())
                        {
                            return null;
                        }
                        return repository.from(ite.next());
                    }
                });
            }
        };
    }

    @Override
    public boolean exist(final PojoAccessor.Category category)
    {
        switch (category)
        {
        case GET:
            return getter != null;
        case SET:
            return setter != null;
        }
        throw new Error("case式に未実装");
    }

    @Override
    public Type genericType(final Category category)
    {
        switch (category)
        {
        case GET:
            return getter == null ? null : getter.genericType();
        case SET:
            return setter == null ? null : setter.genericType();
        default:
            throw new Error("実装もれ");
        }
    }

    @Override
    public Object get()
    {
        return getter.get();
    }

    @Override
    public <E> E get(final Class<E> type)
    {
        if (type == null)
        {
            throw new IllegalArgumentException("type is null");
        }
        if (getter == null)
        {
            throw new NoSuchPropertyError(name, target);
        }
        final Object result = getter.get();
        return $.<E> cast(convertor.convert(result,
                getter.resultType(),
                type));
    }

    @Override
    public <E> E get(final Key<E> type)
    {
        if (type == null)
        {
            throw new IllegalArgumentException("type is null");
        }
        if (getter == null)
        {
            throw new NoSuchPropertyError(name, target);
        }
        final Object result = getter.get();
        return $.<E> cast(result);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> Iterable<E> iterable(final Class<E> type)
    {
        final Object object = get(Object.class);
        if (object == null)
        {
            return Collections.emptyList();
        }
        if (object.getClass().isArray())
        {
            return iterate((E[]) object);
        }
        if (object instanceof Iterable == false)
        {
            throw new ClassCastException(toString());
        }
        return cast(object);
    }

    @Override
    public void set(final Object value) throws InvocationTargetException
    {
        if (setter == null)
        {
            throw new NoSuchPropertyError(name, target);
        }
        try
        {
            if (value == null && setter.argumentType() == Boolean.TYPE)
            {
                setter.set(false);
                return;
            }
            if (value == null && setter.argumentType().isPrimitive())
            {
                setter.set(0);
                return;
            }
            if (value == null)
            {
                setter.set(null);
                return;
            }
            final Object object = convertor.convert(value,
                    value.getClass(),
                    setter.argumentType());
            setter.set(object);
        }
        catch (final IllegalAccessException e)
        {
            throw new Error(e);
        }
    }

    @Override
    public String toString()
    {
        return target.getClass().getName() + "@" + name;
    }

    @Override
    public Class<?> type(final Category category)
    {
        switch (category)
        {
        case GET:
            return getter == null ? null : getter.resultType();
        case SET:
            return setter == null ? null : setter.argumentType();
        default:
            throw new Error("実装もれ");
        }
    }

    /**
     * @param target
     * @param name
     * @param repository
     * @param convertor
     * @param getter
     * @param setter
     */
    public PojoPropertyImpl(final PojoAccessorRepository repository,
            final TypeConvertor convertor, final Object target,
            final String name, final PojoAccessorGetter getter,
            final PojoAccessorSetter setter)
    {
        this.repository = repository;
        this.convertor = convertor;
        this.target = target;
        this.name = name;
        this.getter = getter;
        this.setter = setter;
    }

    final TypeConvertor convertor;
    final PojoAccessorGetter getter;
    final String name;
    final PojoAccessorRepository repository;
    final PojoAccessorSetter setter;
    final Object target;
}
