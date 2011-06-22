/**
 * 
 */
package cc.aileron.web;

/**
 * Configure
 * 
 * @author aileron
 */
public interface WebConfigure
{
    /**
     * @param binder
     * @throws Exception
     */
    void configure(WebBinder binder) throws Exception;
}
