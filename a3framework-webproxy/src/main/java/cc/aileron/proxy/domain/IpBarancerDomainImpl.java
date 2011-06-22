/**
 * 
 */
package cc.aileron.proxy.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * @author aileron
 */
public class IpBarancerDomainImpl implements IpBalancerDomain
{
    /**
     * @author aileron
     */
    static class AccesseLog extends HashMap<String, Long>
    {
        private static final long serialVersionUID = 1L;
    }

    @Override
    public String get(final String hostName)
    {
        final long current = System.currentTimeMillis();
        for (int i = 0; i < gatewaysLength; i++)
        {
            final Long _time = accessedLogs[i].get(hostName);
            final long time = _time == null ? 0 : _time;
            if (time < current)
            {
                accessedLogs[i].put(hostName, current + interval);
                return gateways[i];
            }
        }
        return null;
    }

    /**
     * @param interval
     * @param gateways
     */
    public IpBarancerDomainImpl(final long interval, final String... gateways)
    {
        this.interval = interval;
        this.gateways = gateways;
        this.accessedLogs = new AccesseLog[gateways.length];
        this.gatewaysLength = gateways.length;
        for (int i = 0; i < gatewaysLength; i++)
        {
            accessedLogs[i] = new AccesseLog();
        }
    }

    final Map<String, Long>[] accessedLogs;
    final String[] gateways;
    final int gatewaysLength;
    final long interval;
}
