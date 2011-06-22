/**
 * 
 */
package cc.aileron.pojo.impl;

import java.util.Iterator;

import cc.aileron.generic.$;
import cc.aileron.generic.ObjectProvider;
import cc.aileron.generic.ObjectReference;
import cc.aileron.pojo.NoSuchPropertyException;
import cc.aileron.pojo.PojoAccessor;
import cc.aileron.pojo.PojoAccessorRepository;
import cc.aileron.pojo.PojoProperty;
import cc.aileron.pojo.meta.PojoPropertiesMeta;
import cc.aileron.pojo.meta.PojoPropertiesMetaRepository;
import cc.aileron.pojo.meta.PojoPropertyFetcher;
import cc.aileron.pojo.type.TypeConvertor;

/**
 * @author aileron
 * @param <T>
 */
public class PojoAccessorImpl<T> implements PojoAccessor<T>
{
    @Override
    public PojoAccessor<Object> add(final Object object)
    {
        if (object == null)
        {
            throw new IllegalArgumentException("object is null");
        }
        return new PojoAccessorMix(accessorRepository,
                this,
                accessorRepository.from(object));
    }

    @Override
    public PojoAccessor<Object> add(final PojoAccessor<Object> object)
    {
        if (object == null)
        {
            throw new IllegalArgumentException("object is null");
        }
        return new PojoAccessorMix(accessorRepository, this, object);
    }

    @Override
    public PojoAccessor<Object> add(final String namespace, final Object object)
    {
        if (object == null)
        {
            throw new IllegalArgumentException("object is null");
        }
        return new PojoAccessorAddNamespace(accessorRepository,
                this,
                accessorRepository.from(object),
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
        return new PojoAccessorAddNamespace(accessorRepository,
                this,
                object,
                namespace);
    }

    @Override
    public PojoAccessorRepository repository()
    {
        return accessorRepository;
    }

    @Override
    public T target()
    {
        return target;
    }

    @Override
    public Iterable<PojoProperty> to()
    {
        final PojoAccessor<T> self = this;
        return new Iterable<PojoProperty>()
        {
            @Override
            public Iterator<PojoProperty> iterator()
            {
                final ObjectReference<String> keys = $.iterate(meta.keys()
                        .iterator());
                return $.iterate(new ObjectReference<PojoProperty>()
                {
                    @Override
                    public PojoProperty get()
                    {
                        try
                        {
                            return self.to(keys.get());
                        }
                        catch (final NoSuchPropertyException e)
                        {
                            throw e.error();
                        }
                    }
                });
            }
        };
    }

    @Override
    public PojoProperty to(final String key) throws NoSuchPropertyException
    {
        return fetcher.fetch(key);
    }

    /**
     * @param type
     * @param target
     * @param accessorRepository
     * @param metaRepository
     * @param convertor
     */
    public PojoAccessorImpl(final Class<T> type, final T target,
            final PojoAccessorRepository accessorRepository,
            final PojoPropertiesMetaRepository metaRepository,
            final TypeConvertor convertor)
    {
        this.type = type;
        this.target = target;
        this.accessorRepository = accessorRepository;
        this.convertor = convertor;
        this.meta = metaRepository.get(type);
        this.func = new ObjectProvider<Object, PojoPropertyFetcher>()
        {
            @Override
            public PojoPropertyFetcher get(final Object p)
            {
                return new PojoPropertyFetcherImpl(p,
                        accessorRepository,
                        metaRepository,
                        convertor);
            }
        };
        this.fetcher = func.get(target);
    }

    final PojoAccessorRepository accessorRepository;
    final TypeConvertor convertor;
    final PojoPropertyFetcher fetcher;
    final ObjectProvider<Object, PojoPropertyFetcher> func;
    final PojoPropertiesMeta<T> meta;
    final T target;
    final Class<T> type;
}
