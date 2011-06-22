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
import java.sql.SQLException;

/**
 * @author Aileron
 * 
 */
public class GeneratedNumberNoneGeneratedKeys implements GeneratedNamber
{
    @Override
    public int get(final PreparedStatement statement) throws SQLException
    {
        int id = getId(statement.getResultSet());
        while (statement.getMoreResults())
        {
            id = getId(statement.getResultSet());
        }
        return id;
    }

    /**
     * @param rs
     * @throws SQLException
     * @throws PojoPropertiesNotFoundException
     * @throws PojoAccessorValueNotFoundException
     */
    private int getId(final ResultSet rs) throws SQLException
    {
        if (rs == null)
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

}