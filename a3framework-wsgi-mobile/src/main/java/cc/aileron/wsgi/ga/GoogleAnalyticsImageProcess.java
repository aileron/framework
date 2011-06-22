/**
 * 
 */
package cc.aileron.wsgi.ga;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;

import cc.aileron.generic.ObjectProvider;
import cc.aileron.generic.Resource;
import cc.aileron.web.WebProcess;
import cc.aileron.wsgi.Parameter;
import cc.aileron.wsgi.Wsgi;
import cc.aileron.wsgi.Wsgi.Request;
import cc.aileron.wsgi.Wsgi.Response;

/**
 * @author aileron
 */
public class GoogleAnalyticsImageProcess implements WebProcess<Object>
{
    private static final String COOKIE_NAME = "__utmmobile";

    // The path the cookie will be available to, edit this to use a different
    // cookie path.
    private static final String COOKIE_PATH = "/";

    // Two years in seconds.
    private static final int COOKIE_USER_PERSISTENCE = 63072000;

    /**
     * Copyright 2009 Google Inc. All Rights Reserved.
     **/

    // Tracker version.
    private static final String version = "4.4sj";

    // The last octect of the IP address is removed to anonymize the user.
    private static String getIP(final String remoteAddress)
    {
        if (isEmpty(remoteAddress))
        {
            return "";
        }
        // Capture the first three octects of the IP address and replace the
        // forth
        // with 0, e.g. 124.455.3.123 becomes 124.455.3.0
        final String regex = "^([^.]+\\.[^.]+\\.[^.]+\\.).*";
        final Pattern getFirstBitOfIPAddress = Pattern.compile(regex);
        final Matcher m = getFirstBitOfIPAddress.matcher(remoteAddress);
        if (m.matches())
        {
            return m.group(1) + "0";
        }
        return "";
    }

    // Get a random number string.
    private static String getRandomNumber()
    {
        return Integer.toString((int) (Math.random() * 0x7fffffff));
    }

    // Generate a visitor id for this hit.
    // If there is a visitor id in the cookie, use that, otherwise
    // use the guid if we have one, otherwise use a random number.
    private static String getVisitorId(final String guid, final String account,
            final String userAgent, final Cookie cookie)
            throws NoSuchAlgorithmException, UnsupportedEncodingException
    {

        // If there is a value in the cookie, don't change it.
        if (cookie != null && cookie.getValue() != null)
        {
            return cookie.getValue();
        }

        String message;
        if (!isEmpty(guid))
        {
            // Create the visitor id using the guid.
            message = guid + account;
        }
        else
        {
            // otherwise this is a new user, create a new random id.
            message = userAgent + getRandomNumber()
                    + UUID.randomUUID().toString();
        }

        final MessageDigest m = MessageDigest.getInstance("MD5");
        m.update(message.getBytes("UTF-8"), 0, message.length());
        final byte[] sum = m.digest();
        final BigInteger messageAsNumber = new BigInteger(1, sum);
        String md5String = messageAsNumber.toString(16);

        // Pad to make sure id is 32 characters long.
        while (md5String.length() < 32)
        {
            md5String = "0" + md5String;
        }

        return "0x" + md5String.substring(0, 16);
    }

    // A string is empty in our terms, if it is null, empty or a dash.
    private static boolean isEmpty(final String in)
    {
        return in == null || "-".equals(in) || "".equals(in);
    }

    @Override
    public cc.aileron.web.WebProcess.Case process(final Object resource)
            throws Exception
    {
        trackPageView(Wsgi.Request(), Wsgi.Response());
        return Case.TERMINATE;
    }

    // Make a tracking request to Google Analytics from this server.
    // Copies the headers from the original request to the new one.
    // If request containg utmdebug parameter, exceptions encountered
    // communicating with Google Analytics are thown.
    private void sendRequestToGoogleAnalytics(final String utmUrl,
            final Request request) throws Exception
    {
        try
        {
            final URL url = new URL(utmUrl);
            final URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            connection.addRequestProperty("User-Agent",
                    request.header().get("User-Agent"));
            connection.addRequestProperty("Accepts-Language", request.header()
                    .get("Accepts-Language"));
            connection.getInputStream();
        }
        catch (final Exception e)
        {
            if (request.parameter().get("utmdebug") != null)
            {
                throw new Exception(e);
            }
        }
    }

