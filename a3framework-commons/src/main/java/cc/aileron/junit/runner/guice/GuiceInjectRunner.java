/**
 * Copyright (C) 2008 aileron.cc
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
package cc.aileron.junit.runner.guice;

import java.lang.reflect.Constructor;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runners.BlockJUnit4ClassRunner;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * junit-test-runner
 * 
 * @author Aileron
 * 
 */
public class GuiceInjectRunner extends BlockJUnit4ClassRunner
{
    @Override
    protected void collectInitializationErrors(final List<Throwable> errors)
    {
        validatePublicVoidNoArgMethods(BeforeClass.class,
                true,
                errors);
        validatePublicVoidNoArgMethods(AfterClass.class,
                true,
                errors);
    }

    @Override
    protected Object createTest() throws Exception
    {
        return injector.getInstance(getTestClass().getJavaClass());
    }

    /**
     * injector の取得
     * 
     * @param targetClass
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ModuleConfigurationException
     */
    private Injector getInjector(final Class<?> targetClass)
    {
        final GuiceInjectRunnerModule moduleAnnotation = targetClass.getAnnotation(GuiceInjectRunnerModule.class);
        if (moduleAnnotation == null)
        {
            return Guice.createInjector();
        }
        try
        {
            final Class<? extends Module> moduleClass = moduleAnnotation.value();
            final String args = moduleAnnotation.arg();
            if (args.length() == 0)
            {
                final Constructor<? extends Module> constructor = moduleClass.getConstructor();
                constructor.setAccessible(true);
                return Guice.createInjector(constructor.newInstance());
            }

            final Constructor<? extends Module> constructor = moduleClass.getConstructor(String.class);
            final Module module = constructor.newInstance(args);
            return Guice.createInjector(module);

        }
        catch (final Exception e)
        {
            e.printStackTrace();
            throw new Error(e);
        }
    }

    /**
     * constractor
     * 
     * @param testClass
     * @throws org.junit.runners.model.InitializationError
     */
    public GuiceInjectRunner(final Class<?> testClass) throws org.junit.runners.model.InitializationError
    {
        super(testClass);
        this.injector = getInjector(testClass);
    }

    /**
     * injector
     */
    private final Injector injector;
}
