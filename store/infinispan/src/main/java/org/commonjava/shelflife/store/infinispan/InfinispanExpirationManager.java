package org.commonjava.shelflife.store.infinispan;

import static org.commonjava.shelflife.expire.ExpirationEventType.CANCEL;
import static org.commonjava.shelflife.expire.ExpirationEventType.EXPIRE;
import static org.commonjava.shelflife.expire.ExpirationEventType.SCHEDULE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.lucene.search.Query;
import org.commonjava.shelflife.expire.ExpirationEvent;
import org.commonjava.shelflife.expire.ExpirationManager;
import org.commonjava.shelflife.expire.ExpirationManagerException;
import org.commonjava.shelflife.expire.match.ExpirationMatcher;
import org.commonjava.shelflife.model.Expiration;
import org.commonjava.shelflife.model.ExpirationKey;
import org.commonjava.util.logging.Logger;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.infinispan.Cache;
import org.infinispan.query.CacheQuery;
import org.infinispan.query.Search;
import org.infinispan.query.SearchManager;

@Singleton
public class InfinispanExpirationManager
    implements ExpirationManager
{

    private static final long NEXT_EXPIRATION_BATCH_OFFSET = TimeUnit.MILLISECONDS.convert( 5, TimeUnit.MINUTES );

    private final Logger logger = new Logger( getClass() );

    private final Timer timer = new Timer( true );

    private final List<Expiration> currentExpirations = new ArrayList<Expiration>();

    @Inject
    private Event<ExpirationEvent> eventQueue;

    @Inject
    @ShelflifeCache
    private Cache<ExpirationKey, Expiration> expirationCache;

    @PostConstruct
    protected void startLoader()
    {
        timer.schedule( new LoadNextExpirationsTask( expirationCache, currentExpirations ), 0,
                        NEXT_EXPIRATION_BATCH_OFFSET );
    }

    @PreDestroy
    protected void stopLoader()
    {
        timer.cancel();
    }

    @Override
    public void schedule( final Expiration expiration )
        throws ExpirationManagerException
    {
        final long expires = expiration.getExpires() - System.currentTimeMillis();
        if ( expires < NEXT_EXPIRATION_BATCH_OFFSET )
        {
            synchronized ( currentExpirations )
            {
                currentExpirations.add( expiration );
            }
            timer.schedule( new ExpirationTask( expiration ), expires );
        }

        expirationCache.put( expiration.getKey(), expiration );

        logger.info( "[SCHEDULED] %s, expires: %s\nCurrent time: %s", expiration.getKey(),
                     new Date( expiration.getExpires() ), new Date() );
        eventQueue.fire( new ExpirationEvent( expiration, SCHEDULE ) );
    }

    @Override
    public void cancel( final Expiration expiration )
        throws ExpirationManagerException
    {
        synchronized ( expiration )
        {
            if ( expiration.isActive() && contains( expiration ) )
            {
                expiration.cancel();

                synchronized ( currentExpirations )
                {
                    currentExpirations.remove( expiration );
                }

                expirationCache.remove( expiration.getKey() );

                logger.info( "[CANCELED] %s at: %s", expiration.getKey(), new Date() );
                eventQueue.fire( new ExpirationEvent( expiration, CANCEL ) );
            }
        }
    }

    @Override
    public void trigger( final Expiration expiration )
        throws ExpirationManagerException
    {
        if ( expiration.isActive() && contains( expiration ) )
        {
            expiration.expire();

            synchronized ( currentExpirations )
            {
                currentExpirations.remove( expiration );
            }

            expirationCache.remove( expiration.getKey() );

            logger.info( "[TRIGGERED] %s at: %s", expiration.getKey(), new Date() );
            eventQueue.fire( new ExpirationEvent( expiration, EXPIRE ) );
        }
    }

    @Override
    public void triggerAll()
        throws ExpirationManagerException
    {
        logger.debug( "[TRIGGER] ALL" );
        for ( final Expiration exp : all() )
        {
            trigger( exp );
        }
    }

    @Override
    public void triggerAll( final ExpirationMatcher matcher )
        throws ExpirationManagerException
    {
        logger.debug( "[TRIGGER] ALL" );
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
        logger.debug( "[CANCEL] ALL" );
        for ( final Expiration exp : all() )
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
    public void loadedFromStorage( final Collection<Expiration> expirations )
        throws ExpirationManagerException
    {
        for ( final Expiration expiration : expirations )
        {
            this.currentExpirations.add( expiration );
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
    {
        return currentExpirations.contains( expiration ) || expirationCache.containsKey( expiration.getKey() );
    }

    private Set<Expiration> getMatching( final ExpirationMatcher matcher )
    {
        final Set<Expiration> matching = new LinkedHashSet<Expiration>();
        for ( final Expiration exp : all() )
        {
            if ( matcher.matches( exp ) )
            {
                matching.add( exp );
            }
        }

        return matching;
    }

    private Set<Expiration> all()
    {
        final Set<Expiration> result = new HashSet<Expiration>();
        for ( final Entry<ExpirationKey, Expiration> entry : expirationCache.entrySet() )
        {
            result.add( entry.getValue() );
        }

        return result;
    }

    public final class LoadNextExpirationsTask
        extends TimerTask
    {
        private final Cache<ExpirationKey, Expiration> expirationCache;

        private final List<Expiration> currentExpirations;

        LoadNextExpirationsTask( final Cache<ExpirationKey, Expiration> expirationCache,
                                 final List<Expiration> currentExpirations )
        {
            this.expirationCache = expirationCache;
            this.currentExpirations = currentExpirations;
        }

        @Override
        public void run()
        {
            System.out.println( "Loading next expirations from cache: " + expirationCache );
            final SearchManager searchManager = Search.getSearchManager( expirationCache );

            final QueryBuilder qb = searchManager.buildQueryBuilderForClass( Expiration.class )
                                                 .get();
            final Query query = qb.range()
                                  .onField( "expires" )
                                  .below( System.currentTimeMillis() + NEXT_EXPIRATION_BATCH_OFFSET )
                                  .createQuery();

            final CacheQuery cq = searchManager.getQuery( query, Expiration.class );

            final List<Object> list = cq.list();
            for ( final Object object : list )
            {
                final Expiration exp = (Expiration) object;
                synchronized ( currentExpirations )
                {
                    currentExpirations.add( exp );
                }
            }

        }
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
                    InfinispanExpirationManager.this.cancel( expiration );
                }
                catch ( final ExpirationManagerException eC )
                {
                    logger.error( "Cannot cancel failed expiration: %s. Reason: %s", eC, expiration.getKey(),
                                  eC.getMessage() );
                }
            }
        }
    }

}
