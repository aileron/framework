/**
 * 
 */
package cc.aileron.hier;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author aileron
 */
@Singleton
public class UrlTreeContainerDelimiterDefault implements
        UrlTreeContainerDelimiter
{
    @Override
    public String value()
    {
        return delimiter;
    }

    /**
     */
    @Inject
    public UrlTreeContainerDelimiterDefault()
    {
        this.delimiter = "[_/]";
    }

    /**
     * @param delimiter
     */
    public UrlTreeContainerDelimiterDefault(final String delimiter)
    {
        this.delimiter = delimiter;
    }

    private final String delimiter;
}
