/*
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
package cc.aileron.generic;

/**
 * @author Aileron
 */
public class PrimitiveWrappers
{
    /**
     * get boolean
     * 
     * @author Aileron
     * 
     */
    public static interface BooleanGetAccessor
    {
        /**
         * @return boolean
         */
        Boolean toBoolean();
    }

    /**
     * set boolean
     * 
     * @author Aileron
     */
    public static interface BooleanSetAccessor
    {
        /**
         * @param bool
         */
        void bool(Boolean bool);
    }

    /**
     * get number
     * 
     * @author Aileron
     * 
     */
    public static interface NumberGetAccessor
    {
        /**
         * @return number
         */
        Number toNumber();
    }

    /**
     * set number
     * 
     * @author Aileron
     * 
     */
    public static interface NumberSetAccessor
    {
        /**
         * @param number
         */
        void number(Number number);
    }

    /**
     * 
     * @author Aileron
     * 
     */
    public static interface StringDebugAccessor
    {
        /**
         * 
         * @return debug string
         */
        String toDebug();
    }

    /**
     * set string
     * 
     * @author Aileron
     * 
     */
    public static interface StringSetAccessor
    {
        /**
         * @param string
         */
        void string(String string);
    }

}
