/**
 * 
 */
package cc.aileron.web;

import java.util.List;
import java.util.Map;

import cc.aileron.wsgi.Wsgi.Method;

/**
 * 対象クラスを、HTTPリクエストにバインドする
 */
public interface WebBinder
{
    /**
     * @author aileron
     * 
     * @param <T>
     */
    interface Bind<T>
    {
        /**
         * @param method
         * @param uri
         * @return {@link Setting}
         * @throws NoSuchMethodException
         * @throws SecurityException
         */
        Setting<T> to(Method method, String uri)
                throws SecurityException, NoSuchMethodException;

        /**
         * @param method
         * @param uri
         * @param overrideKey
         * @return {@link Setting}
         * @throws NoSuchMethodException
         * @throws SecurityException
         */
        Setting<T> to(Method method, String uri, String overrideKey)
                throws SecurityException, NoSuchMethodException;
    }

    /**
     * プロセスコンテナ
     */
    interface Container
    {
        /**
         * @param method
         * @param uri
         * @param parameter
         * @return {@link ProcessSet}
         */
        WebBinder.Setting<Object> get(Method method, String uri,
                Map<String, Object> parameter);

        /**
         * @param method
         * @param uri
         * @param setting
         */
        void set(Method method, String uri, Setting<?> setting);

        /**
         * @param method
         * @param uri
         * @param overrideKey
         * @param setting
         */
        void set(Method method, String uri, String overrideKey,
                Setting<?> setting);

    }

    /**
     * 処理セット
     * 
     * @param <T>
     */
    interface ProcessSet<T>
    {

        /**
         * @return process
         */
        List<WebProcess<? super T>> process();

        /**
         * @return resource
         */
        Class<T> resource();
    }

    /**
     * 設定
     * 
     * @param <T>
     */
    interface Setting<T>
    {
        /**
         * @return process
         */
        List<WebProcess<? super T>> process();

        /**
         * @param process
         * @return {@link Setting}
         */
        Setting<T> process(final Class<? extends WebProcess<? super T>> process);

        /**
         * @param process
         * @return {@link Setting}
         */
        Setting<T> process(final WebProcess<? super T> process);

        /**
         * @return resource
         */
        T resource();

    }

    /**
     * @param <T>
     * @param type
     * @return {@link Bind}
     */
    <T> Bind<T> bind(Class<T> type);
}
