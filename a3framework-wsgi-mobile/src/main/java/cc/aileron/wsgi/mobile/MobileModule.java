/**
 * 
 */
package cc.aileron.wsgi.mobile;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import cc.aileron.generic.ObjectProvider;
import cc.aileron.generic.ObjectReference;
import cc.aileron.generic.Resource;
import cc.aileron.generic.error.NotFoundException;
import cc.aileron.generic.util.SoftHashMap;
import cc.aileron.template.Template;
import cc.aileron.template.TemplateRepository;

import com.google.inject.Binder;
import com.google.inject.Module;

/**
 * @author aileron
 */
public class MobileModule implements Module
{
    @Override
    public void configure(final Binder binder)
    {
        binder.bind(MobileTemplateRepository.class).toInstance(mrepo);
    }

    /**
     * @throws TransformerConfigurationException
     * @throws XPathExpressionException
     * @throws NotFoundException
     */
    public MobileModule() throws TransformerConfigurationException,
            XPathExpressionException, NotFoundException
    {
        final MobileHtmlCssInlineConvertor cssInlineConvertor = new MobileHtmlCssInlineConvertorImpl();
        final CssSelectorPropertiesFactory cssloader = new CssSelectorPropertiesFactoryImpl(new CssSelectorToXPathImpl());
        final MobileHtmlEmojiConvertor emojiConvertor = new MobileHtmlEmojiConvertorImpl();
        mrepo = new MobileTemplateRepository()
        {
            @Override
            public ObjectProvider<String, Template> get(final String csspath)
            {
                final CssSelectorProperties css = cssloader.parse(Resource.Loader.get(csspath)
                        .toString());

                final MobileTemplateFactory factory = new MobileTemplateFactory(css,
                        TemplateRepository.XML,
                        cssInlineConvertor,
                        emojiConvertor);

                return new ObjectProvider<String, Template>()
                {
                    @Override
                    public Template get(final String path)
                    {
                        final ObjectReference<Template> result;
                        final ObjectReference<Template> cache = caches.get(path);
                        if (cache != null)
                        {
                            result = cache;
                        }
                        else
                        {
                            final ObjectReference<Template> template = factory.get(path);
                            caches.put(path, template);
                            result = template;
                        }
                        return result.get();
                    }

                    final SoftHashMap<String, ObjectReference<Template>> caches = new SoftHashMap<String, ObjectReference<Template>>();
                };
            }

        };
    }

    private final MobileTemplateRepository mrepo;
}
