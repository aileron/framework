/**
 * 
 */
package cc.aileron.pojo;

/**
 * @author aileron
 * @param <T>
 */
public interface PojoAccessor<T>
{
    /**
     * カテゴリ
     */
    enum Category
    {
        GET, SET
    }

    /**
     * @param object
     * @return {@link PojoAccessor}
     */
    PojoAccessor<Object> add(Object object);

    /**
     * @param object
     * @return {@link PojoAccessor}
     */
    PojoAccessor<Object> add(PojoAccessor<Object> object);

    /**
     * @param namespace
     * @param object
     * @return {@link PojoAccessor}
     */
    PojoAccessor<Object> add(String namespace, Object object);

    /**
     * @param namespace
     * @param object
     * @return {@link PojoAccessor}
     */
    PojoAccessor<Object> add(String namespace, PojoAccessor<Object> object);

    /**
     * @return {@link PojoAccessorRepository}
     */
    PojoAccessorRepository repository();

    /**
     * @return T
     */
    T target();

    /**
     * @return to
     */
    Iterable<PojoProperty> to();

    /**
     * @param key
     * @return {@link PojoProperty} 存在しない場合は、null
     * @throws NoSuchPropertyException
     */
    PojoProperty to(String key) throws NoSuchPropertyException;

    /**
     * pojoAccessorRepository
     */
    PojoAccessorRepository Repository = PojoAccessorRepository.Factory.get();
}
