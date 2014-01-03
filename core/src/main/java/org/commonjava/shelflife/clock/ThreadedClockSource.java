/*******************************************************************************
 * Copyright (C) 2014 John Casey.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.commonjava.shelflife.clock;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.commonjava.cdi.util.weft.ExecutorConfig;
import org.commonjava.cdi.util.weft.StoppableRunnable;
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
    private ScheduledExecutorService executor;

    private Clock clock;

    private long period;

    public ThreadedClockSource()
    {
        this.period = ExpirationManager.NEXT_EXPIRATION_BATCH_OFFSET;
    }

    public ThreadedClockSource( final ScheduledExecutorService executor, final long period )
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
            throw new RuntimeException( "Cannot start with expiration manager: " + manager + ". This clock source is already started!" );
        }

        logger.info( "Starting clock for manager: %s, period: %s", manager, period );

        clock = new Clock( manager );
        executor.scheduleAtFixedRate( clock, 0, period, TimeUnit.MILLISECONDS );
    }

    private final class Clock
        extends StoppableRunnable
    {
        private final Logger logger = new Logger( getClass() );

        private final ExpirationManager manager;

        private Clock( final ExpirationManager manager )
        {
            this.manager = manager;
        }

        @Override
        protected void doExecute()
        {
            logger.info( "Clearing expired from: %s", manager );
            manager.clearExpired();
        }
    }

    @Override
    @PreDestroy
    public void stop()
    {
        clock.stop();
        executor.shutdown();
    }

    @Override
    public String toString()
    {
        return String.format( "ThreadedClockSource [period=%s]", period );
    }

}
