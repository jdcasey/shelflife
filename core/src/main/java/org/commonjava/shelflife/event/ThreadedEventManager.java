package org.commonjava.shelflife.event;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.commonjava.cdi.util.weft.ExecutorConfig;
import org.commonjava.shelflife.event.ExpirationEvent;
import org.commonjava.shelflife.event.ExpirationEventManager;
import org.commonjava.shelflife.event.ExpirationEventType;
import org.commonjava.shelflife.model.Expiration;
import org.commonjava.util.logging.Logger;

@Alternative
@ApplicationScoped
public class ThreadedEventManager
    implements ExpirationEventManager
{

    private final Logger logger = new Logger( getClass() );

    private final Set<SimpleEventListener> listeners = new HashSet<SimpleEventListener>();

    @Inject
    @ExecutorConfig( threads = 2, named = "shelflife-events", daemon = true, priority = 9 )
    private Executor exec;

    @Inject
    private Event<ExpirationEvent> jeeEvents;

    public ThreadedEventManager()
    {
    }

    public ThreadedEventManager( final Executor exec )
    {
        this.exec = exec;
    }

    public void clearListeners()
    {
        synchronized ( listeners )
        {
            listeners.clear();
        }
    }

    public boolean addListener( final SimpleEventListener listener )
    {
        synchronized ( listeners )
        {
            return listeners.add( listener );
        }
    }

    public boolean removeListener( final SimpleEventListener listener )
    {
        synchronized ( listeners )
        {
            return listeners.remove( listener );
        }
    }

    public interface SimpleEventListener
    {
        void onEvent( ExpirationEvent event );
    }

    private static final class Dispatch
        implements Runnable
    {
        private final Logger logger = new Logger( getClass() );

        private final Set<SimpleEventListener> listeners;

        private final ExpirationEvent evt;

        public Dispatch( final Set<SimpleEventListener> listeners, final ExpirationEvent evt )
        {
            this.evt = evt;
            this.listeners = new HashSet<SimpleEventListener>( listeners );
        }

        @Override
        public void run()
        {
            logger.debug( "Firing event: %s to %d listeners: %s", evt, listeners.size(), listeners );
            for ( final SimpleEventListener listener : listeners )
            {
                logger.debug( "%s -> %s", evt, listener );
                listener.onEvent( evt );
            }
        }
    }

    @Override
    public ExpirationEvent fire( final Expiration expiration, final ExpirationEventType type )
    {
        ExpirationEvent event = null;
        synchronized ( listeners )
        {
            logger.debug( "Preparing to fire event of type: %s for: %s to listeners: %s", type, expiration, listeners );
            if ( !listeners.isEmpty() )
            {
                event = new ExpirationEvent( expiration, type );
                logger.debug( "Adding event dispatch for: %s", event );

                // TODO: May need to try to avoid new dispatch construction all the time...
                exec.execute( new Dispatch( listeners, event ) );

                if ( jeeEvents != null )
                {
                    jeeEvents.fire( event );
                }

                //                for ( final SimpleEventListener listener : listeners )
                //                {
                //                    logger.debug( "%s -> %s", event, listener );
                //                    listener.onEvent( event );
                //                }
            }
        }

        return event;
    }
}
