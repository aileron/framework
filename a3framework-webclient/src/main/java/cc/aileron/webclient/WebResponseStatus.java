/**
 * 
 */
package cc.aileron.webclient;

/**
 * @author aileron
 */
public interface WebResponseStatus
{
    /**
     * @return protocolVersion
     */
    String protocolVersion();

    /**
     * @return reasonPhrase
     */
    String reasonPhrase();

    /**
     * @return statusCode
     */
    int statusCode();
}
