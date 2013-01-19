package org.commonjava.shelflife.store.memory;

import static org.commonjava.shelflife.expire.ExpirationEventType.CANCEL;
import static org.commonjava.shelflife.expire.ExpirationEventType.EXPIRE;
import static org.commonjava.shelflife.expire.ExpirationEventType.SCHEDULE;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.commonjava.shelflife.expire.ExpirationEvent;
import org.commonjava.shelflife.expire.ExpirationManager;
import org.commonjava.shelflife.expire.ExpirationManagerException;
import org.commonjava.shelflife.expire.match.ExpirationMatcher;
import org.commonjava.shelflife.model.Expiration;
import org.commonjava.shelflife.model.ExpirationKey;
import org.commonjava.util.logging.Logger;

@javax.enterprise.context.ApplicationScoped
public class MemoryExpirationManager
    implements ExpirationManager
{

    private final Logger logger = new Logger( getClass() );

    private final Timer timer = new Timer( true );

    private final LinkedHashMap<ExpirationKey, Expiration> expirations = new LinkedHashMap<ExpirationKey, Expiration>();

    @Inject
    private Event<ExpirationEvent> eventQueue;

    @Override
    public void schedule( final Expiration expiration )
        throws ExpirationManagerException
    {
        expirations.put( expiration.getKey(), expiration );
        timer.schedule( new ExpirationTask( expiration ), expiration.getExpires() - System.currentTimeMillis() );
        logger.info( "[SCHEDULED] %s, expires: %s", expiration.getKey(), new Date( expiration.getExpires() ) );
        eventQueue.fire( new ExpirationEvent( expiration, SCHEDULE ) );
    }

    @Override
    public void cancel( final Expiration expiration )
        throws ExpirationManagerException
    {
        synchronized ( expiration )
        {
            if ( expiration.isActive() && expirations.containsKey( expiration.getKey() ) )
            {
                expiration.cancel();
                expirations.remove( expiration );
                logger.info( "[CANCELED] %s", expiration.getKey(), new Date( expiration.getExpires() ) );
                eventQueue.fire( new ExpirationEvent( expiration, CANCEL ) );
            }
        }
    }

    @Override
    public void cancel( final ExpirationKey key )
        throws ExpirationManagerException
    {
        final Expiration expiration = expirations.get( key );
        if ( expiration != null )
        {
            cancel( expiration );
        }

    }

    @Override
    public void trigger( final ExpirationKey key )
        throws ExpirationManagerException
    {
        final Expiration expiration = expirations.get( key );
        if ( expiration != null )
        {
            trigger( expiration );
        }
    }

    @Override
    public void trigger( final Expiration expiration )
        throws ExpirationManagerException
    {
        synchronized ( expiration )
        {
            if ( expiration.isActive() && expirations.containsKey( expiration.getKey() ) )
            {
                expiration.expire();
                expirations.remove( expiration );
                logger.info( "[TRIGGERED] %s", expiration.getKey(), new Date( expiration.getExpires() ) );
                eventQueue.fire( new ExpirationEvent( expiration, EXPIRE ) );
            }
        }
    }

    @Override
    public void triggerAll()
        throws ExpirationManagerException
    {
        for ( final Expiration exp : new LinkedHashSet<Expiration>( expirations.values() ) )
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
        for ( final Expiration exp : new LinkedHashSet<Expiration>( expirations.values() ) )
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
        for ( final Expiration exp : expirations.values() )
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
            this.expirations.put( expiration.getKey(), expiration );
            if ( expiration.getExpires() <= System.currentTimeMillis() )
            {
                trigger( expiration );
            }
            else
            {
                timer.schedule( new ExpirationTask( expiration ), expiration.getExpires() - System.currentTimeMillis() );
            }
        }
    }

    @Override
    public boolean contains( final Expiration expiration )
        throws ExpirationManagerException
    {
        return expirations.containsKey( expiration.getKey() );
    }

    @Override
    public boolean hasExpiration( final ExpirationKey key )
    {
        return expirations.containsKey( key );
    }

}
