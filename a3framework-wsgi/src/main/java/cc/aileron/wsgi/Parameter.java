/**
 * 
 */
package cc.aileron.wsgi;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.aileron.generic.ObjectProvider;
import cc.aileron.generic.util.URLTranslator;

/**
 * リクエストパラメータの処理
 */
public class Parameter
{
    static class StrMapHelper
    {
        void add(final String key, final String value)
        {
            final Object object = result.get(key);
            if (object == null)
            {
                result.put(key, value);
                return;
            }
            if (object instanceof String)
            {
                result.put(key, new String[] { (String) object, value });
                return;
            }
            if (object instanceof String[])
            {
                final String[] array = (String[]) object;
                final int length = array.length + 1;
                final String[] newArray = new String[length];
                System.arraycopy(array, 0, newArray, 0, length);
                result.put(key, newArray);
            }
        }

        public StrMapHelper(final Map<String, Object> result)
        {
            this.result = result;
        }

        private final Map<String, Object> result;
    }

    static final Logger logger = LoggerFactory.getLogger(Parameter.class);

    static final URLTranslator TRANSLATOR = URLTranslator.factory.get(Charset.forName("UTF-8"));

    /**
     * @param request
     * @return parameter
     */
    public static ObjectProvider<String, String> getSimpleParamter(
            final Wsgi.Request request)
    {
        final Map<String, Object> param = request.parameter();
        return new ObjectProvider<String, String>()
        {
            @Override
            public String get(final String key)
            {
                final Object value = param.get(key);
                if (value instanceof String)
                {
                    return (String) value;
                }
                if (value instanceof String[])
                {
                    return ((String[]) value)[0];
                }
                return null;
            }
        };
    }

    /**
     * @param query
     * @param content
     * @return parameter
     * @throws IOException
     */
    public static Map<String, Object> post(final String query,
            final Wsgi.Request.Content content) throws IOException
    {
        final Map<String, Object> result = query(query);
        final StrMapHelper strmap = new StrMapHelper(result);
        if (content.type().equals("application/x-www-form-urlencoded"))
        {
            final byte[] rawdata = new byte[content.length()];
            final int readLength = content.stream().read(rawdata);

            final String data = new String(rawdata);

            logger.debug("readLength:{}", readLength);
            logger.debug(data);

            if (data.isEmpty())
            {
                return result;
            }

            for (final String token : data.split("&"))
            {
                logger.debug("token:{}", token);

                final String[] kv = token.split("=");
                strmap.add(kv[0], kv.length == 2 ? TRANSLATOR.decode(kv[1])
                        : null);
            }
        }
        return result;
    }

    /**
     * URLエンコード形式
     * 
     * @param query
     * @return paraemter
     */
    public static Map<String, Object> query(final String query)
    {
        final Map<String, Object> result = new HashMap<String, Object>();
        if (query.isEmpty())
        {
            return result;
        }

        final StrMapHelper strmap = new StrMapHelper(result);
        for (final String token : query.split("&"))
        {
            final String[] kv = token.split("=");
            strmap.add(kv[0], TRANSLATOR.decode(kv[1]));
        }
        return result;
    }
}
