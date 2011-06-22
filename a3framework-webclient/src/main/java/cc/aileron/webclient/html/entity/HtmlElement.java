package cc.aileron.webclient.html.entity;

import java.util.List;

import org.w3c.dom.html.HTMLElement;

import cc.aileron.webclient.html.HtmlPage;

/**
 * @author aileron
 */
public interface HtmlElement
{
    /**
     * @param name
     * @return attr
     */
    String attr(String name);

    /**
     * @param name
     * @param value
     */
    void attr(String name, String value);

    /**
     * @param <T>
     * @return children
     */
    <T extends HtmlElement> List<T> children();

    /**
     * @param <T>
     * @param xpath
     * @return List<T>
     */
    <T extends HtmlElement> List<T> getByXPath(String xpath);

    /**
     * @return id
     */
    String id();

    /**
     * @return {@link HtmlPage}
     */
    HtmlPage page();

    /**
     * @param <T>
     * @return {@link HTMLElement}
     */
    <T extends HTMLElement> T raw();

    /**
     * @return text
     */
    String text();
}