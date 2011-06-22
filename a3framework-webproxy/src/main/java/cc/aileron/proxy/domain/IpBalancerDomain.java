/**
 * 
 */
package cc.aileron.proxy.domain;

/**
 * IP分散に関する問題領域
 * 
 * @author aileron
 */
public interface IpBalancerDomain
{
    /**
     * @param hostName
     * @return ipaddress
     */
    String get(String hostName);
}
