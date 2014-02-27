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
package org.commonjava.shelflife.event;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.commonjava.cdi.util.weft.ExecutorConfig;
import org.commonjava.shelflife.model.Expiration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Alternative
@ApplicationScoped
public class ThreadedEventManager
    implements ExpirationEventManager
{

    private final Logger logger = LoggerFactory.getLogger( getClass() );

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
        private final Logger logger = LoggerFactory.getLogger( getClass() );

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
            logger.debug( "Firing event: {} to {} listeners: {}", evt, listeners.size(), listeners );
            for ( final SimpleEventListener listener : listeners )
            {
                logger.debug( "{} -> {}", evt, listener );
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
            logger.debug( "Preparing to fire event of type: {} for: {} to listeners: {}", type, expiration, listeners );
            if ( !listeners.isEmpty() )
            {
                event = new ExpirationEvent( expiration, type );
                logger.debug( "Adding event dispatch for: {}", event );

                // TODO: May need to try to avoid new dispatch construction all the time...
                exec.execute( new Dispatch( listeners, event ) );

                if ( jeeEvents != null )
                {
                    jeeEvents.fire( event );
                }

                //                for ( final SimpleEventListener listener : listeners )
                //                {
                //                    logger.debug( "{} -> {}", event, listener );
                //                    listener.onEvent( event );
                //                }
            }
        }

        return event;
    }
}
