/**
 * 
 */
package cc.aileron.generic.util;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author aileron
 */
public class WorkQueue implements Executor
{
    /**
     * @author aileron
     */
    private class PoolWorker extends Thread
    {
        @Override
        public void run()
        {
            while (true)
            {
                Runnable runnable;
                try
                {
                    runnable = queue.take();
                }
                catch (final InterruptedException e1)
                {
                    return;
                }

                try
                {
                    runnable.run();
                }
                catch (final RuntimeException e)
                {
                    e.printStackTrace();
                }
            }
        }

        public PoolWorker()
        {
        }
    }

    /**
     * @param r
     */
    @Override
    public void execute(final Runnable r)
    {
        queue.add(r);
    }

    /**
     * 止める
     */
    public void shutdown()
    {
        for (final PoolWorker worker : workers)
        {
            worker.interrupt();
        }
    }

    /**
     * @param nThreads
     */
    public WorkQueue(final int nThreads)
    {
        workers = new PoolWorker[nThreads];
        for (int i = 0; i < nThreads; i++)
        {
            workers[i] = new PoolWorker();
            workers[i].start();
        }
    }

    final LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
    final PoolWorker[] workers;
}
