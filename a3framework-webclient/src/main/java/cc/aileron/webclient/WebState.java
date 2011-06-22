/**
 * 
 */
package cc.aileron.webclient;

import java.net.HttpCookie;
import java.util.List;

/**
 * クッキー等の状態を保持
 * 
 * @author aileron
 */
public interface WebState
{
    /**
     * @param domain
     * @return List<HttpCookie>
     */
    List<HttpCookie> domain(String domain);
}
