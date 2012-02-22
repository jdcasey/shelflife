package org.commonjava.shelflife.fixture;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.event.Observes;
import javax.inject.Singleton;

import org.commonjava.shelflife.expire.ExpirationEvent;

@Singleton
public class TestExpirationListener
{

    private List<ExpirationEvent> events = new ArrayList<ExpirationEvent>();

    public synchronized void expire( @Observes final ExpirationEvent event )
    {
        events.add( event );
        notify();
    }

    public synchronized List<ExpirationEvent> waitForEvents( final long timeout )
        throws InterruptedException
    {
        final long start = System.currentTimeMillis();

        while ( events.isEmpty() && ( System.currentTimeMillis() - start < timeout ) )
        {
            wait( 500 );
        }

        List<ExpirationEvent> result = null;
        if ( !events.isEmpty() )
        {
            result = events;
            events = new ArrayList<ExpirationEvent>();
        }

        return result;
    }

}
