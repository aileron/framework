/**
 * 
 */
package cc.aileron.dao.jdbc;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.EnumSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cc.aileron.generic.ObjectProvider;
import cc.aileron.generic.PrimitiveWrappers.NumberGetAccessor;
import cc.aileron.generic.util.SkipList;
import cc.aileron.pojo.NoSuchPropertyException;
import cc.aileron.pojo.PojoAccessor;
import cc.aileron.pojo.PojoAccessorRepository;
import cc.aileron.template.Template;
import cc.aileron.template.method.VariableExpand;

/**
 * @author aileron
 */
public class SqlTemplateFetcherImpl implements SqlTemplateFetcher
{
    static final Pattern pattern = Pattern.compile("\\.(.)");

    @Override
    public StatmentParameter fetch(final SqlTemplateCategory category,
            final PojoAccessor<?> parameterObject)
    {
        final String sqlFilePath = dirName + "/" + category.type + sqlName;
        final Template t = templateRepository.get(sqlFilePath);
        final StringWriter writer = new StringWriter();
        final List<Object> object = new SkipList<Object>();
        t.print(accessorRepository.from(new SqlTemplateContext()
        {
            @Override
            public String rep(final VariableExpand ve)
            {
                return ve.expand(parameterObject);
            }

            @Override
            public void var(final String name)
            {
                try
                {
                    object.add(convert(parameterObject.to(name).get()));
                }
                catch (final NoSuchPropertyException e)
                {
                    throw e.error();
                }
            }
        }), new PrintWriter(writer));

        String addsql = "";
        switch (category)
        {
        case COUNT:
        case FIND_ONE:
            addsql = " limit 1;";
            break;
        case FIND_PAGING:
            try
            {
                addsql = String.format(" limit %d offset %d;",
                        parameterObject.to("limit").get(),
                        parameterObject.to("offset").get());
            }
            catch (final NoSuchPropertyException e)
            {
                throw e.error();
            }
            break;
        default:
            break;
        }

        final String tmp = writer.toString();
        final int endIdx = tmp.lastIndexOf(';');
        final String baseSql = endIdx == -1 ? tmp : tmp.substring(0, endIdx);
        final String sql = baseSql + addsql;
        return new StatmentParameter()
        {

            @Override
            public List<Object> arguments()
            {
                return object;
            }

            @Override
            public SqlTemplateCategory category()
            {
                return category;
            }

            @Override
            public String name()
            {
                return sqlFilePath;
            }

            @Override
            public String sql()
            {
                return sql;
            }
        };
    }

    Object convert(final Object object)
    {
        if (object == null)
        {
            return null;
        }

        /*
         * Numberのオブジェクトとして評価可能な場合には、Numberの値を取得
         */
        if (object instanceof NumberGetAccessor)
        {
            return NumberGetAccessor.class.cast(object).toNumber();
        }

        /*
         * EnumSet の場合は、各Enumの数値表現の重ね合わせをDBに格納する為
         * 計算する。またEnumの数値表現は、Numberとして評価した際の値を使用する
         */
        if (object instanceof EnumSet)
        {
            int result = 0;
            for (final Object o : EnumSet.class.cast(object))
            {
                result += ((NumberGetAccessor) o).toNumber().intValue();
            }
            return result;
        }

        /*
         * 特に変換無し
         */
        return object;
    }

    /**
     * @param targetClass
     * @return dir
     */
    private String getDirName(final Class<?> targetClass)
    {
        final String name = targetClass.getCanonicalName();
        if (name == null)
        {
            return null;
        }
        final StringBuffer buffer = new StringBuffer();
        final Matcher matcher = pattern.matcher(name);
        while (matcher.find())
        {
            matcher.appendReplacement(buffer, "/"
                    + matcher.group(1).toLowerCase());
        }
        return matcher.appendTail(buffer).toString();
    }

    /**
     * @param accessorRepository
     * @param templateRepository
     * @param type
     * @param sqlName
     */
    public SqlTemplateFetcherImpl(
            final PojoAccessorRepository accessorRepository,
            final ObjectProvider<String, Template> templateRepository,
            final Class<?> type, final String sqlName)
    {
        this.accessorRepository = accessorRepository;
        this.templateRepository = templateRepository;
        this.dirName = getDirName(type);
        this.sqlName = sqlName.isEmpty() ? ".sql" : "-" + sqlName + ".sql";
    }

    final PojoAccessorRepository accessorRepository;
    final String dirName;
    final String sqlName;
    final ObjectProvider<String, Template> templateRepository;
}
