/**
 * 
 */
package cc.aileron.generic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import cc.aileron.generic.util.SkipList;

/**
 * @author aileron
 */
public interface Resource extends Option
{
    /**
     * @author aileron
     */
    class ClassPathResourceLoader implements StreamLoader
    {
        @Override
        public InputStream load(final String path)
        {
            for (final StreamLoader loader : list)
            {
                final InputStream stream = loader.load(path);
                if (stream != null)
                {
                    return stream;
                }
            }
            return null;
        }

        private void addLoader(final URL url)
        {
            final String protocol = url.getProtocol();
            if (protocol.equals("file"))
            {
                final File base;
                try
                {
                    base = new File(url.toURI());
                }
                catch (final URISyntaxException e)
                {
                    e.printStackTrace();
                    return;
                }
                list.add(new StreamLoader()
                {
                    @Override
                    public InputStream load(final String path)
                    {
                        try
                        {
                            return new FileInputStream(new File(base, path));
                        }
                        catch (final FileNotFoundException e)
                        {
                            e.printStackTrace();
                            return null;
                        }
                    }
                });
                return;
            }
            final String classpathAttribute;
            try
            {
                final JarFile jarFile = new JarFile(url.getPath());
                list.add(new StreamLoader()
                {
                    @Override
                    public InputStream load(final String path)
                    {
                        final ZipEntry entry = jarFile.getEntry(path);
                        if (entry == null)
                        {
                            return null;
                        }
                        try
                        {
                            return jarFile.getInputStream(jarFile.getEntry(path));
                        }
                        catch (final IOException e)
                        {
                            return null;
                        }
                    }
                });

                final Manifest manifest;
                final Attributes attributes;
                if ((manifest = jarFile.getManifest()) == null
                        || (attributes = manifest.getMainAttributes()) == null)
                {
                    return;
                }

                classpathAttribute = attributes.getValue((Attributes.Name.CLASS_PATH));
                if (classpathAttribute == null || classpathAttribute.isEmpty())
                {
                    return;
                }
            }
            catch (final IOException e)
            {
                return;
            }
            for (final String path : classpathAttribute.split(" "))
            {
                try
                {
                    addLoader(new URL(path));
                }
                catch (final MalformedURLException e)
                {
                }
            }
        }

        /**
         * @throws IOException
         * 
         */
        public ClassPathResourceLoader() throws IOException
        {
            final URLClassLoader classLoader = (URLClassLoader) Thread.currentThread()
                    .getContextClassLoader();

            list.add(new StreamLoader()
            {
                @Override
                public InputStream load(final String path)
                {
                    return classLoader.getResourceAsStream(path);
                }
            });

            final List<URL> urls = Arrays.asList(classLoader.getURLs());
            for (final URL url : urls)
            {
                addLoader(url);
            }
        }

        final List<StreamLoader> list = new SkipList<Resource.StreamLoader>();
    }

    /**
     * @author aileron
     */
    abstract class Loader implements ObjectProvider<String, Resource>
    {
        static List<StreamLoader> loaders = new SkipList<StreamLoader>();
        static
        {
            try
            {
                loaders.add(new ClassPathResourceLoader());
            }
            catch (final IOException e)
            {
                throw new Error(e);
            }
        }

        /**
         * @param loader
         */
        public static void append(final StreamLoader loader)
        {
            loaders.add(loader);
        }
    }

    /**
     * 文字列による、リソースインスタンス生成用クラス
     */
    class Str implements Resource
    {
        @Override
        public boolean isNotFound()
        {
            return false;
        }

        @Override
        public String path()
        {
            return path;
        }

        @Override
        public byte[] toBytes()
        {
            return string.getBytes();
        }

        @Override
        public Properties toProperties()
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public InputStream toStream()
        {
            throw new UnsupportedOperationException();
        }

        /**
         * @param string
         */
        public Str(final String string)
        {
            this.path = "";
            this.string = string;
        }

        /**
         * @param path
         * @param string
         */
        public Str(final String path, final String string)
        {
            this.path = path;
            this.string = string;
        }

        private final String path;

        private final String string;

    }

    /**
     * @author aileron
     */
    interface StreamLoader
    {
        InputStream load(String path);
    }

    /**
     * @return path
     */
    String path();

    /**
     * @return toBytes
     */
    byte[] toBytes();

    /**
     * @return {@link Properties}
     */
    Properties toProperties();

    /**
     * @return {@link InputStream}
     */
    InputStream toStream();

    /**
     * @return toString
     */
    @Override
    String toString();

    /**
     * loader
     */
    Loader Loader = new Loader()
    {
        @Override
        public Resource get(final String path)
        {
            InputStream tmp = null;
            for (final StreamLoader loader : loaders)
            {
                tmp = loader.load(path);
                if (tmp != null)
                {
                    break;
                }
            }
            final InputStream stream = tmp;
            if (stream == null)
            {
                return new Resource()
                {
                    @Override
                    public boolean isNotFound()
                    {
                        return true;
                    }

                    @Override
                    public String path()
                    {
                        return path;
                    }

                    @Override
                    public byte[] toBytes()
                    {
                        throw new UnsupportedOperationException("not found resource["
                                + path + "]");
                    }

                    @Override
                    public Properties toProperties()
                    {
                        throw new UnsupportedOperationException("not found resource["
                                + path + "]");
                    }

                    @Override
                    public InputStream toStream()
                    {
                        throw new UnsupportedOperationException("not found resource["
                                + path + "]");
                    }

                    @Override
                    public String toString()
                    {
                        throw new UnsupportedOperationException("not found resource["
                                + path + "]");
                    }

                };
            }
            return new Resource()
            {

                @Override
                public boolean isNotFound()
                {
                    return false;
                }

                @Override
                public String path()
                {
                    return path;
                }

                @Override
                public byte[] toBytes()
                {
                    final ReadableByteChannel channel = Channels.newChannel(stream);
                    final byte[] buff = new byte[65536];
                    try
                    {
                        final int size = channel.read(ByteBuffer.wrap(buff));
                        return Arrays.copyOfRange(buff, 0, size);
                    }
                    catch (final IOException e)
                    {
                        throw new Error(e);
                    }
                }

                @Override
                public Properties toProperties()
                {
                    final Properties properties = new Properties();
                    try
                    {
                        properties.load(stream);
                    }
                    catch (final IOException e)
                    {
                        throw new Error(e);
                    }
                    return properties;
                }

                @Override
                public InputStream toStream()
                {
                    return stream;
                }

                @Override
                public String toString()
                {
                    final ReadableByteChannel channel = Channels.newChannel(stream);
                    final byte[] buff = new byte[65536];
                    try
                    {
                        final int size = channel.read(ByteBuffer.wrap(buff));
                        return new String(buff, 0, size, "UTF-8");
                    }
                    catch (final IOException e)
                    {
                        throw new Error(e);
                    }
                }
            };
        }
    };
}
