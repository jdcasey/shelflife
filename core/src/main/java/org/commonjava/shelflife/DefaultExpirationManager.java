package org.commonjava.shelflife;

import static org.commonjava.shelflife.event.ExpirationEventType.CANCEL;
import static org.commonjava.shelflife.event.ExpirationEventType.EXPIRE;
import static org.commonjava.shelflife.event.ExpirationEventType.SCHEDULE;
import static org.commonjava.shelflife.util.BlockKeyUtils.generateBlockKey;
import static org.commonjava.shelflife.util.BlockKeyUtils.generateCurrentBlockKey;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.commonjava.shelflife.clock.ExpirationClockSource;
import org.commonjava.shelflife.event.ExpirationEventManager;
import org.commonjava.shelflife.event.ExpirationEventType;
import org.commonjava.shelflife.match.ExpirationMatcher;
import org.commonjava.shelflife.model.Expiration;
import org.commonjava.shelflife.model.ExpirationKey;
import org.commonjava.shelflife.store.ExpirationBlockStore;
import org.commonjava.util.logging.Logger;

@ApplicationScoped
public class DefaultExpirationManager
    implements ExpirationManager
{
    private final Logger logger = new Logger( getClass() );

    private final Set<Expiration> expirations = new TreeSet<>();

    private final Set<String> currentKeys = new HashSet<String>();

    @Inject
    private ExpirationBlockStore store;

    @Inject
    private ExpirationClockSource clock;

    @Inject
    private ExpirationEventManager events;

    public DefaultExpirationManager()
    {
    }

    public DefaultExpirationManager( final ExpirationBlockStore store, final ExpirationClockSource clock, final ExpirationEventManager events )
        throws ExpirationManagerException
    {
        this.store = store;
        this.clock = clock;
        this.events = events;
        startClock();
    }

    @PostConstruct
    public void startClock()
        throws ExpirationManagerException
    {
        logger.info( "Starting expiration manager with clock: %s", clock );
        loadNextExpirations();
        clock.start( this );
    }

    @Override
    public void clearExpired()
    {
        logger.debug( "Loading next block of events, if appropriate" );
        loadNextExpirations();
        logger.debug( "Handling expired events" );
        purgeExpired();
    }

    @PreDestroy
    public void saveCurrentBlocks()
        throws ExpirationManagerException
    {
        final Map<String, Set<Expiration>> currentBlocks = new HashMap<String, Set<Expiration>>();
        for ( final Expiration expiration : new TreeSet<>( expirations ) )
        {
            final String key = generateBlockKey( expiration.getExpires() );
            Set<Expiration> block = currentBlocks.get( key );
            if ( block == null )
            {
                block = new TreeSet<Expiration>();
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
            logger.debug( "IMMEDIATELY triggering: %s", expiration );
            fire( expiration, SCHEDULE );
            trigger( expiration );
            return;
        }

        logger.debug( "Current block keys are: %s", currentKeys );

        final String key = generateBlockKey( expiration.getExpires() );
        if ( currentKeys.contains( key ) )
        {
            logger.debug( "Adding %s to current expirations block in memory.", expiration );
            synchronized ( expirations )
            {
                expirations.add( expiration );
            }
        }
        else
        {
            logger.debug( "Adding %s to future expirations block: %s.", expiration, key );
            store.addToBlock( key, expiration );
        }

        expiration.schedule();

        //        logger.info( "[SCHEDULED] %s, expires: %s", expiration.getKey(), new Date( expiration.getExpires() ) );
        fire( expiration, SCHEDULE );
    }

    private void fire( final Expiration expiration, final ExpirationEventType type )
    {
        logger.debug( "Firing %s for: %s", type, expiration );
        events.fire( expiration, type );
    }

    @Override
    public void cancel( final Expiration expiration )
        throws ExpirationManagerException
    {
        if ( expiration.isActive() && expirations.contains( expiration ) )
        {
            expiration.cancel();
            remove( expiration );
            logger.info( "[CANCELED] %s", expiration.getKey(), new Date( expiration.getExpires() ) );
            fire( expiration, CANCEL );
        }
    }

    @Override
    public void cancel( final ExpirationKey key )
        throws ExpirationManagerException
    {
        Expiration expiration = null;
        for ( final Expiration e : new TreeSet<Expiration>( expirations ) )
        {
            if ( e.getKey()
                  .equals( key ) )
            {
                expiration = e;
                break;
            }
        }

        if ( expiration != null )
        {
            cancel( expiration );
        }
    }

    @Override
    public void trigger( final ExpirationKey key )
        throws ExpirationManagerException
    {
        Expiration expiration = null;
        for ( final Expiration e : new TreeSet<Expiration>( expirations ) )
        {
            if ( e.getKey()
                  .equals( key ) )
            {
                expiration = e;
                break;
            }
        }

        if ( expiration != null )
        {
            trigger( expiration );
        }
    }

    @Override
    public void trigger( final Expiration expiration )
        throws ExpirationManagerException
    {
        logger.info( "Attempting to trigger: %s", expiration.getKey() );
        synchronized ( expiration )
        {
            if ( expiration.isActive() && expirations.contains( expiration ) )
            {
                expiration.expire();
                remove( expiration );
                logger.info( "[TRIGGERED] %s at: %s", expiration.getKey(), new Date( expiration.getExpires() ) );
                fire( expiration, EXPIRE );
            }
        }
    }

    @Override
    public void triggerAll()
        throws ExpirationManagerException
    {
        logger.info( "Triggering all %d expirations:\n\n%s", expirations.size(), new TreeSet<Expiration>( expirations ) );
        for ( final Expiration exp : new TreeSet<Expiration>( expirations ) )
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
        for ( final Expiration exp : new TreeSet<Expiration>( expirations ) )
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

    // TODO: Only searches currently-loaded expirations!
    @Override
    public boolean contains( final Expiration expiration )
        throws ExpirationManagerException
    {
        return expirations.contains( expiration );
    }

    // TODO: Only searches currently-loaded expirations!
    @Override
    public boolean hasExpiration( final ExpirationKey key )
    {
        for ( final Expiration e : new TreeSet<Expiration>( expirations ) )
        {
            if ( key.equals( e.getKey() ) )
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public void loadedFromStorage( final Collection<Expiration> loaded )
        throws ExpirationManagerException
    {
        logger.debug( "Loading %d expirations from storage", loaded.size() );
        final Map<Expiration, String> toAdd = new HashMap<>();
        final Map<Expiration, String> toStore = new HashMap<>();
        for ( final Expiration expiration : loaded )
        {
            final long expires = expiration.getExpires();
            if ( expires < System.currentTimeMillis() )
            {
                logger.debug( "IMMEDIATELY firing %s (already expired)", expiration );
                fire( expiration, SCHEDULE );
                trigger( expiration );
                return;
            }

            final String key = generateBlockKey( expiration.getExpires() );
            logger.debug( "block key is: %s\ncurrently loaded blocks are: %s", key, currentKeys );
            if ( currentKeys.contains( key ) )
            {
                logger.debug( "Adding to current expirations: %s", expiration );
                toAdd.put( expiration, key );
            }
            else
            {
                logger.debug( "adding to future expiration block: %s", expiration );
                toStore.put( expiration, key );
            }

            expiration.schedule();
        }

        expirations.addAll( toAdd.keySet() );
        currentKeys.addAll( toAdd.values() );

        for ( final Entry<Expiration, String> entry : toStore.entrySet() )
        {
            final Expiration exp = entry.getKey();
            final String key = entry.getValue();

            store.addToBlock( key, exp );
        }
    }

    // FIXME: This only searches the currently loaded blocks.
    protected Set<Expiration> getMatching( final ExpirationMatcher matcher )
    {
        final Set<Expiration> matching = new TreeSet<Expiration>();
        for ( final Expiration exp : new TreeSet<Expiration>( expirations ) )
        {
            if ( matcher.matches( exp ) )
            {
                matching.add( exp );
            }
        }

        return matching;
    }

    @Override
    public void loadNextExpirations()
    {
        final String currentKey = generateCurrentBlockKey();
        List<String> keys = store.listKeysInOrder();

        synchronized ( currentKeys )
        {
            // force-load the current key into the list.
            currentKeys.add( currentKey );
        }

        if ( keys == null )
        {
            return;
        }

        final int idx = keys.indexOf( currentKey );
        if ( idx > -1 && idx < keys.size() - 1 )
        {
            keys = keys.subList( 0, idx + 1 );
        }

        logger.info( "Loading initial expiration blocks: %s", keys );

        for ( final String key : keys )
        {
            loadExpirations( key );
        }
    }

    protected void loadExpirations( final String key )
    {
        logger.debug( "current block key is: %s", key );
        synchronized ( currentKeys )
        {
            if ( currentKeys.contains( key ) )
            {
                return;
            }

            currentKeys.add( key );
        }

        logger.debug( "Loading batch of expirations for: %s", key );
        try
        {
            final Set<Expiration> expirations = store.getBlock( key );

            if ( expirations != null )
            {
                int added = 0;
                synchronized ( expirations )
                {
                    for ( final Expiration expiration : expirations )
                    {
                        if ( this.expirations.add( expiration ) )
                        {
                            added++;
                        }
                    }
                }

                logger.debug( "Added %d new expirations from block: %s", added, key );

                if ( added > 0 )
                {
                    synchronized ( this )
                    {
                        notifyAll();
                    }
                }
            }
        }
        catch ( final ExpirationManagerException e )
        {
            logger.error( "Failed to load expirations from: %s. Reason: %s", e, key, e.getMessage() );
        }
    }

    // TODO: Split expirations up into blocks and parallelize the purge.
    protected void purgeExpired()
    {
        if ( expirations.isEmpty() )
        {
            return;
        }

        final Set<Expiration> current = new TreeSet<Expiration>( expirations );

        logger.info( "Checking %d expirations", current.size() );
        for ( final Expiration exp : current )
        {
            if ( exp == null )
            {
                continue;
            }

            final ExpirationKey key = exp.getKey();
            logger.debug( "Checking expiration for: %s", key );

            boolean cancel = false;
            if ( !exp.isActive() )
            {
                logger.info( "Expiration no longer active: %s", exp );
                cancel = true;
            }

            boolean expired = false;
            if ( !cancel )
            {
                expired = exp.getExpires() <= System.currentTimeMillis();

                logger.info( "Checking expiration: %s vs current time: %s. Expired? %s", exp.getExpires(), System.currentTimeMillis(), expired );

                if ( expired )
                {
                    try
                    {
                        logger.info( "\n\n\n [%s] TRIGGERING: %s (expiration timeout: %s)\n\n\n", System.currentTimeMillis(), exp, exp.getExpires() );

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
                logger.info( "Canceling: %s", key );
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
                logger.info( "Removing handled expiration: %s", key );
                try
                {
                    remove( exp );
                }
                catch ( final ExpirationManagerException e )
                {
                    logger.error( "Failed to remove expiration: %s. Reason: %s", e, key, e.getMessage() );
                }
            }
        }
    }

    protected void remove( final Expiration expiration )
        throws ExpirationManagerException
    {
        if ( expirations.remove( expiration ) )
        {
            final String key = generateBlockKey( expiration.getExpires() );
            store.removeFromBlock( key, expiration );
        }
    }

    @Override
    public String toString()
    {
        return String.format( "DefaultExpirationManager@%s [\n  Event Manager: %s\n  Clock Source: %s\n  Block Store: %s\n]", hashCode(), events,
                              clock, store );
    }

}
