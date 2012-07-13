package org.commonjava.shelflife.store.infinispan.fixture;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;

import org.commonjava.shelflife.model.Expiration;
import org.commonjava.shelflife.model.ExpirationKey;
import org.commonjava.shelflife.store.infinispan.ShelflifeCache;
import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

public class TestConfigProvider
{

    private Cache<ExpirationKey, Expiration> cache;

    private EmbeddedCacheManager cacheManager;

    @PostConstruct
    public void startCacheManager()
        throws IOException
    {
        cacheManager = new DefaultCacheManager( Thread.currentThread()
                                                      .getContextClassLoader()
                                                      .getResourceAsStream( "infinispan.xml" ) );
        cacheManager.start();
        cacheManager.startCaches( "shelflife" );

        cache = cacheManager.getCache( "shelflife" );
    }

    public void stopCacheManager()
    {
        if ( cache != null )
        {
            cache.stop();
        }

        if ( cacheManager != null )
        {
            cacheManager.stop();
        }
    }

    @Produces
    @ShelflifeCache
    public synchronized Cache<ExpirationKey, Expiration> getShelflifeCache()
        throws IOException
    {
        return cache;
    }

}
