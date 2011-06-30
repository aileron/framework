package cc.aileron.wsgi;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.aileron.generic.$;
import cc.aileron.generic.ObjectReference;
import cc.aileron.generic.Resource;
import cc.aileron.web.WebBinder;
import cc.aileron.web.WebProcess;
import cc.aileron.web.WebProcess.Case;
import cc.aileron.wsgi.Wsgi.Response.StreamWriteProcesser;

/**
 * Router
 */
public class Router extends Wsgi
{
    @Override
    public void execute(final Context context) throws Exception
    {
        /*
         * thread-local-context
         */
        Wsgi.Context(context);

        /*
         * forward の為のメインループ
         */
        while (true)
        {
            /*
             * request
             */
            if (logger.isTraceEnabled())
            {
                logger.trace("{} {}", new Object[] {
                        context.request().method(), context.request().path() });
            }

            /*
             * hier 取得
             */
            final WebBinder.Setting<Object> set = hier.get(context.request()
                    .method(), context.request().path(), context.request()
                    .parameter());

            /*
             * ディスパッチされていない場合は、ファイル出力
             */
            if (set == null)
            {
                localFile.execute(context);
                return;
            }

            final Object resource = set.resource();
            if (logger.isTraceEnabled())
            {
                logger.trace("{} {} ({}) parameter:{}", new Object[] {
                        context.request().method(), context.request().path(),
                        resource.getClass(), context.request().parameter() });
            }
            for (final WebProcess<Object> process : $.<List<WebProcess<Object>>> cast(set.process()))
            {
                final Case state;
                try
                {
                    state = process.process(resource);
                }
                catch (final Throwable throwable)
                {
                    logger.error(String.format("%s %s (%s) %s",
                            context.request().method(),
                            context.request().path(),
                            resource.getClass(),
                            process), throwable);
                    break;
                }
                if (logger.isTraceEnabled())
                {
                    logger.trace("{} {} ({}) {}", new Object[] {
                            context.request().method(),
                            context.request().path(), resource.getClass(),
                            process });
                }
                if (state == Case.RETRY)
                {
                    continue;
                }
                if (state == WebProcess.Case.TERMINATE)
                {
                    break;
                }
            }
            Wsgi.Context.remove();
            break;
        }
    }

    /**
     * @param hier
     * @throws Exception
     */
    public Router(final ObjectReference<WebBinder.Container> hier)
            throws Exception
    {
        this.hier = hier.get();
    }

    final WebBinder.Container hier;

    final Wsgi localFile = new Wsgi()
    {
        @Override
        public void execute(final Wsgi.Context context) throws Exception
        {
            final Resource resource = Resource.Loader.get(context.request()
                    .path());
            context.response().out(new StreamWriteProcesser()
            {
                @Override
                public void write(final OutputStream output) throws IOException
                {
                    final ReadableByteChannel inputChannel = Channels.newChannel(resource.toStream());
                    final WritableByteChannel outputChannel = Channels.newChannel(output);
                    final ByteBuffer buffer = local.get();
                    while (inputChannel.read(buffer) > 0)
                    {
                        buffer.flip();
                        outputChannel.write(buffer);
                    }
                }
            });
        }

        ThreadLocal<ByteBuffer> local = new ThreadLocal<ByteBuffer>()
        {
            @Override
            protected ByteBuffer initialValue()
            {
                return ByteBuffer.allocate(1024);
            }
        };
    };
    final Logger logger = LoggerFactory.getLogger(this.getClass());
}
