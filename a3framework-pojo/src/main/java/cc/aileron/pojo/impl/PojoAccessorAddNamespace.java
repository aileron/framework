package cc.aileron.pojo.impl;

import static cc.aileron.generic.$.*;
import cc.aileron.pojo.NoSuchPropertyException;
import cc.aileron.pojo.PojoAccessor;
import cc.aileron.pojo.PojoAccessorRepository;
import cc.aileron.pojo.PojoProperty;

/**
 * @author aileron
 */
public class PojoAccessorAddNamespace implements PojoAccessor<Object>
{
    @Override
    public PojoAccessor<Object> add(final Object object)
    {
        if (object == null)
        {
            throw new IllegalArgumentException("object is null");
        }
        return new PojoAccessorMix(repository, this, repository.from(object));
    }

    @Override
    public PojoAccessor<Object> add(final PojoAccessor<Object> object)
    {
        if (object == null)
        {
            throw new IllegalArgumentException("object is null");
        }
        return new PojoAccessorMix(repository, this, object);
    }

    @Override
    public PojoAccessor<Object> add(final String namespace, final Object object)
    {
        if (object == null)
        {
            throw new IllegalArgumentException("object is null");
        }
        return new PojoAccessorAddNamespace(repository,
                this,
                repository.from(object),
                namespace);
    }

    @Override
    public PojoAccessor<Object> add(final String namespace,
            final PojoAccessor<Object> object)
    {
        if (object == null)
        {
            throw new IllegalArgumentException("object is null");
        }
        return new PojoAccessorAddNamespace(repository, this, object, namespace);
    }

    @Override
    public PojoAccessorRepository repository()
    {
        return repository;
    }

    @Override
    public Object target()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterable<PojoProperty> to()
    {
        return self.to();
    }

    @Override
    public PojoProperty to(final String key) throws NoSuchPropertyException
    {
        final int idx = key.indexOf(namespace);
        if (idx >= 0)
        {
            if (key.length() == namespace.length())
            {
                return add.to("self");
            }
            return add.to(key.substring(idx + namespace.length() + 1));
        }

        try
        {
            return self.to(key);
        }
        catch (final NoSuchPropertyException e2)
        {
            NoSuchPropertyException p = e2;
            while (p.getCause() instanceof NoSuchPropertyException)
            {
                p = ((NoSuchPropertyException) p.getCause());
            }
            throw p;
        }

    }

    /**
     * @param repository
     * @param self
     * @param add
     * @param namespace
     */
    public PojoAccessorAddNamespace(final PojoAccessorRepository repository,
            final PojoAccessor<?> self, final PojoAccessor<?> add,
            final String namespace)
    {
        this.repository = repository;
        this.namespace = namespace;
        this.self = cast(self);
        this.add = cast(add);
    }

    final PojoAccessor<Object> add;
    final String namespace;
    final PojoAccessorRepository repository;
    final PojoAccessor<Object> self;
}