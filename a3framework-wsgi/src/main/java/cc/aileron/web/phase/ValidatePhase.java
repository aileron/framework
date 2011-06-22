/**
 * 
 */
package cc.aileron.web.phase;

import cc.aileron.web.WebProcess;

/**
 * @author aileron
 */
public interface ValidatePhase
{
    /**
     * @author aileron
     */
    class Executor implements WebProcess<ValidatePhase>
    {
        @Override
        public cc.aileron.web.WebProcess.Case process(
                final ValidatePhase resource) throws Exception
        {
            if (resource.validate())
            {
                return Case.CONTINUE;
            }
            return Case.TERMINATE;
        }

    }

    /**
     * @return validate
     */
    boolean validate();
}
