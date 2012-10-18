package org.commonjava.shelflife.store.infinispan.fixture;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;

import org.commonjava.util.logging.Logger;
import org.infinispan.manager.CacheContainer;
import org.infinispan.manager.DefaultCacheManager;

@javax.enterprise.context.ApplicationScoped
public class TestConfigProvider
{

    private final Logger logger = new Logger( getClass() );

    private CacheContainer cacheManager;

    @PostConstruct
    public void startCacheManager()
        throws IOException
    {
        logger.info( "\n\n\n\nCache manager starting...\n\n\n\n" );
        cacheManager = new DefaultCacheManager();
        cacheManager.start();
    }

    public void stopCacheManager()
    {
        if ( cacheManager != null )
        {
            cacheManager.stop();
        }
    }

    @Produces
    public CacheContainer getCacheContainer()
    {
        return cacheManager;
    }
}
