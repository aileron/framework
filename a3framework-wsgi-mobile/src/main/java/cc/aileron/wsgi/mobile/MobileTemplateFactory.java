/**
 * 
 */
package cc.aileron.wsgi.mobile;

import static cc.aileron.generic.Resource.*;

import java.util.EnumMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.aileron.generic.ObjectProvider;
import cc.aileron.generic.ObjectReference;
import cc.aileron.generic.Resource;
import cc.aileron.template.Template;
import cc.aileron.template.TemplateRepository;
import cc.aileron.wsgi.Wsgi;

/**
 * @author aileron
 */
class MobileTemplateFactory implements
        ObjectProvider<String, ObjectReference<Template>>
{
    @Override
    public ObjectReference<Template> get(final String path)
    {
        final Resource html = Loader.get(path);
        final EnumMap<MobileCarrier, Template> map = new EnumMap<MobileCarrier, Template>(MobileCarrier.class);
        for (final MobileCarrier carrier : MobileCarrier.values())
        {
            if (!carrier.isMobile())
            {
                map.put(carrier, repository.get(html));
                continue;
            }

            /*
             * htmlを書き換える為に、文字列を取得
             */
            String convertHtml = Loader.get(path).toString();

            /*
             * doctype 部分までを削除
             */
            final MobileCarrier htmlcarrier;
            final Matcher matcher = extractDoctype.matcher(convertHtml);
            if (matcher.find())
            {
                htmlcarrier = MobileCarrier.parseDocType(matcher.group(1));
                convertHtml = convertHtml.substring(matcher.end());
            }
            else
            {
                htmlcarrier = MobileCarrier.OTHER;
            }

            /*
             * htmlのキャリアと、表示キャリアが同一の場合
             */
            if (carrier == htmlcarrier)
            {
                map.put(carrier, repository.get(html));
                continue;
            }

            /*
             * 絵文字の置き換え
             */
            logger.debug("emojiConvertor.convert({},{},?}",
                    htmlcarrier,
                    carrier);
            convertHtml = emojiConvertor.convert(htmlcarrier,
                    carrier,
                    convertHtml);

            /*
             * css のインライン化
             */
            if (carrier.enableCssInline())
            {
                try
                {
                    convertHtml = cssInlineConvertor.convert(carrier,
                            convertHtml,
                            css);
                }
                catch (final Exception e)
                {
                    throw new Error(e);
                }
            }
            else
            {
                /*
                 * xml宣言 + doctype を先頭に付与
                 */
                convertHtml = carrier.xmlDeclaration() + carrier.doctype()
                        + convertHtml;
            }

            /*
             * 変換後テンプレートの格納
             */
            map.put(carrier,
                    repository.get(new Resource.Str(path, convertHtml)));
        }

        return new ObjectReference<Template>()
        {
            @Override
            public Template get()
            {
                final String userAgent = Wsgi.Request()
                        .header()
                        .get("User-Agent");
                final MobileCarrier carrier = MobileCarrier.parseUserAgent(userAgent);
                return map.get(carrier);
            }
        };
    }

    /**
     * @param repository
     * @param css
     * @param cssInlineConvertor
     * @param emojiConvertor
     */
    public MobileTemplateFactory(final CssSelectorProperties css,
            final TemplateRepository repository,
            final MobileHtmlCssInlineConvertor cssInlineConvertor,
            final MobileHtmlEmojiConvertor emojiConvertor)
    {
        this.repository = repository;
        this.css = css;
        this.cssInlineConvertor = cssInlineConvertor;
        this.emojiConvertor = emojiConvertor;
    }

    private final CssSelectorProperties css;
    private final MobileHtmlCssInlineConvertor cssInlineConvertor;
    private final MobileHtmlEmojiConvertor emojiConvertor;
    private final Pattern extractDoctype = Pattern.compile("(<!DOCTYPE.+?>)",
            Pattern.CASE_INSENSITIVE);
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final TemplateRepository repository;
}