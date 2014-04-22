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
package org.commonjava.shelflife.fixture;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.event.Observes;

import org.commonjava.shelflife.event.ExpirationEvent;
import org.commonjava.shelflife.event.ThreadedEventManager.SimpleEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@javax.enterprise.context.ApplicationScoped
public class TestExpirationListener
    implements SimpleEventListener
{

    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final List<ExpirationEvent> events = new ArrayList<ExpirationEvent>();

    public void expire( @Observes final ExpirationEvent event )
    {
        synchronized ( events )
        {
            logger.debug( "Adding event: {}", event );
            events.add( event );
            events.notify();
        }
    }

    @Override
    public void onEvent( final ExpirationEvent event )
    {
        synchronized ( events )
        {
            logger.debug( "Adding event: {}", event );
            events.add( event );
            events.notify();
        }
    }

    public List<ExpirationEvent> waitForEvents( final int count, final long perEventTimeout )
        throws InterruptedException
    {
        final long timeout = ( count + 4 ) * perEventTimeout;

        final long start = System.currentTimeMillis();
        long elapsed;

        final List<ExpirationEvent> result = new ArrayList<ExpirationEvent>();
        synchronized ( events )
        {
            while ( ( events.size() < count ) && ( ( elapsed = System.currentTimeMillis() - start ) < timeout ) )
            {
                logger.debug( "TICK: {} -> {} events", elapsed, events.size() );
                events.wait( 500 );
            }

            logger.debug( "elapsed: {} (vs {}); event count: {} (vs {}). Either timed out or event count met.",
                          ( System.currentTimeMillis() - start ), timeout, events.size(), count );

            if ( !events.isEmpty() )
            {
                result.addAll( events );
                events.clear();
            }
        }

        logger.debug( "Result: {}", result );

        return result;
    }

}
