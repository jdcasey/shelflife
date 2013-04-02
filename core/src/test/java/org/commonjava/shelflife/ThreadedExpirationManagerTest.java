package org.commonjava.shelflife;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.commonjava.shelflife.DefaultExpirationManager;
import org.commonjava.shelflife.clock.ExpirationClockSource;
import org.commonjava.shelflife.clock.ThreadedClockSource;
import org.commonjava.shelflife.event.ThreadedEventManager;
import org.commonjava.shelflife.store.memory.MemoryBlockStore;

public class ThreadedExpirationManagerTest
    extends AbstractExpirationManagerTest
{

    protected ExpirationClockSource clock;

    @Override
    protected void setupComponents()
        throws Exception
    {
        final MemoryBlockStore store = new MemoryBlockStore();
        clock = new ThreadedClockSource( Executors.newFixedThreadPool( 2, new ThreadFactory()
        {
            private int counter = 0;

            @Override
            public Thread newThread( final Runnable runnable )
            {
                final Thread t = new Thread( runnable );
                t.setPriority( 2 );
                t.setName( "shelflife-clock-" + counter++ );
                return t;
            }
        } ), getEventTimeout() );

        final ThreadedEventManager events = new ThreadedEventManager( Executors.newFixedThreadPool( 2 ) );
        events.addListener( listener );

        manager = new DefaultExpirationManager( store, clock, events );
    }

    @Override
    protected ExpirationClockSource getClock()
    {
        return clock;
    }

}
