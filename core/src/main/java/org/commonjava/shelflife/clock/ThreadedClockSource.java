package org.commonjava.shelflife.clock;

import java.util.concurrent.Executor;

import javax.annotation.PreDestroy;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.commonjava.cdi.util.weft.ExecutorConfig;
import org.commonjava.shelflife.ExpirationManager;
import org.commonjava.shelflife.ExpirationManagerException;
import org.commonjava.util.logging.Logger;

@Alternative
@Singleton
public class ThreadedClockSource
    implements ExpirationClockSource
{
    private final Logger logger = new Logger( getClass() );

    @Inject
    @ExecutorConfig( threads = 1, priority = 9, daemon = true, named = "shelflife-clock" )
    private Executor executor;

    private Clock clock;

    private long period;

    public ThreadedClockSource()
    {
        this.period = ExpirationManager.NEXT_EXPIRATION_BATCH_OFFSET;
    }

    public ThreadedClockSource( final Executor executor, final long period )
    {
        this.executor = executor;
        this.period = period;
    }

    public void setPeriod( final long period )
    {
        logger.debug( "\n\n\n\nCLOCK PERIOD RESET TO: %d ms", period );
        this.period = period;
    }

    public long getPeriod()
    {
        return period;
    }

    @Override
    public void start( final ExpirationManager manager )
        throws ExpirationManagerException
    {
        if ( clock != null )
        {
            throw new RuntimeException( "Cannot start with expiration manager: " + manager
                + ". This clock source is already started!" );
        }

        logger.debug( "Starting clock for manager: %s, period: %s", manager, period );

        manager.loadNextExpirations();
        clock = new Clock( manager, false );
        executor.execute( clock );
    }

    private final class Clock
        implements Runnable
    {
        private final Logger logger = new Logger( getClass() );

        private final ExpirationManager manager;

        private boolean wait;

        private long lastSkew = 0;

        private boolean stop = false;

        private Thread myThread;

        private Clock( final ExpirationManager manager, final boolean wait )
        {
            this.manager = manager;
            this.wait = wait;
        }

        public synchronized void stop()
        {
            logger.debug( "setting stop flag on clock" );
            stop = true;
            if ( myThread != null )
            {
                logger.debug( "interrupting current clock thread: %s", myThread );
                myThread.interrupt();
            }
        }

        @Override
        public void run()
        {
            if ( stop )
            {
                logger.debug( "stopping clock" );
                return;
            }

            synchronized ( this )
            {
                myThread = Thread.currentThread();
            }

            boolean abort = false;
            if ( wait )
            {
                try
                {
                    logger.debug( "sleeping for wait period of %d ms minus skew of: %d", period, lastSkew );
                    Thread.sleep( period - lastSkew );
                }
                catch ( final InterruptedException e )
                {
                    abort = true;
                }
            }

            final long start = System.currentTimeMillis();

            if ( abort )
            {
                logger.debug( "aborting clock due to interruption." );
                return;
            }

            logger.debug( "Clearing expired from: %s", manager );
            manager.clearExpired();

            wait = true;
            logger.debug( "priming for next tick: %s", this );
            lastSkew = System.currentTimeMillis() - start;

            synchronized ( this )
            {
                myThread = null;
            }

            executor.execute( this );
        }
    }

    @Override
    @PreDestroy
    public void stop()
    {
        clock.stop();
    }

}
