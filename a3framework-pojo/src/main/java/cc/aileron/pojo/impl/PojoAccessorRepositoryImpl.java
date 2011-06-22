/**
 * 
 */
package cc.aileron.pojo.impl;

import static cc.aileron.generic.$.*;
import cc.aileron.pojo.PojoAccessor;
import cc.aileron.pojo.PojoAccessorRepository;
import cc.aileron.pojo.meta.PojoPropertiesMetaRepository;
import cc.aileron.pojo.type.TypeConvertor;
import cc.aileron.pojo.type.TypeConvertorImpl;

/**
 * @author aileron
 */
public class PojoAccessorRepositoryImpl implements PojoAccessorRepository
{
    /*
     * (non-Javadoc)
     * 
     * @see cc.aileron.pojo.PojoAccessorRepository#from(java.lang.Object)
     */
    @Override
    public <T> PojoAccessor<T> from(final T object)
    {
        final Class<T> type = cast(object.getClass());
        return new PojoAccessorImpl<T>(type, object, this, meta, convert);
    }

    protected PojoAccessorRepositoryImpl()
    {
    }

    final TypeConvertor convert = new TypeConvertorImpl();
    final PojoPropertiesMetaRepository meta = new PojoPropertiesMetaRepositoryImpl();
}
