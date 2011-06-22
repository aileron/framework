/**
 * 
 */
package cc.aileron.pojo.impl;

import static cc.aileron.generic.$.*;
import cc.aileron.pojo.NoSuchPropertyException;
import cc.aileron.pojo.PojoAccessorRepository;
import cc.aileron.pojo.PojoProperty;
import cc.aileron.pojo.meta.PojoAccessorGetter;
import cc.aileron.pojo.meta.PojoAccessorSetter;
import cc.aileron.pojo.meta.PojoPropertiesMeta;
import cc.aileron.pojo.meta.PojoPropertiesMetaRepository;
import cc.aileron.pojo.meta.PojoPropertyFetcher;
import cc.aileron.pojo.type.TypeConvertor;

/**
 * @author aileron
 */
public class PojoPropertyFetcherImpl implements PojoPropertyFetcher
{
    @Override
    public PojoProperty fetch(final String key) throws NoSuchPropertyException
    {
        Object self = target;
        PojoPropertiesMeta<Object> meta = meta(self.getClass());

        final String[] names = key.split("\\.");
        final int last = names.length - 1;
        for (int i = 0; i < last; i++)
        {
            final String name = names[i];
            final PojoAccessorGetter getter = meta.get(self, name);
            if (getter == null)
            {
                throw new NoSuchPropertyException(name, self);
            }
            meta = meta(getter.resultType());
            self = getter.get();
            if (self == null)
            {
                return null;
            }
        }

        final String name = names[last];
        final PojoAccessorSetter setter = meta.set(self, name);
        final PojoAccessorGetter getter = meta.get(self, name);
        if (getter == null && setter == null)
        {
            throw new NoSuchPropertyException(name, self);
        }
        return new PojoPropertyImpl(accessorRepository,
                convertor,
                self,
                name,
                getter,
                setter);

    }

    private PojoPropertiesMeta<Object> meta(final Class<?> type)
    {
        return cast(repository.get(type));
    }

    /**
     * @param target
     * @param accessorRepository
     * @param repository
     * @param convertor
     */
    public PojoPropertyFetcherImpl(final Object target,
            final PojoAccessorRepository accessorRepository,
            final PojoPropertiesMetaRepository repository,
            final TypeConvertor convertor)
    {
        this.target = target;
        this.accessorRepository = accessorRepository;
        this.repository = repository;
        this.convertor = convertor;
    }

    final PojoAccessorRepository accessorRepository;
    final TypeConvertor convertor;
    final PojoPropertiesMetaRepository repository;
    final Object target;
}
