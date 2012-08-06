package org.commonjava.shelflife.store.infinispan.fixture;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.commonjava.shelflife.model.Expiration;
import org.commonjava.shelflife.model.ExpirationKey;
import org.commonjava.shelflife.store.infinispan.ShelflifeCache;
import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

@Singleton
public class TestConfigProvider
{

    private Cache<ExpirationKey, Expiration> cache;

    private EmbeddedCacheManager cacheManager;

    private File configFile;

    @PostConstruct
    public void startCacheManager()
        throws IOException
    {
        cacheManager = new DefaultCacheManager( getConfigStream() );
        cacheManager.start();
        cacheManager.startCaches( "shelflife" );

        cache = cacheManager.getCache( "shelflife" );
    }

    @PreDestroy
    public void clearConfigFile()
        throws IOException
    {
        if ( configFile != null && configFile.exists() )
        {
            FileUtils.forceDelete( configFile );
        }
    }

    private InputStream getConfigStream()
        throws IOException
    {
        final InputStream ispnXml = Thread.currentThread()
                                          .getContextClassLoader()
                                          .getResourceAsStream( "infinispan.xml" );

        configFile = File.createTempFile( "infinispan.", ".xml" );

        final String raw = IOUtils.toString( ispnXml );
        final String config = raw.replace( "${this.file}", configFile.getAbsolutePath() );

        System.out.println( "Writing temp config to: " + configFile + "\n\n" + config + "\n\n" );

        FileUtils.write( configFile, config );

        final InputStream stream = new ByteArrayInputStream( config.getBytes() );
        return stream;
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
