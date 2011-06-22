/**
 * 
 */
package cc.aileron.pojo.impl;

import static cc.aileron.generic.$.*;
import cc.aileron.generic.util.SoftHashMap;
import cc.aileron.pojo.meta.PojoPropertiesMeta;
import cc.aileron.pojo.meta.PojoPropertiesMetaRepository;

/**
 * @author aileron
 * 
 */
public class PojoPropertiesMetaRepositoryImpl implements
        PojoPropertiesMetaRepository
{
    @Override
    public synchronized <T> PojoPropertiesMeta<T> get(final Class<T> target)
    {
        final PojoPropertiesMeta<T> meta = cast(map.get(target));
        if (meta != null)
        {
            return meta;
        }
        final PojoPropertiesMetaImpl<T> newmeta = new PojoPropertiesMetaImpl<T>(target);
        map.put(target, newmeta);
        return newmeta;
    }

    final SoftHashMap<Class<? extends Object>, PojoPropertiesMeta<? extends Object>> map = new SoftHashMap<Class<? extends Object>, PojoPropertiesMeta<? extends Object>>();
}
