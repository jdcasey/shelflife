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

import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PreDestroy;
import javax.enterprise.inject.Alternative;

import org.commonjava.shelflife.ExpirationManager;
import org.commonjava.shelflife.ExpirationManagerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Alternative
public class TimerClockSource
    implements ExpirationClockSource
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final Timer timer = new Timer( "shelflife-timer@" + hashCode(), false );

    private Clock clock;

    private final long period;

    public TimerClockSource()
    {
        this.period = ExpirationManager.NEXT_EXPIRATION_BATCH_OFFSET;
    }

    public TimerClockSource( final long period )
    {
        this.period = period;
    }

    @Override
    public void start( final ExpirationManager manager )
        throws ExpirationManagerException
    {
        if ( clock != null )
        {
            throw new RuntimeException( "Cannot start with expiration manager: " + manager + ". This clock source is already started!" );
        }

        logger.info( "Starting clock for manager: {}, period: {}", manager, period );

        clock = new Clock( manager );
        timer.schedule( clock, 0, period );
    }

    private static final class Clock
        extends TimerTask
    {
        private final Logger logger = LoggerFactory.getLogger( getClass() );

        private final ExpirationManager manager;

        private Clock( final ExpirationManager manager )
        {
            this.manager = manager;
        }

        @Override
        public void run()
        {
            logger.info( "Clearing expired from: {}", manager );
            manager.clearExpired();
        }
    }

    @Override
    @PreDestroy
    public void stop()
    {
        synchronized ( clock )
        {
            clock.cancel();
        }
        timer.cancel();
        timer.purge();
    }

    @Override
    public String toString()
    {
        return String.format( "TimerClockSource [period=%s]", period );
    }

}
