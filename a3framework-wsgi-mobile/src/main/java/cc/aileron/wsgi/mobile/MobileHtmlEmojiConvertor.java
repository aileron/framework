/**
 *
 */
package cc.aileron.wsgi.mobile;

import java.util.Map;

import com.google.inject.ImplementedBy;

/**
 * @author aileron
 */
@ImplementedBy(MobileHtmlEmojiConvertorImpl.class)
public interface MobileHtmlEmojiConvertor
{
    /**
     * @author aileron
     */
    enum Emoji
    {
        AU, DOCOMO, SOFTBANK, TEXT
    }

    /**
     * @param org
     * @param dist
     * @param html
     * @return convert-html
     */
    String convert(MobileCarrier org, MobileCarrier dist, String html);

    /**
     * @param carrier
     * @param code
     * @return map
     */
    Map<Emoji, String> get(MobileCarrier carrier, String code);
}
