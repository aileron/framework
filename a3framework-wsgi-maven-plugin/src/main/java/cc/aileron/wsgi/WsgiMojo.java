/**
 * 
 */
package cc.aileron.wsgi;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Properties;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import cc.aileron.generic.$;
import cc.aileron.generic.ObjectProvider;

/**
 * @goal wsgi
 * @author aileron
 */
public class WsgiMojo extends AbstractMojo
{
    /**
     * ポート
     */
    static final int PORT = Integer.parseInt(System.getProperty("port", "8080"));

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        final String repository = localRepository.getBasedir();
        final URI targetDir = new File(project.getBuild().getOutputDirectory()).toURI();
        final List<URI> artifacts = $.map($.<List<Artifact>> cast(project.getDependencyArtifacts()),
                new ObjectProvider<Artifact, URI>()
                {
                    @Override
                    public URI get(final Artifact dependency)
                    {
                        return new File(repository + "/"
                                + dependency.getGroupId().replace('.', '/')
                                + "/" + dependency.getArtifactId() + "/"
                                + dependency.getVersion() + "/"
                                + dependency.getArtifactId() + "-"
                                + dependency.getVersion() + ".jar").toURI();
                    }
                });

        final URL[] classpath = new URL[artifacts.size() + 1];
        for (int i = 0, size = classpath.length - 1; i < size; i++)
        {
            try
            {
                final URL url = artifacts.get(i).toURL();
                getLog().debug(String.format("classpath[ %s ]", url));
                classpath[i] = url;
            }
            catch (final MalformedURLException e)
            {
                throw new MojoFailureException("artifacts is failed!!!");
            }
        }
        try
        {
            final URL url = targetDir.toURL();
            getLog().debug(String.format("classpath[ %s ]", url));
            classpath[classpath.length - 1] = url;
        }
        catch (final MalformedURLException e)
        {
            throw new MojoFailureException("target dir is failed!!!");
        }

        try
        {
            main(classpath);
        }
        catch (final Exception e)
        {
            throw new MojoExecutionException("server execute fail", e);
        }
    }

    private void main(final URL[] classpath) throws IOException
    {
        /*
         * config
         */
        final Properties config = new Properties();
        config.load(new URLClassLoader(classpath).getResourceAsStream("wsgi.properties"));

        /*
         * listen
         */
        final ServerSocket server = new ServerSocket(PORT);
        for (;;)
        {
            final Socket client = server.accept();
            final URLClassLoader classloader = new URLClassLoader(classpath);
            final Thread thread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        classloader.loadClass("cc.aileron.wsgi.Server")
                                .getMethod("exec",
                                        Socket.class,
                                        Properties.class)
                                .invoke(null, client, config);
                    }
                    catch (final Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
            thread.setContextClassLoader(classloader);
            thread.run();
        }
    }

    /**
     * The enclosing project.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * @parameter expression="${localRepository}"
     * @required
     * @readonly
     * @since 1.0
     */
    private ArtifactRepository localRepository;

}
