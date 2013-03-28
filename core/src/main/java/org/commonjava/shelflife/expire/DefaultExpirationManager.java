package org.commonjava.shelflife.expire;

import static org.commonjava.shelflife.expire.ExpirationEventType.CANCEL;
import static org.commonjava.shelflife.expire.ExpirationEventType.EXPIRE;
import static org.commonjava.shelflife.expire.ExpirationEventType.SCHEDULE;
import static org.commonjava.shelflife.util.BlockKeyUtils.generateBlockKey;
import static org.commonjava.shelflife.util.BlockKeyUtils.generateCurrentBlockKey;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.commonjava.shelflife.expire.clock.ExpirationClockSource;
import org.commonjava.shelflife.expire.match.ExpirationMatcher;
import org.commonjava.shelflife.model.Expiration;
import org.commonjava.shelflife.model.ExpirationKey;
import org.commonjava.shelflife.store.ExpirationBlockStore;
import org.commonjava.util.logging.Logger;

@ApplicationScoped
public class DefaultExpirationManager
    implements ExpirationManager
{
    private final Logger logger = new Logger( getClass() );

    private final LinkedHashMap<ExpirationKey, Expiration> expirations = new LinkedHashMap<ExpirationKey, Expiration>();

    private final Set<String> currentKeys = new HashSet<String>();

    @Inject
    private Event<ExpirationEvent> eventQueue;

    @Inject
    private ExpirationBlockStore store;

    // NOTE: Not used; Only injected to ensure something is available.
    @Inject
    private ExpirationClockSource clock;

    @Override
    public void clearExpired()
    {
        loadNextExpirations();
        purgeExpired();
    }

    @PreDestroy
    public void saveCurrentBlocks()
        throws ExpirationManagerException
    {
        final Map<String, Set<Expiration>> currentBlocks = new HashMap<String, Set<Expiration>>();
        for ( final Expiration expiration : expirations.values() )
        {
            final String key = generateBlockKey( expiration.getExpires() );
            Set<Expiration> block = currentBlocks.get( key );
            if ( block == null )
            {
                block = new LinkedHashSet<Expiration>();
                currentBlocks.put( key, block );
            }

            block.add( expiration );
        }

        store.writeBlocks( currentBlocks );
    }

    @Override
    public void schedule( final Expiration expiration )
        throws ExpirationManagerException
    {
        final long expires = expiration.getExpires();
        if ( expires < System.currentTimeMillis() )
        {
            eventQueue.fire( new ExpirationEvent( expiration, SCHEDULE ) );
            trigger( expiration );
            return;
        }

        final String key = generateBlockKey( expiration.getExpires() );
        if ( currentKeys.contains( key ) )
        {
            expirations.put( expiration.getKey(), expiration );
        }
        else
        {
            store.addToBlock( key, expiration );
        }

        //        logger.info( "[SCHEDULED] %s, expires: %s", expiration.getKey(), new Date( expiration.getExpires() ) );
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
                remove( expiration );
                //                logger.info( "[CANCELED] %s", expiration.getKey(), new Date( expiration.getExpires() ) );
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
                remove( expiration );
                //                logger.info( "[TRIGGERED] %s", expiration.getKey(), new Date( expiration.getExpires() ) );
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

    @Override
    public void loadedFromStorage( final Collection<Expiration> loaded )
        throws ExpirationManagerException
    {
        for ( final Expiration expiration : loaded )
        {
            final long expires = expiration.getExpires();
            if ( expires < System.currentTimeMillis() )
            {
                eventQueue.fire( new ExpirationEvent( expiration, SCHEDULE ) );
                trigger( expiration );
                return;
            }

            final String key = generateBlockKey( expiration.getExpires() );
            if ( currentKeys.contains( key ) )
            {
                expirations.put( expiration.getKey(), expiration );
            }
            else
            {
                store.addToBlock( key, expiration );
            }
        }
    }

    // FIXME: This only searches the currently loaded blocks.
    protected Set<Expiration> getMatching( final ExpirationMatcher matcher )
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

    protected void loadNextExpirations()
    {
        final String key = generateCurrentBlockKey();
        if ( currentKeys.contains( key ) )
        {
            return;
        }

        logger.info( "Loading batch of expirations for: %s", key );

        try
        {
            final Set<Expiration> expirations = store.getBlock( key );

            if ( expirations != null )
            {
                synchronized ( expirations )
                {
                    int added = 0;
                    for ( final Expiration expiration : expirations )
                    {
                        if ( !expirations.contains( expiration ) )
                        {
                            expirations.add( expiration );
                            added++;
                        }
                    }

                    if ( added > 0 )
                    {
                        expirations.notifyAll();
                    }
                }

                currentKeys.add( key );
            }
        }
        catch ( final ExpirationManagerException e )
        {
            logger.error( "Failed to load expirations from: %s. Reason: %s", e, key, e.getMessage() );
        }
    }

    protected void purgeExpired()
    {
        if ( expirations.isEmpty() )
        {
            return;
        }

        Map<ExpirationKey, Expiration> current;
        synchronized ( expirations )
        {
            current = new HashMap<ExpirationKey, Expiration>( expirations );
        }

        for ( final Map.Entry<ExpirationKey, Expiration> entry : new HashSet<Map.Entry<ExpirationKey, Expiration>>(
                                                                                                                    current.entrySet() ) )
        {
            final ExpirationKey key = entry.getKey();
            final Expiration exp = entry.getValue();
            if ( exp == null )
            {
                continue;
            }

            //                logger.info( "Handling expiration for: %s", key );

            boolean cancel = false;
            if ( !exp.isActive() )
            {
                //                    logger.info( "Expiration no longer active: %s", exp );
                cancel = true;
                return;
            }

            boolean expired = false;
            if ( !cancel )
            {
                expired = exp.getExpires() <= System.currentTimeMillis();

                //                    logger.info( "Checking expiration: %s vs current time: %s. Expired? %s", exp.getExpires(),
                //                                 System.currentTimeMillis(), expired );

                if ( expired )
                {
                    try
                    {
                        //                            logger.info( "\n\n\n [%s] TRIGGERING: %s (expiration timeout: %s)\n\n\n",
                        //                                         System.currentTimeMillis(), exp, exp.getExpires() );

                        trigger( exp );
                    }
                    catch ( final ExpirationManagerException e )
                    {
                        logger.error( "Failed to trigger expiration: %s. Reason: %s", e, key, e.getMessage() );

                        cancel = true;
                    }
                }
            }

            if ( cancel )
            {
                //                    logger.info( "Canceling: %s", key );
                try
                {
                    cancel( exp );
                }
                catch ( final ExpirationManagerException e )
                {
                    logger.error( "Failed to cancel expiration: %s. Reason: %s", e, key, e.getMessage() );
                }
            }

            if ( cancel || expired )
            {
                synchronized ( expirations )
                {
                    //                        logger.info( "Removing handled expiration: %s", key );
                    try
                    {
                        remove( exp );
                    }
                    catch ( final ExpirationManagerException e )
                    {
                        logger.error( "Failed to remove expiration: %s. Reason: %s", e, key, e.getMessage() );
                    }

                    current.remove( key );
                }
            }
        }
    }

    protected void remove( final Expiration expiration )
        throws ExpirationManagerException
    {
        final Expiration removed = expirations.remove( expiration.getKey() );
        if ( removed == null )
        {
            final String key = generateBlockKey( expiration.getExpires() );
            store.removeFromBlock( key, expiration );
        }
        else if ( expirations.isEmpty() )
        {
            store.removeBlocks( currentKeys );
            currentKeys.clear();
        }
    }

}
