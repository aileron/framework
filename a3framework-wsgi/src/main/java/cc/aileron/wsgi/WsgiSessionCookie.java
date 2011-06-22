/**
 * 
 */
package cc.aileron.wsgi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.servlet.http.Cookie;

import cc.aileron.generic.$;
import cc.aileron.wsgi.Wsgi.Request;
import cc.aileron.wsgi.Wsgi.Response;
import cc.aileron.wsgi.Wsgi.Session;

/**
 * クッキー利用のセッション
 */
public class WsgiSessionCookie implements Wsgi.Session.Handler
{
    /**
     * The base length.
     */
    static final int BASELENGTH = 255;

    /**
     * Chunk size per RFC 2045 section 6.8.
     * 
     * <p>
     * The {@value} character limit does not count the trailing CRLF, but counts
     * all other characters, including any equal signs.
     * </p>
     * 
     * @see <a href="http://www.ietf.org/rfc/rfc2045.txt">RFC 2045 section
     *      6.8</a>
     */
    static final int CHUNK_SIZE = 76;

    static final String COOKIE_NAME = Wsgi.Session.class.getName();

    /**
     * Used to calculate the number of bits in a byte.
     */
    static final int EIGHTBIT = 8;

    /**
     * Used to get the number of Quadruples.
     */
    static final int FOURBYTE = 4;

    /**
     * Lookup length.
     */
    static final int LOOKUPLENGTH = 64;

    /**
     * Byte used to pad output.
     */
    static final byte PAD = (byte) '=';
    /**
     * Used to test the sign of a byte.
     */
    static final int SIGN = -128;

    /**
     * Used when encoding something which has fewer than 24 bits.
     */
    static final int SIXTEENBIT = 16;

    /**
     * Used to determine how many bits data contains.
     */
    static final int TWENTYFOURBITGROUP = 24;

    // Create arrays to hold the base64 characters and a
    // lookup for base64 chars
    private static byte[] base64Alphabet = new byte[BASELENGTH];

    private static byte[] lookUpBase64Alphabet = new byte[LOOKUPLENGTH];

    // Populating the lookup and character arrays
    static
    {
        for (int i = 0; i < BASELENGTH; i++)
        {
            base64Alphabet[i] = (byte) -1;
        }
        for (int i = 'Z'; i >= 'A'; i--)
        {
            base64Alphabet[i] = (byte) (i - 'A');
        }
        for (int i = 'z'; i >= 'a'; i--)
        {
            base64Alphabet[i] = (byte) (i - 'a' + 26);
        }
        for (int i = '9'; i >= '0'; i--)
        {
            base64Alphabet[i] = (byte) (i - '0' + 52);
        }

        base64Alphabet['+'] = 62;
        base64Alphabet['/'] = 63;

        for (int i = 0; i <= 25; i++)
        {
            lookUpBase64Alphabet[i] = (byte) ('A' + i);
        }

        for (int i = 26, j = 0; i <= 51; i++, j++)
        {
            lookUpBase64Alphabet[i] = (byte) ('a' + j);
        }

        for (int i = 52, j = 0; i <= 61; i++, j++)
        {
            lookUpBase64Alphabet[i] = (byte) ('0' + j);
        }

        lookUpBase64Alphabet[62] = (byte) '+';
        lookUpBase64Alphabet[63] = (byte) '/';
    }