    // Track a page view, updates all the cookies and campaign tracker,
    // makes a server side request to Google Analytics and writes the
    // transparent
    // gif byte data to the response.
    private void trackPageView(final Request request, final Response response)
            throws Exception
    {
        final ObjectProvider<String, String> param = Parameter.getSimpleParamter(request);
        final Map<String, String> header = request.header();

        String domainName = request.header().get("Host");
        if (isEmpty(domainName))
        {
            domainName = "";
        }

        // Get the referrer from the utmr parameter, this is the referrer to the
        // page that contains the tracking pixel, not the referrer for tracking
        // pixel.
        String documentReferer = param.get("utmr");
        if (isEmpty(documentReferer))
        {
            documentReferer = "-";
        }
        else
        {
            documentReferer = URLDecoder.decode(documentReferer, "UTF-8");
        }
        String documentPath = param.get("utmp");
        if (isEmpty(documentPath))
        {
            documentPath = "";
        }
        else
        {
            documentPath = URLDecoder.decode(documentPath, "UTF-8");
        }

        final String account = param.get("utmac");
        String userAgent = header.get("User-Agent");
        if (isEmpty(userAgent))
        {
            userAgent = "";
        }

        // Try and get visitor cookie from the request.
        final Cookie[] cookies = request.cookie();
        Cookie cookie = null;
        if (cookies != null)
        {
            for (int i = 0; i < cookies.length; i++)
            {
                if (cookies[i].getName().equals(COOKIE_NAME))
                {
                    cookie = cookies[i];
                }
            }
        }

        String guidHeader = param.get("X-DCMGUID");
        if (isEmpty(guidHeader))
        {
            guidHeader = header.get("X-UP-SUBNO");
        }
        if (isEmpty(guidHeader))
        {
            guidHeader = header.get("X-JPHONE-UID");
        }
        if (isEmpty(guidHeader))
        {
            guidHeader = header.get("X-EM-UID");
        }

        final String visitorId = getVisitorId(guidHeader,
                account,
                userAgent,
                cookie);

        // Always try and add the cookie to the response.
        final Cookie newCookie = new Cookie(COOKIE_NAME, visitorId);
        newCookie.setMaxAge(COOKIE_USER_PERSISTENCE);
        newCookie.setPath(COOKIE_PATH);
        response.header().add(newCookie);

        final String utmGifLocation = "http://www.google-analytics.com/__utm.gif";

        // Construct the gif hit url.
        final String utmUrl = utmGifLocation + "?" + "utmwv=" + version
                + "&utmn=" + getRandomNumber() + "&utmhn="
                + URLEncoder.encode(domainName, "UTF-8") + "&utmr="
                + URLEncoder.encode(documentReferer, "UTF-8") + "&utmp="
                + URLEncoder.encode(documentPath, "UTF-8") + "&utmac="
                + account + "&utmcc=__utma%3D999.999.999.999.999.1%3B"
                + "&utmvid=" + visitorId + "&utmip="
                + getIP(request.remoteAddress().toString());

        request.remoteAddress();

        sendRequestToGoogleAnalytics(utmUrl, request);

        // If the debug parameter is on, add a header to the response that
        // contains
        // the url that was used to contact Google Analytics.
        if (param.get("utmdebug") != null)
        {
            response.header().add("X-GA-MOBILE-URL", utmUrl);
        }
        // Finally write the gif data to the response.

        response.header()
                .add("Cache-Control",
                        "private, no-cache, no-cache=Set-Cookie, proxy-revalidate")
                .add("Pragma", "no-cache")
                .add("Expires", "Wed, 17 Sep 1975 21:32:10 GMT")
                .add("Content-Type", "image/gif")
                .add("Content-Length", "43");

    }

    /**
     * @throws IOException
     */
    public GoogleAnalyticsImageProcess() throws IOException
    {
        blankGif = Resource.Loader.get("blank.gif").toBytes();
    }

    final byte[] blankGif;
}
