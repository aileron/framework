package cc.aileron.proxy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import cc.aileron.generic.util.WorkQueue;
import cc.aileron.proxy.domain.IpBalancerDomain;
import cc.aileron.proxy.domain.IpBarancerDomainImpl;

/**
 * @author aileron
 */
public class MultiIpProxy
{
    static final long interval = Integer.parseInt(System.getProperty("interval",
            "10000"));
    static final int proxyport = Integer.parseInt(System.getProperty("port",
            "8080"));
    static final int workQueueThreads = Integer.parseInt(System.getProperty("thread",
            "10"));

    /**
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args) throws IOException
    {
        final IpBalancerDomain domain = new IpBarancerDomainImpl(interval, args);
        final WorkQueue workQueue = new WorkQueue(workQueueThreads);
        final ServerSocket server = new ServerSocket(proxyport);
        for (;;)
        {
            final Socket client = server.accept();
            log("client:accept");
            workQueue.execute(new Runnable()
            {
                /**
                 * @throws NumberFormatException
                 * @throws IOException
                 */
                public void doRun() throws NumberFormatException, IOException
                {
                    log("client:accept:doRun");

                    final BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    final StringBuilder builder = new StringBuilder();
                    while (reader.ready())
                    {
                        final String line = reader.readLine();
                        log("client:request:%s", line);
                        builder.append(line).append("\n");
                        if (line.indexOf("Host:") == 0)
                        {
                            log("client:host:read");

                            final String[] hostTokens = line.replaceAll("Host: (.*)",
                                    "$1")
                                    .trim()
                                    .split(":");

                            final String hostName = hostTokens[0];
                            final String port = hostTokens.length == 2 ? hostTokens[1]
                                    : "80";

                            String gateway;
                            do
                            {
                                gateway = domain.get(hostName);
                                if (gateway != null)
                                {
                                    break;
                                }
                                try
                                {
                                    Thread.sleep(1000);
                                }
                                catch (final InterruptedException e)
                                {
                                    break;
                                }

                            } while (true);
                            log("proxy(host=%s, gateway=%s)", hostName, gateway);
                            if (gateway == null)
                            {
                                log("proxy(gateway is nul)");
                                break;
                            }

                            proxy = new Socket();
                            proxy.bind(new InetSocketAddress(gateway, 0));
                            proxy.connect(new InetSocketAddress(hostName,
                                    Integer.parseInt(port)));

                            log("client:proxy:init");
                            break;
                        }
                    }

                    if (proxy == null)
                    {
                        client.close();
                        return;
                    }

                    /*
                     * write proxy request line
                     */
                    final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(proxy.getOutputStream()));
                    writer.write(builder.toString());
                    while (reader.ready())
                    {
                        writer.write(reader.readLine());
                        writer.newLine();
                    }
                    writer.newLine();
                    writer.flush();

                    /*
                     * write client response
                     */
                    final InputStream input = proxy.getInputStream();
                    final OutputStream output = client.getOutputStream();
                    final byte[] buffer = new byte[1024];
                    int n = 0;
                    while ((n = input.read(buffer)) != -1)
                    {
                        output.write(buffer, 0, n);
                    }
                    proxy.close();
                    client.close();
                }

                @Override
                public void run()
                {
                    try
                    {
                        doRun();
                    }
                    catch (final Exception e)
                    {
                        e.printStackTrace();
                    }
                }

                Socket proxy;
            });
        }
    }

    /**
     * @param format
     * @param args
     */
    static void log(final String format, final Object... args)
    {
        // System.out.println(String.format(format, args));
    }
}
