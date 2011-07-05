/**
 * 
 */
package cc.aileron.dao.jdbc;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import cc.aileron.dao.DataTransaction;

/**
 * @author aileron
 */
public class StatmentExecutorImpl implements StatmentExecutor
{
    static final GeneratedNumberNoneGeneratedKeys none = new GeneratedNumberNoneGeneratedKeys();
    static final GeneratedNumberSerialGeneratedKeys serial = new GeneratedNumberSerialGeneratedKeys(none);

    @Override
    public int execute(final StatmentParameter parameter)
    {
        final Connection connection = transaction.get();
        try
        {
            final String sql = parameter.sql();
            final List<Object> fetchParameter = parameter.arguments();

            if (logger.isEnable())
            {
                logger.output(parameter);
            }

            final PreparedStatement statement = connection.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS);

            for (int max = fetchParameter.size(), i = 0; i < max; i++)
            {
                statement.setObject(i + 1, fetchParameter.get(i));
            }
            if (!statement.execute())
            {
                return 0;
            }
            if (parameter.category() == SqlTemplateCategory.UPDATE)
            {
                return statement.getUpdateCount();
            }
            return generatedNumber.get(statement);
        }
        catch (final SQLException e)
        {
            throw new Error(e);
        }
        finally
        {
            transaction.end();
        }
    }

    @Override
    public void execute(final StatmentParameter parameter,
            final ResultSetHandller handller)
    {
        final Connection connection = transaction.get();
        try
        {
            final String sql = parameter.sql();
            final List<Object> fetchParameter = parameter.arguments();

            if (logger.isEnable())
            {
                logger.output(parameter);
            }

            final PreparedStatement statement = connection.prepareStatement(sql,
                    java.sql.ResultSet.TYPE_FORWARD_ONLY,
                    java.sql.ResultSet.CONCUR_READ_ONLY);

            for (int max = fetchParameter.size(), i = 0; i < max; i++)
            {
                statement.setObject(i + 1, fetchParameter.get(i));
            }
            if (!statement.execute())
            {
                statement.close();
                return;
            }
            final ResultSet rs = statement.getResultSet();
            handller.handle(rs);
        }
        catch (final InvocationTargetException e)
        {
            throw new Error(e.getCause());
        }
        catch (final Exception e)
        {
            throw new Error(e);
        }
        finally
        {
            transaction.end();
        }
    }

    /**
     * @return {@link GeneratedNamber}
     */
    public GeneratedNamber getGeneratedNumber()
    {
        switch (transaction.db())
        {
        case MYSQL:
        case H2:
            return serial;
        default:
            return none;
        }
    }

    /**
     * @param transaction
     * @param logger
     */
    public StatmentExecutorImpl(final DataTransaction transaction,
            final StatmentLogger logger)
    {
        this.transaction = transaction;
        this.logger = logger;
        this.generatedNumber = getGeneratedNumber();
    }

    private final GeneratedNamber generatedNumber;
    private final StatmentLogger logger;
    private final DataTransaction transaction;
}
