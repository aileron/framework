/**
 * 
 */
package cc.aileron.web.phase;

import java.io.OutputStream;

import cc.aileron.web.WebProcess;
import cc.aileron.wsgi.Wsgi;
import cc.aileron.wsgi.Wsgi.Response.StreamWriteProcesser;

/**
 * @author aileron
 */
public interface OutputStreamPhase
{
    /**
     * @author aileron
     */
    class Executor implements WebProcess<OutputStreamPhase>
    {
        @Override
        public cc.aileron.web.WebProcess.Case process(
                final OutputStreamPhase resource) throws Exception
        {
            Wsgi.Response(new StreamWriteProcesser()
            {
                @Override
                public void write(final OutputStream stream) throws Exception
                {
                    resource.output(stream);
                }
            });
            return Case.TERMINATE;
        }
    }

    /**
     * HTTP Header
     */
    class Header
    {
        /**
         * @param name
         * @param value
         */
        public Header(final String name, final String value)
        {
            this.name = name;
            this.value = value;
        }

        /**
         * key
         */
        public final String name;

        /**
         * value
         */
        public final String value;
    }

    /**
     * @return {@link Header}
     */
    Header[] headers();

    /**
     * @param stream
     * @throws Exception
     */
    void output(OutputStream stream) throws Exception;
}
