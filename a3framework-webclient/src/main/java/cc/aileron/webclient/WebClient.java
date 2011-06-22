/**
 *
 */
package cc.aileron.webclient;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.ParseException;
import org.xml.sax.SAXException;

import cc.aileron.webclient.html.HtmlPage;
import cc.aileron.webclient.impl.WebClientImpl;

import com.google.inject.ImplementedBy;

/**
 * @author aileron
 */
@ImplementedBy(WebClientImpl.class)
public interface WebClient
{

    /**
     * @param request
     * @return {@link WebResponse}
     * @throws IOException
     * @throws HttpException
     */
    WebResponse<byte[]> getBytes(final WebRequest request)
            throws IOException, HttpException;

    /**
     * @param request
     * @return {@link WebResponse}
     * @throws IOException
     * @throws HttpException
     */
    WebResponse<File> getFile(WebRequest request)
            throws IOException, HttpException;

    /**
     * @param request
     * @return {@link WebResponse}
     * @throws IOException
     * @throws HttpException
     * @throws SAXException
     * @throws ParseException
     */
    WebResponse<HtmlPage> getPage(WebRequest request)
            throws IOException, HttpException, ParseException, SAXException;

    /**
     * @param request
     * @return {@link WebResponse}
     * @throws IOException
     * @throws HttpException
     */
    WebResponse<String> getString(WebRequest request)
            throws IOException, HttpException;

}
