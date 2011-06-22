/**
 * 
 */
package cc.aileron.web.phase;

import cc.aileron.web.WebProcess;

/**
 * @author aileron
 */
public interface UpdatePhase
{
    /**
     * @author aileron
     */
    class Executor implements WebProcess<UpdatePhase>
    {
        @Override
        public cc.aileron.web.WebProcess.Case process(final UpdatePhase resource)
                throws Exception
        {
            resource.update();
            return Case.CONTINUE;
        }

    }

    /**
     * @throws Exception
     */
    void update() throws Exception;
}
