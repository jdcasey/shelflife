package org.commonjava.shelflife.expire;

import static org.commonjava.shelflife.expire.ExpirationEventType.CANCEL;
import static org.commonjava.shelflife.expire.ExpirationEventType.SCHEDULE;
import static org.commonjava.shelflife.expire.ExpirationEventType.EXPIRE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.commonjava.shelflife.expire.match.ExpirationMatcher;
import org.commonjava.shelflife.model.Expiration;
import org.commonjava.util.logging.Logger;

@Singleton
public class MemoryExpirationManager
    implements ExpirationManager
{

    private final Logger logger = new Logger( getClass() );

    private final Timer timer = new Timer( true );

    private final List<Expiration> expirations = new ArrayList<Expiration>();

    @Inject
    private Event<ExpirationEvent> eventQueue;

    @Override
    public void schedule( final Expiration expiration )
        throws ExpirationManagerException
    {
        expirations.add( expiration );
        timer.schedule( new ExpirationTask( expiration ), expiration.getExpires() - System.currentTimeMillis() );
        eventQueue.fire( new ExpirationEvent( expiration, SCHEDULE ) );
    }

    @Override
    public void cancel( final Expiration expiration )
        throws ExpirationManagerException
    {
        synchronized ( expiration )
        {
            if ( expiration.isActive() && expirations.contains( expiration ) )
            {
                cancelInternal( expiration );
                eventQueue.fire( new ExpirationEvent( expiration, CANCEL ) );
            }
        }
    }

    private void cancelInternal( final Expiration expiration )
    {
        expiration.deactivate();
        expirations.remove( expiration );
    }

    @Override
    public void trigger( final Expiration expiration )
        throws ExpirationManagerException
    {
        synchronized ( expiration )
        {
            if ( expiration.isActive() && expirations.contains( expiration ) )
            {
                final ExpirationEvent event = new ExpirationEvent( expiration, EXPIRE );
                eventQueue.fire( event );

                cancelInternal( expiration );
            }
        }
    }

    @Override
    public void triggerAll()
        throws ExpirationManagerException
    {
        for ( final Expiration exp : new LinkedHashSet<Expiration>( expirations ) )
        {
            trigger( exp );
        }
    }

    @Override
    public void triggerAll( final ExpirationMatcher matcher )
        throws ExpirationManagerException
    {
        for ( final Expiration exp : getMatching( matcher ) )
        {
            if ( matcher.matches( exp ) )
            {
                trigger( exp );
            }
        }
    }

    @Override
    public void cancelAll()
        throws ExpirationManagerException
    {
        for ( final Expiration exp : new LinkedHashSet<Expiration>( expirations ) )
        {
            cancel( exp );
        }
    }

    @Override
    public void cancelAll( final ExpirationMatcher matcher )
        throws ExpirationManagerException
    {
        for ( final Expiration exp : getMatching( matcher ) )
        {
            cancel( exp );
        }
    }

    private Set<Expiration> getMatching( final ExpirationMatcher matcher )
    {
        final Set<Expiration> matching = new LinkedHashSet<Expiration>();
        for ( final Expiration exp : expirations )
        {
            if ( matcher.matches( exp ) )
            {
                matching.add( exp );
            }
        }

        return matching;
    }

    public final class ExpirationTask
        extends TimerTask
    {
        private final Expiration expiration;

        ExpirationTask( final Expiration exp )
        {
            this.expiration = exp;
        }

        @Override
        public void run()
        {
            try
            {
                trigger( expiration );
            }
            catch ( final ExpirationManagerException e )
            {
                logger.error( "Timed trigger of: %s failed: %s", e, expiration.getKey(), e.getMessage() );

                try
                {
                    MemoryExpirationManager.this.cancel( expiration );
                }
                catch ( final ExpirationManagerException eC )
                {
                    logger.error( "Cannot cancel failed expiration: %s. Reason: %s", eC, expiration.getKey(),
                                  eC.getMessage() );
                }
            }
        }
    }

    @Override
    public void loadedFromStorage( final Collection<Expiration> expirations )
        throws ExpirationManagerException
    {
        for ( final Expiration expiration : expirations )
        {
            expirations.add( expiration );
            timer.schedule( new ExpirationTask( expiration ), expiration.getExpires() - System.currentTimeMillis() );
        }
    }

}
