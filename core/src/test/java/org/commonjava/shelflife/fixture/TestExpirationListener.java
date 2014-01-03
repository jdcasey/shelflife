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
package org.commonjava.shelflife.fixture;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.event.Observes;

import org.commonjava.shelflife.event.ExpirationEvent;
import org.commonjava.shelflife.event.ThreadedEventManager.SimpleEventListener;
import org.commonjava.util.logging.Logger;

@javax.enterprise.context.ApplicationScoped
public class TestExpirationListener
    implements SimpleEventListener
{

    private final Logger logger = new Logger( getClass() );

    private final List<ExpirationEvent> events = new ArrayList<ExpirationEvent>();

    public void expire( @Observes final ExpirationEvent event )
    {
        synchronized ( events )
        {
            logger.debug( "Adding event: %s", event );
            events.add( event );
            events.notify();
        }
    }

    @Override
    public void onEvent( final ExpirationEvent event )
    {
        synchronized ( events )
        {
            logger.debug( "Adding event: %s", event );
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
                logger.debug( "TICK: %s -> %d events", elapsed, events.size() );
                events.wait( 500 );
            }

            logger.debug( "elapsed: %s (vs %s); event count: %s (vs %s). Either timed out or event count met.",
                          ( System.currentTimeMillis() - start ), timeout, events.size(), count );

            if ( !events.isEmpty() )
            {
                result.addAll( events );
                events.clear();
            }
        }

        logger.debug( "Result: %s", result );

        return result;
    }

}
