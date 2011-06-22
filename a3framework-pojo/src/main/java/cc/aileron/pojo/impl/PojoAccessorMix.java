package cc.aileron.pojo.impl;

import static cc.aileron.generic.$.*;
import cc.aileron.generic.$;
import cc.aileron.pojo.NoSuchPropertyError;
import cc.aileron.pojo.NoSuchPropertyException;
import cc.aileron.pojo.PojoAccessor;
import cc.aileron.pojo.PojoAccessorRepository;
import cc.aileron.pojo.PojoProperty;

/**
 * @author aileron
 */
public class PojoAccessorMix implements PojoAccessor<Object>
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
        try
        {
            return add.to();
        }
        catch (final NoSuchPropertyError e1)
        {
            try
            {
                return self.to();
            }
            catch (final NoSuchPropertyError e2)
            {
                throw e1;
            }
        }
    }

    @Override
    public PojoProperty to(final String key) throws NoSuchPropertyException
    {
        try
        {
            return add.to(key);
        }
        catch (final NoSuchPropertyException e1)
        {
            try
            {
                return self.to(key);
            }
            catch (final NoSuchPropertyException e2)
            {
                throw new NoSuchPropertyException(key, $.add(e1.target,
                        e2.target));
            }
        }
    }

    /**
     * @param repository
     * @param self
     * @param add
     */
    public PojoAccessorMix(final PojoAccessorRepository repository,
            final PojoAccessor<?> self, final PojoAccessor<?> add)
    {
        this.repository = repository;
        this.self = cast(self);
        this.add = cast(add);
    }

    final PojoAccessor<Object> add;
    final PojoAccessorRepository repository;
    final PojoAccessor<Object> self;
}