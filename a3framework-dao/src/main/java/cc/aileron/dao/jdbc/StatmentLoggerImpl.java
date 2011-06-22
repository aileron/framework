/*
 * Copyright 2008 aileron.cc, Inc.
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

import java.sql.Timestamp;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.aileron.generic.$;
import cc.aileron.generic.PrimitiveWrappers.NumberGetAccessor;

/**
 * @author Aileron
 */
public class StatmentLoggerImpl implements StatmentLogger
{
    private static class SMatcher
    {
        public void appendReplacement()
        {
            rp("/*" + (idx++) + "*/null");
        }

        public void appendReplacement(final Class<?> type, final String object)
        {
            if (object == null)
            {
                rp("/*" + (idx++) + "*/null");
                return;
            }
            rp("/*" + idx++ + ":" + type.getName() + "*/" + object);
        }

        public void appendReplacement(
                final Enum<? extends NumberGetAccessor> object)
        {
            if (object == null)
            {
                rp("/*" + (idx++) + "*/null");
                return;
            }
            final Number id = NumberGetAccessor.class.cast(object).toNumber();
            rp("/*" + idx++ + ":" + object.getClass().getName() + "@"
                    + object.name() + "*/" + id);
        }

        public void appendTail()
        {
            matcher.appendTail(buffer);
        }

        /**
         * @return find
         */
        public boolean find()
        {
            return matcher.find();
        }

        @Override
        public String toString()
        {
            return buffer.toString();
        }

        private void rp(final String replacement)
        {
            matcher.appendReplacement(buffer,
                    Matcher.quoteReplacement(replacement));
        }

        public SMatcher(final Matcher matcher)
        {
            this.matcher = matcher;
        }

        private final StringBuffer buffer = new StringBuffer();
        private int idx = 0;
        private final Matcher matcher;
    }

    /**
     * sql のパラメータを置き換える為の正規表現
     */
    private static final Pattern pattern = Pattern.compile("\\?");

    @Override
    public boolean isEnable()
    {
        return isEnabled;
    }

    @Override
    public void output(final StatmentParameter p)
    {
        if (p.arguments().size() == 0)
        {
            logger.debug(p.name() + "=>" + p.sql());
        }
        else
        {
            final SMatcher matcher = new SMatcher(pattern.matcher(p.sql()));
            for (final Object object : p.arguments())
            {
                if (matcher.find())
                {
                    if (object == null)
                    {
                        matcher.appendReplacement();
                        continue;
                    }
                    if (object.getClass().isPrimitive()
                            || Number.class.isAssignableFrom(object.getClass()))
                    {
                        matcher.appendReplacement(object.getClass(),
                                object.toString());
                        continue;
                    }
                    if (object.getClass().isEnum())
                    {
                        final Enum<? extends NumberGetAccessor> value = $.cast(object);
                        matcher.appendReplacement(value);
                        continue;
                    }
                    if (Date.class.isAssignableFrom(object.getClass()))
                    {
                        final String value = new Timestamp(((Date) object).getTime()).toString();
                        matcher.appendReplacement(object.getClass(), "'"
                                + value + "'");
                        continue;
                    }
                    matcher.appendReplacement(object.getClass(),
                            "'" + object.toString() + "'");
                }
            }
            matcher.appendTail();
            logger.debug(p.name() + "=>" + matcher.toString());
        }
    }

    /**
     * default constractor
     */
    public StatmentLoggerImpl()
    {
        logger = LoggerFactory.getLogger(this.getClass());
        isEnabled = logger.isDebugEnabled();
    }

    /**
     * is-debug
     */
    private final boolean isEnabled;

    /**
     * logger
     */
    private final Logger logger;
}