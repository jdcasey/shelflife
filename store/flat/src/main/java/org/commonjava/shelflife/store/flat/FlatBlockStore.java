package org.commonjava.shelflife.store.flat;

import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Executor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.commonjava.cdi.util.weft.ExecutorConfig;
import org.commonjava.cdi.util.weft.PeriodicRunnable;
import org.commonjava.shelflife.ExpirationManagerException;
import org.commonjava.shelflife.inject.Shelflife;
import org.commonjava.shelflife.model.Expiration;
import org.commonjava.shelflife.store.ExpirationBlockStore;
import org.commonjava.web.json.ser.JsonSerializer;

import com.google.gson.reflect.TypeToken;

@ApplicationScoped
public class FlatBlockStore
    implements ExpirationBlockStore
{

    private static final String ENCODING = "UTF-8";

    @Inject
    @Shelflife
    private JsonSerializer serializer;

    @Inject
    private FlatBlockStoreConfiguration config;

    @Inject
    @ExecutorConfig( daemon = true, named = "shelflife-flushcache", threads = 1, priority = 6 )
    private Executor executor;

    private final Map<String, Set<Expiration>> cache = new HashMap<String, Set<Expiration>>();

    public FlatBlockStore()
    {
    }

    public FlatBlockStore( final FlatBlockStoreConfiguration config, final JsonSerializer serializer,
                           final Executor executor )
    {
        this.config = config;
        this.serializer = serializer;
        this.executor = executor;
        startCacheManager();
    }

    @PostConstruct
    public void startCacheManager()
    {
        executor.execute( new CacheFlusher( config.getCacheFlushMillis(), executor ) );
    }

    @Override
    public void writeBlocks( final Map<String, Set<Expiration>> currentBlocks )
        throws ExpirationManagerException
    {
        for ( final Map.Entry<String, Set<Expiration>> entry : currentBlocks.entrySet() )
        {
            putBlock( entry.getKey(), entry.getValue() );
        }
    }

    @Override
    public void addToBlock( final String key, final Expiration expiration )
        throws ExpirationManagerException
    {
        Set<Expiration> block = getBlock( key );
        if ( block == null )
        {
            block = new TreeSet<Expiration>();
        }

        if ( block.add( expiration ) )
        {
            putBlock( key, block );
        }
    }

    @Override
    public Set<Expiration> getBlock( final String key )
        throws ExpirationManagerException
    {
        Set<Expiration> block = cache.get( key );
        if ( block == null )
        {
            final File f = getBlockFile( key );
            if ( f.exists() )
            {
                FileInputStream stream = null;
                try
                {
                    stream = new FileInputStream( f );

                    final TypeToken<List<Expiration>> token = new TypeToken<List<Expiration>>()
                    {
                    };

                    final List<Expiration> listing = serializer.fromStream( stream, ENCODING, token );
                    block = new TreeSet<Expiration>( listing );
                }
                catch ( final IOException e )
                {
                    throw new ExpirationManagerException( "Failed to read block from: %s. Reason: %s", e, f,
                                                          e.getMessage() );
                }
                finally
                {
                    closeQuietly( stream );
                }
            }
        }

        return block;
    }

    @Override
    public void removeFromBlock( final String key, final Expiration expiration )
        throws ExpirationManagerException
    {
        final Set<Expiration> block = getBlock( key );
        if ( block != null && block.remove( expiration ) )
        {
            putBlock( key, block );
        }
    }

    private void putBlock( final String key, final Set<Expiration> block )
    {
        cache.put( key, block );
    }

    @PreDestroy
    public synchronized void flushCaches()
        throws ExpirationManagerException
    {
        final Set<String> keys = new HashSet<String>( cache.keySet() );
        for ( final String key : keys )
        {
            final Set<Expiration> block = cache.get( key );
            writeBlock( key, block );
            cache.remove( key );
        }
    }

    private void writeBlock( final String key, final Set<Expiration> block )
        throws ExpirationManagerException
    {
        final File f = getBlockFile( key );
        final File d = f.getParentFile();
        if ( d != null && !d.isDirectory() )
        {
            d.mkdirs();
        }

        final List<Expiration> sortedBlock = new ArrayList<Expiration>( block );
        Collections.sort( sortedBlock );

        FileWriter writer = null;
        try
        {
            writer = new FileWriter( f );
            final String json = serializer.toString( sortedBlock );

            writer.write( json );
        }
        catch ( final IOException e )
        {
            throw new ExpirationManagerException( "Failed to write expiration block to: %s. Reason: %s", e, f,
                                                  e.getMessage() );
        }
        finally
        {
            closeQuietly( writer );
        }
    }

    @Override
    public void removeBlocks( final Set<String> currentKeys )
        throws ExpirationManagerException
    {
        for ( final String key : currentKeys )
        {
            cache.remove( key );
            final File f = getBlockFile( key );
            if ( f.exists() )
            {
                f.delete();
            }
        }
    }

    @Override
    public void removeBlocks( final String... currentKeys )
        throws ExpirationManagerException
    {
        for ( final String key : currentKeys )
        {
            cache.remove( key );
            final File f = getBlockFile( key );
            if ( f.exists() )
            {
                f.delete();
            }
        }
    }

    private File getBlockFile( final String key )
    {
        final String fname = "expiration-block-" + key + ".json";

        config.getStorageDirectory()
              .mkdirs();

        return new File( config.getStorageDirectory(), fname );
    }

    private final class CacheFlusher
        extends PeriodicRunnable
    {
        public CacheFlusher( final long period, final Executor executor )
        {
            super( period, true, executor );
        }

        @Override
        protected void onPeriodExpire()
        {
            try
            {
                logger.debug( "Flushing block caches to disk." );
                flushCaches();
            }
            catch ( final ExpirationManagerException e )
            {
                logger.error( "Failed to write cached blocks to disk: %s", e, e.getMessage() );
            }
        }

    }
}
