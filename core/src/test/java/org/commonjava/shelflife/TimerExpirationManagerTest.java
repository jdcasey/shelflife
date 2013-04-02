package org.commonjava.shelflife;

import java.util.concurrent.Executors;

import org.commonjava.shelflife.DefaultExpirationManager;
import org.commonjava.shelflife.clock.ExpirationClockSource;
import org.commonjava.shelflife.clock.TimerClockSource;
import org.commonjava.shelflife.event.ThreadedEventManager;
import org.commonjava.shelflife.store.memory.MemoryBlockStore;

public class TimerExpirationManagerTest
    extends AbstractExpirationManagerTest
{

    protected ExpirationClockSource clock;

    @Override
    protected void setupComponents()
        throws Exception
    {
        final MemoryBlockStore store = new MemoryBlockStore();
        clock = new TimerClockSource( getEventTimeout() );

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
