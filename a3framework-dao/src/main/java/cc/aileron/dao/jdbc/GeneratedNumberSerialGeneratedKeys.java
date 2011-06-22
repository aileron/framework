/**
 * Copyright (C) 2009 aileron.cc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package cc.aileron.dao.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * statement.getGeneratedKeys() を使用したナンバの取得実装
 * 
 * @author Aileron
 * 
 */
public class GeneratedNumberSerialGeneratedKeys implements GeneratedNamber
{
    /**
     * @param statement
     * @return serianlNumber
     * @throws SQLException
     */
    public int doGetSerialNumber(final PreparedStatement statement)
            throws SQLException
    {
        final ResultSet rs = statement.getGeneratedKeys();
        if (rs == null)
        {
            return 0;
        }

        final ResultSetMetaData meta = rs.getMetaData();
        if (meta.getColumnCount() == 0)
        {
            return 0;
        }
        if (!rs.next())
        {
            return 0;
        }

        try
        {
            final int result = rs.getInt(1);
            return result;
        }
        finally
        {
            rs.close();
        }
    }

    @Override
    public int get(final PreparedStatement statement) throws SQLException
    {
        final int serial = doGetSerialNumber(statement);
        if (serial > 0)
        {
            return serial;
        }
        return serialNoneGeneratedKeys.get(statement);
    }

    /**
     * @param serialNoneGeneratedKeys
     */
    public GeneratedNumberSerialGeneratedKeys(
            final GeneratedNumberNoneGeneratedKeys serialNoneGeneratedKeys)
    {
        this.serialNoneGeneratedKeys = serialNoneGeneratedKeys;
    }

    private final GeneratedNumberNoneGeneratedKeys serialNoneGeneratedKeys;
}