    public static byte[] decode(byte[] base64Data)
    {
        // RFC 2045 requires that we discard ALL non-Base64 characters
        base64Data = discardNonBase64(base64Data);

        // handle the edge case, so we don't have to worry about it later
        if (base64Data.length == 0)
        {
            return new byte[0];
        }

        final int numberQuadruple = base64Data.length / FOURBYTE;
        byte decodedData[] = null;
        byte b1 = 0, b2 = 0, b3 = 0, b4 = 0, marker0 = 0, marker1 = 0;

        // Throw away anything not in base64Data

        int encodedIndex = 0;
        int dataIndex = 0;
        {
            // this sizes the output array properly - rlw
            int lastData = base64Data.length;
            // ignore the '=' padding
            while (base64Data[lastData - 1] == PAD)
            {
                if (--lastData == 0)
                {
                    return new byte[0];
                }
            }
            decodedData = new byte[lastData - numberQuadruple];
        }

        for (int i = 0; i < numberQuadruple; i++)
        {
            dataIndex = i * 4;
            marker0 = base64Data[dataIndex + 2];
            marker1 = base64Data[dataIndex + 3];

            b1 = base64Alphabet[base64Data[dataIndex]];
            b2 = base64Alphabet[base64Data[dataIndex + 1]];

            if (marker0 != PAD && marker1 != PAD)
            {
                // No PAD e.g 3cQl
                b3 = base64Alphabet[marker0];
                b4 = base64Alphabet[marker1];

                decodedData[encodedIndex] = (byte) (b1 << 2 | b2 >> 4);
                decodedData[encodedIndex + 1] = (byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf));
                decodedData[encodedIndex + 2] = (byte) (b3 << 6 | b4);
            }
            else if (marker0 == PAD)
            {
                // Two PAD e.g. 3c[Pad][Pad]
                decodedData[encodedIndex] = (byte) (b1 << 2 | b2 >> 4);
            }
            else if (marker1 == PAD)
            {
                // One PAD e.g. 3cQ[Pad]
                b3 = base64Alphabet[marker0];

                decodedData[encodedIndex] = (byte) (b1 << 2 | b2 >> 4);
                decodedData[encodedIndex + 1] = (byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf));
            }
            encodedIndex += 3;
        }
        return decodedData;
    }

    /**
     * Discards any characters outside of the base64 alphabet, per the
     * requirements on page 25 of RFC 2045 - "Any characters outside of the
     * base64 alphabet are to be ignored in base64 encoded data."
     * 
     * @param data
     *            The base-64 encoded data to groom
     * @return The data, less non-base64 characters (see RFC 2045).
     */
    static byte[] discardNonBase64(final byte[] data)
    {
        final byte groomedData[] = new byte[data.length];
        int bytesCopied = 0;

        for (int i = 0; i < data.length; i++)
        {
            if (isBase64(data[i]))
            {
                groomedData[bytesCopied++] = data[i];
            }
        }

        final byte packedData[] = new byte[bytesCopied];

        System.arraycopy(groomedData, 0, packedData, 0, bytesCopied);

        return packedData;
    }

    /**
     * Encodes binary data using the base64 algorithm, optionally chunking the
     * output into 76 character blocks.
     * 
     * @param binaryData
     *            Array containing binary data to encode.
     * @param isChunked
     *            if isChunked is true this encoder will chunk the base64 output
     *            into 76 character blocks
     * @return Base64-encoded data.
     */
    private static byte[] encode(final byte[] binaryData)
    {
        final int lengthDataBits = binaryData.length * EIGHTBIT;
        final int fewerThan24bits = lengthDataBits % TWENTYFOURBITGROUP;
        final int numberTriplets = lengthDataBits / TWENTYFOURBITGROUP;
        byte encodedData[] = null;
        int encodedDataLength = 0;
        final int nbrChunks = 0;

        if (fewerThan24bits != 0)
        {
            // data not divisible by 24 bit
            encodedDataLength = (numberTriplets + 1) * 4;
        }
        else
        {
            // 16 or 8 bit
            encodedDataLength = numberTriplets * 4;
        }

        encodedData = new byte[encodedDataLength];

        byte k = 0, l = 0, b1 = 0, b2 = 0, b3 = 0;

        int encodedIndex = 0;
        int dataIndex = 0;
        int i = 0;
        final int nextSeparatorIndex = CHUNK_SIZE;
        final int chunksSoFar = 0;

        // log.debug("number of triplets = " + numberTriplets);
        for (i = 0; i < numberTriplets; i++)
        {
            dataIndex = i * 3;
            b1 = binaryData[dataIndex];
            b2 = binaryData[dataIndex + 1];
            b3 = binaryData[dataIndex + 2];

            // log.debug("b1= " + b1 +", b2= " + b2 + ", b3= " + b3);

            l = (byte) (b2 & 0x0f);
            k = (byte) (b1 & 0x03);

            final byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2)
                    : (byte) ((b1) >> 2 ^ 0xc0);
            final byte val2 = ((b2 & SIGN) == 0) ? (byte) (b2 >> 4)
                    : (byte) ((b2) >> 4 ^ 0xf0);
            final byte val3 = ((b3 & SIGN) == 0) ? (byte) (b3 >> 6)
                    : (byte) ((b3) >> 6 ^ 0xfc);

            encodedData[encodedIndex] = lookUpBase64Alphabet[val1];
            // log.debug( "val2 = " + val2 );
            // log.debug( "k4   = " + (k<<4) );
            // log.debug( "vak  = " + (val2 | (k<<4)) );
            encodedData[encodedIndex + 1] = lookUpBase64Alphabet[val2
                    | (k << 4)];
            encodedData[encodedIndex + 2] = lookUpBase64Alphabet[(l << 2)
                    | val3];
            encodedData[encodedIndex + 3] = lookUpBase64Alphabet[b3 & 0x3f];

            encodedIndex += 4;
        }

        // form integral number of 6-bit groups
        dataIndex = i * 3;

        if (fewerThan24bits == EIGHTBIT)
        {
            b1 = binaryData[dataIndex];
            k = (byte) (b1 & 0x03);
            // log.debug("b1=" + b1);
            // log.debug("b1<<2 = " + (b1>>2) );
            final byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2)
                    : (byte) ((b1) >> 2 ^ 0xc0);
            encodedData[encodedIndex] = lookUpBase64Alphabet[val1];
            encodedData[encodedIndex + 1] = lookUpBase64Alphabet[k << 4];
            encodedData[encodedIndex + 2] = PAD;
            encodedData[encodedIndex + 3] = PAD;
        }
        else if (fewerThan24bits == SIXTEENBIT)
        {

            b1 = binaryData[dataIndex];
            b2 = binaryData[dataIndex + 1];
            l = (byte) (b2 & 0x0f);
            k = (byte) (b1 & 0x03);

            final byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2)
                    : (byte) ((b1) >> 2 ^ 0xc0);
            final byte val2 = ((b2 & SIGN) == 0) ? (byte) (b2 >> 4)
                    : (byte) ((b2) >> 4 ^ 0xf0);

            encodedData[encodedIndex] = lookUpBase64Alphabet[val1];
            encodedData[encodedIndex + 1] = lookUpBase64Alphabet[val2
                    | (k << 4)];
            encodedData[encodedIndex + 2] = lookUpBase64Alphabet[l << 2];
            encodedData[encodedIndex + 3] = PAD;
        }

        return encodedData;
    }

    private static boolean isBase64(final byte octect)
    {
        if (octect == PAD)
        {
            return true;
        }
        else if (base64Alphabet[octect] == -1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    @Override
    public Session get(final Request request, final Response response)
            throws IOException, ClassNotFoundException
    {
        final WsgiSession session;
        final Cookie cookie = cookie(request);
        if (cookie != null)
        {
            final ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(decode(cookie.getValue()
                    .getBytes())));
            session = $.cast(stream.readObject());
        }
        else
        {
            session = new WsgiSession();
        }
        return session;
    }

    @Override
    public void put(final Session session, final Request request,
            final Response response) throws IOException
    {
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final ObjectOutputStream output = new ObjectOutputStream(stream);
        output.writeObject(session);
        output.close();
        final String value = new String(encode(stream.toByteArray()));
        final Cookie cookie = new Cookie(COOKIE_NAME, value);
        response.header().add(cookie);
    }

    private Cookie cookie(final Request request)
    {
        for (final Cookie cookie : request.cookie())
        {
            if (!cookie.getName().equals(COOKIE_NAME))
            {
                continue;
            }
            return cookie;
        }
        return null;
    }
}