package org.commonjava.shelflife.store.infinispan.inject;

import java.util.Set;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.commonjava.shelflife.model.Expiration;
import org.commonjava.shelflife.model.ExpirationKey;
import org.commonjava.util.logging.Logger;
import org.infinispan.Cache;
import org.infinispan.manager.CacheContainer;

@Singleton
public class ShelflifeCacheProvider
{

    private final Logger logger = new Logger( getClass() );

    @Inject
    private CacheContainer container;

    @Produces
    @ShelflifeCache( ShelflifeCaches.BLOCKS )
    public Cache<String, Set<ExpirationKey>> getBlockCache()
    {
        return getCache( ShelflifeCaches.BLOCKS.cacheName() );
    }

    @Produces
    @ShelflifeCache( ShelflifeCaches.DATA )
    public Cache<ExpirationKey, Expiration> getDataCache()
    {
        return getCache( ShelflifeCaches.DATA.cacheName() );
    }

    public <K, V> Cache<K, V> getCache( final String name )
    {
        logger.info( "\n\n\n\nLooking for cache...\n\n\n\n" );
        logger.info( "Retrieving cache: %s", name );

        final Cache<K, V> cache = container.getCache( name );
        cache.start();

        return cache;
    }

}
