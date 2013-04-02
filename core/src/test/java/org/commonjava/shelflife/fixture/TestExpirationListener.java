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
        final long timeout = ( count + 1 ) * perEventTimeout;

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
