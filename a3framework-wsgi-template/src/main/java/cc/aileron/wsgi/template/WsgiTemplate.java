/**
 * 
 */
package cc.aileron.wsgi.template;

import cc.aileron.generic.ObjectProvider;
import cc.aileron.generic.Resource;
import cc.aileron.generic.error.NotFoundException;
import cc.aileron.template.Template;
import cc.aileron.web.WebProcess;

import com.google.inject.ImplementedBy;

/**
 * WsgiTemplate インスタンスを取得します
 * 
 * @author aileron
 */
@ImplementedBy(WsgiTemplateImpl.class)
public interface WsgiTemplate
{

    /**
     * コンテントタイプを指定します
     * 
     * @author aileron
     * 
     */
    interface ContentType
    {
        /**
         * @param contentType
         * @return {@link Path}
         */
        Path type(String contentType);
    }

    /**
     * file-pathを指定します
     * 
     * @author aileron
     */
    interface Path
    {
        /**
         * @param path
         * @return {@link WebProcess}
         * @throws NotFoundException
         */
        WebProcess<Object> path(String path) throws NotFoundException;
    }

    /**
     * @param repository
     * @return {@link ContentType}
     */
    ContentType get(ObjectProvider<Resource, Template> repository);

    /**
     * @param repository
     * @param global
     * @return {@link ContentType}
     */
    ContentType get(ObjectProvider<Resource, Template> repository, Object global);
}
