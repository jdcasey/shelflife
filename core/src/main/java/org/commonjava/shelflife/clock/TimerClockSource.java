/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
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
            logger.debug( "Clearing expired from: {}", manager );
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
