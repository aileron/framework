/**
 * 
 */
package cc.aileron.web.process;

import cc.aileron.web.WebProcess;
import cc.aileron.wsgi.Wsgi;

/**
 * リソースインスタンスを、requestアトリジュートにbindします
 */
public class ReqestAttributeBind implements WebProcess<Object>
{
    @Override
    public cc.aileron.web.WebProcess.Case process(final Object resource)
            throws Exception
    {
        Wsgi.Request().attributes().put(name, resource);
        return Case.CONTINUE;
    }

    /**
     * @param name
     */
    public ReqestAttributeBind(final String name)
    {
        this.name = name;
    }

    private final String name;
}
