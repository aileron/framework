/**
 * 
 */
package cc.aileron.wsgi;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import cc.aileron.generic.util.WorkQueue;

/**
 * @requiresDependencyResolution test
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
        final String baseDir = localRepository.getBasedir();
        final ArrayList<URL> set = new ArrayList<URL>();
        for (final Artifact dependency : (Set<Artifact>) project.getArtifacts())
        {
            try
            {
                final String path = "jar:file:" + baseDir
                        + localRepository.pathOf(dependency) + "!/";

                set.add(new URL(path));
            }
            catch (final MalformedURLException e)
            {
                throw new MojoFailureException("dependency jar is failed!!!");
            }
        }
        final URL[] classpath = new URL[set.size() + 1];
        int i = 0;
        for (final URL url : set)
        {
            classpath[i++] = url;
        }
        try
        {
            final URL url = new File(project.getBuild().getOutputDirectory()).toURI()
                    .toURL();

            classpath[classpath.length - 1] = url;
        }
        catch (final MalformedURLException e1)
        {
            throw new MojoFailureException("target dir is failed!!!");
        }
        for (final URL url : classpath)
        {
            getLog().debug("classpath:" + url);
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

        final WorkQueue worker = new WorkQueue(10);

        /*
         * listen
         */
        final ServerSocket server = new ServerSocket(PORT);
        getLog().debug("server start!!!");
        for (;;)
        {
            final Socket client = server.accept();
            final URLClassLoader loader = new URLClassLoader(classpath);
            worker.execute(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        Thread.currentThread().setContextClassLoader(loader);
                        loader.loadClass("cc.aileron.wsgi.Server")
                                .getMethod("exec",
                                        Socket.class,
                                        Properties.class,
                                        ClassLoader.class)
                                .invoke(null, client, config, loader);
                    }
                    catch (final Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * Specify classifier to look for. Example: sources
     * 
     * @optional
     * @since 2.0
     * @parameter expression="${classifier}" default-value=""
     */
    protected String classifier;

    /**
     * Comma separated list of Artifact names too exclude.
     * 
     * @since 2.0
     * @optional
     * @parameter expression="${excludeArtifactIds}" default-value=""
     */
    protected String excludeArtifactIds;

    /**
     * Comma Separated list of Classifiers to exclude. Empty String indicates
     * don't exclude anything (default).
     * 
     * @since 2.0
     * @parameter expression="${excludeClassifiers}" default-value=""
     * @optional
     */
    protected String excludeClassifiers;

    /**
     * Comma separated list of GroupId Names to exclude.
     * 
     * @since 2.0
     * @optional
     * @parameter expression="${excludeGroupIds}" default-value=""
     */
    protected String excludeGroupIds;

    /**
     * Scope to exclude. An Empty string indicates no scopes (default).
     * 
     * @since 2.0
     * @parameter expression="${excludeScope}" default-value=""
     * @optional
     */
    protected String excludeScope;

    /**
     * If we should exclude transitive dependencies
     * 
     * @since 2.0
     * @optional
     * @parameter expression="${excludeTransitive}" default-value="false"
     */
    protected boolean excludeTransitive;

    /**
     * Comma Separated list of Types to exclude. Empty String indicates don't
     * exclude anything (default).
     * 
     * @since 2.0
     * @parameter expression="${excludeTypes}" default-value=""
     * @optional
     */
    protected String excludeTypes;

    /**
     * Comma separated list of Artifact names to include.
     * 
     * @since 2.0
     * @optional
     * @parameter expression="${includeArtifactIds}" default-value=""
     */
    protected String includeArtifactIds;

    /**
     * Comma Separated list of Classifiers to include. Empty String indicates
     * include everything (default).
     * 
     * @since 2.0
     * @parameter expression="${includeClassifiers}" default-value=""
     * @optional
     */
    protected String includeClassifiers;

    /**
     * Comma separated list of GroupIds to include.
     * 
     * @since 2.0
     * @optional
     * @parameter expression="${includeGroupIds}" default-value=""
     */
    protected String includeGroupIds;

    /**
     * Scope to include. An Empty string indicates all scopes (default).
     * 
     * @since 2.0
     * @parameter expression="${includeScope}" default-value=""
     * @optional
     */
    protected String includeScope;

    /**
     * Comma Separated list of Types to include. Empty String indicates include
     * everything (default).
     * 
     * @since 2.0
     * @parameter expression="${includeTypes}" default-value=""
     * @optional
     */
    protected String includeTypes;

    /**
     * @parameter expression="${localRepository}"
     * @required
     * @readonly
     * @since 1.0
     */
    protected ArtifactRepository localRepository;

    /**
     * The enclosing project.
     * 
     * @requiresDependencyResolution runtime
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * Specify type to look for when constructing artifact based on classifier.
     * Example: java-source,jar,war
     * 
     * @optional
     * @since 2.0
     * @parameter expression="${type}" default-value="java-source"
     */
    protected String type;
}
