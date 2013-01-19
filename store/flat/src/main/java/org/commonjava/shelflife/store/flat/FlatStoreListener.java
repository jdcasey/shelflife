package org.commonjava.shelflife.store.flat;

import static org.apache.commons.codec.digest.DigestUtils.md5Hex;
import static org.apache.commons.io.FileUtils.write;
import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.commonjava.shelflife.expire.ExpirationEvent;
import org.commonjava.shelflife.expire.ExpirationEventType;
import org.commonjava.shelflife.expire.ExpirationManager;
import org.commonjava.shelflife.expire.ExpirationManagerException;
import org.commonjava.shelflife.inject.Shelflife;
import org.commonjava.shelflife.model.Expiration;
import org.commonjava.shelflife.util.ChangeSynchronizer;
import org.commonjava.util.logging.Logger;
import org.commonjava.web.json.ser.JsonSerializer;

@javax.enterprise.context.ApplicationScoped
public class FlatStoreListener
{

    private static final String ENCODING = "UTF-8";

    private final Logger logger = new Logger( getClass() );

    @Inject
    private ExpirationManager manager;

    @Inject
    @Shelflife
    private JsonSerializer serializer;

    @Inject
    private FlatShelflifeStoreConfiguration config;

    private final ChangeSynchronizer changeSync = new ChangeSynchronizer();

    @PostConstruct
    public void loadExpirations()
    {
        final Set<Expiration> expirations = loadAll();
        try
        {
            manager.loadedFromStorage( expirations );
        }
        catch ( final ExpirationManagerException e )
        {
            logger.error( "Failed to restore expirations from directory: %s. Reason: %s", e,
                          config.getStorageDirectory(), e.getMessage() );
        }
    }

    public void handleExpirationEvent( @Observes final ExpirationEvent event )
    {
        final ExpirationEventType type = event.getType();
        switch ( type )
        {
            case SCHEDULE:
            {
                store( event.getExpiration() );
                changeSync.addChanged();
                break;
            }

            case CANCEL:
            case EXPIRE:
            {
                delete( event.getExpiration() );
                changeSync.addChanged();
                break;
            }

            default:
            {
                logger.warn( "Unknown event type: %s", type );
            }
        }
    }

    private void store( final Expiration expiration )
    {
        final File f = getFile( expiration );

        final String json = serializer.toString( expiration );

        try
        {
            logger.info( "Writing expiration information: %s to: %s", expiration, f );
            write( f, json, ENCODING );
        }
        catch ( final IOException e )
        {
            logger.error( "Failed to store expiration: %s in: %s. Reason: %s", e, expiration.getKey(),
                          config.getStorageDirectory(), e.getMessage() );
        }
    }

    public File getFile( final Expiration expiration )
    {
        final String fname = "exp-" + md5Hex( expiration.getKey()
                                                        .toString() ) + ".json";

        config.getStorageDirectory()
              .mkdirs();

        return new File( config.getStorageDirectory(), fname );
    }

    private void delete( final Expiration expiration )
    {
        final File f = getFile( expiration );
        logger.info( "Deleting expiration information: %s from: %s", expiration, f );
        f.delete();
    }

    private Set<Expiration> loadAll()
    {
        final File[] files = config.getStorageDirectory()
                                   .listFiles( new FilenameFilter()
                                   {

                                       @Override
                                       public boolean accept( final File dir, final String name )
                                       {
                                           return name.startsWith( "exp-" ) && name.endsWith( ".json" );
                                       }
                                   } );

        final Set<Expiration> loaded = new LinkedHashSet<Expiration>();
        if ( files != null )
        {
            for ( final File file : files )
            {
                FileInputStream in = null;
                try
                {
                    in = new FileInputStream( file );
                    final Expiration exp = serializer.fromStream( in, ENCODING, Expiration.class );

                    loaded.add( exp );
                }
                catch ( final IOException e )
                {
                    logger.error( "Failed to load expiration from: %s. Reason: %s", e, file, e.getMessage() );
                }
                finally
                {
                    closeQuietly( in );
                }
            }
        }

        return loaded;
    }

    public int waitForEvents( final int count, final int timeout, final int poll )
    {
        return changeSync.waitForChange( count, timeout, poll );
    }

    public int waitForEvents( final int timeout, final int poll )
    {
        return changeSync.waitForChange( 1, timeout, poll );
    }
}
