package cc.aileron.webclient.html.entity;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.HttpException;

/**
 * @author aileron
 */
public interface HtmlImageElement extends HtmlElement
{
    /**
     * @return {@link File}
     * @throws HttpException
     * @throws IOException
     * @throws URISyntaxException
     */
    File getFile() throws URISyntaxException, IOException, HttpException;
}
