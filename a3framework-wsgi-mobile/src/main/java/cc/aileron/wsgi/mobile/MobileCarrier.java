package cc.aileron.wsgi.mobile;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

/**
 * @author aileron
 */
public enum MobileCarrier
{
    /**
     * AU
     */
    AU
    {
        @Override
        public String doctypePublic()
        {
            return "-//OPENWAVE//DTD XHTML 1.0//EN";
        }

        @Override
        public String doctypeSystem()
        {
            return "http://www.openwave.com/DTD/xhtml-basic.dtd";
        }

        @Override
        public String uid(final HttpServletRequest request)
        {
            return request.getHeader("X-Up-Subno");
        }

        @Override
        public boolean userAgent(final String userAgent)
        {
            return userAgent.indexOf("KDDI") != -1;
        }

    },

    /**
     * DOCOMO
     */
    DOCOMO
    {
        @Override
        public String contentType()
        {
            return "application/xhtml+xml";
        }

        @Override
        public String doctypePublic()
        {
            return "-//i-mode group (ja)//DTD XHTML i-XHTML(Locale/Ver.=ja/2.2) 1.0//EN";
        }

        @Override
        public String doctypeSystem()
        {
            return "i-xhtml_4ja_10.dtd";
        }

        @Override
        public boolean enableCssInline()
        {
            return true;
        }

        @Override
        public String uid(final HttpServletRequest request)
        {
            return request.getHeader("X-DCMGUID");
        }

        @Override
        public boolean userAgent(final String userAgent)
        {
            return userAgent.indexOf("DoCoMo") != -1;
        }

    },

    /**
     * OTHER
     */
    OTHER
    {
        @Override
        public boolean isMobile()
        {
            return false;
        }
    },

    /**
     * SOFTBANK
     */
    SOFTBANK
    {
        @Override
        public String doctypePublic()
        {
            return "-//J-PHONE//DTD XHTML Basic 1.0 Plus//EN";
        }

        @Override
        public String doctypeSystem()
        {
            return "xhtml-basic10-plus.dtd";
        }

        @Override
        public String uid(final HttpServletRequest request)
        {
            return request.getHeader("x-jphone-uid");
        }

        @Override
        public boolean userAgent(final String userAgent)
        {
            return userAgent.indexOf("SoftBank") != -1
                    || userAgent.indexOf("J-PHONE") != -1;
        }

    };

    /**
     * values
     */
    public static final MobileCarrier[] values = new MobileCarrier[] { AU,
            DOCOMO, SOFTBANK };

    static final Pattern defaultEmoji = Pattern.compile("&#x(.*?);");

    /**
     * @param doctype
     * @return {@link MobileCarrier}
     */
    public static MobileCarrier parseDocType(final String doctype)
    {
        for (final MobileCarrier carrier : values())
        {
            if (carrier.doctype() != null && carrier.doctype().equals(doctype))
            {
                return carrier;
            }
        }
        return OTHER;
    }

    /**
     * @param useragent
     * @return {@link MobileCarrier}
     */
    public static MobileCarrier parseUserAgent(final String useragent)
    {
        if (useragent == null || useragent.isEmpty())
        {
            return OTHER;
        }
        for (final MobileCarrier carrier : values())
        {
            if (carrier.userAgent(useragent))
            {
                return carrier;
            }
        }
        return OTHER;
    }

    private static String q(final String v)
    {
        return "\"" + v + "\"";
    }

    /**
     * @return contentType
     */
    public String contentType()
    {
        return "text/html";
    }

    /**
     * @return doctype
     */
    public final String doctype()
    {
        return "<!DOCTYPE " + doctypeMethod() + " PUBLIC " + q(doctypePublic())
                + " " + q(doctypeSystem()) + ">";
    }

    /**
     * @return method
     */
    public String doctypeMethod()
    {
        return "html";
    }

    /**
     * @return public
     */
    public String doctypePublic()
    {
        return "";
    }

    /**
     * @return system
     */
    public String doctypeSystem()
    {
        return "";
    }

    /**
     * @return emoji
     */
    public Pattern emoji()
    {
        return defaultEmoji;
    }

    /**
     * @return enableCssInline
     */
    public boolean enableCssInline()
    {
        return false;
    }

    /**
     * @return is mobile
     */
    public boolean isMobile()
    {
        return true;
    }

    /**
     * @param request
     * @return uid
     */
    public String uid(final HttpServletRequest request)
    {
        return null;
    }

    /**
     * @param userAgent
     * @return userAgent
     */
    public boolean userAgent(final String userAgent)
    {
        return false;
    }

    /**
     * @return xmlDeclaration
     */
    public String xmlDeclaration()
    {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    }
}