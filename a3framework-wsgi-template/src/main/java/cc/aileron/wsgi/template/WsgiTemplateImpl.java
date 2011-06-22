/**
 * 
 */
package cc.aileron.wsgi.template;

import java.io.PrintWriter;

import javax.inject.Singleton;

import cc.aileron.generic.$;
import cc.aileron.generic.ObjectProvider;
import cc.aileron.generic.Resource;
import cc.aileron.generic.error.NotFoundException;
import cc.aileron.pojo.PojoAccessor;
import cc.aileron.template.Template;
import cc.aileron.web.WebProcess;
import cc.aileron.wsgi.Wsgi;
import cc.aileron.wsgi.Wsgi.Response.PrintWriterProcesser;

import com.google.inject.Stage;

/**
 * @author aileron
 */
@Singleton
public class WsgiTemplateImpl implements WsgiTemplate
{

    @Override
    public ContentType get(final ObjectProvider<Resource, Template> repository)
    {
        return get(repository, new Object());
    }

    @Override
    public ContentType get(final ObjectProvider<Resource, Template> repository,
            final Object global)
    {
        return new ContentType()
        {
            @Override
            public Path type(final String contentType)
            {
                return new Path()
                {
                    @Override
                    public WebProcess<Object> path(final String path)
                            throws NotFoundException
                    {
                        final Template template;
                        if (isDev == false)
                        {
                            template = repository.get($.some(Resource.Loader.get(path)));
                        }
                        else
                        {
                            template = null;
                        }

                        return new WebProcess<Object>()
                        {
                            @Override
                            public Case process(final Object resource)
                                    throws Exception
                            {
                                final Template out;
                                if (template != null)
                                {
                                    out = template;
                                }
                                else
                                {
                                    out = repository.get($.some(Resource.Loader.get(path)));
                                }

                                final PojoAccessor<Object> object = PojoAccessor.Repository.from(global)
                                        .add(resource);
                                Wsgi.Response()
                                        .header()
                                        .set("Content-Type", contentType);
                                Wsgi.Response(new PrintWriterProcesser()
                                {
                                    @Override
                                    public void write(final PrintWriter writer)
                                    {
                                        out.print(object, writer);
                                    }
                                });

                                return Case.TERMINATE;
                            }

                            @Override
                            public String toString()
                            {
                                return "template[" + path + "]";
                            }

                        };
                    }
                };
            }
        };
    }

    /**
     * @param stage
     */
    @javax.inject.Inject
    public WsgiTemplateImpl(final Stage stage)
    {
        isDev = stage == Stage.DEVELOPMENT;
    }

    final boolean isDev;
}
