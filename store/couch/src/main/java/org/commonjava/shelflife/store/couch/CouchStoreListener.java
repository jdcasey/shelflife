package org.commonjava.shelflife.store.couch;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.commonjava.couch.db.CouchDBException;
import org.commonjava.couch.db.CouchManager;
import org.commonjava.shelflife.expire.ExpirationEvent;
import org.commonjava.shelflife.expire.ExpirationEventType;
import org.commonjava.shelflife.expire.ExpirationManager;
import org.commonjava.shelflife.expire.ExpirationManagerException;
import org.commonjava.shelflife.inject.Shelflife;
import org.commonjava.shelflife.model.Expiration;
import org.commonjava.shelflife.util.ChangeSynchronizer;
import org.commonjava.util.logging.Logger;
import org.commonjava.web.json.ser.JsonSerializer;

@Singleton
public class CouchStoreListener
{

    private final Logger logger = new Logger( getClass() );

    @Inject
    private ExpirationManager manager;

    @Inject
    @Shelflife
    private CouchManager couch;

    @Inject
    private JsonSerializer serializer;

    private final ChangeSynchronizer changeSync = new ChangeSynchronizer();

    @PostConstruct
    public void loadExpirations()
    {
        serializer.registerSerializationAdapters( new ExpirationWrapperSerializer() );

        initCouch();

        final Set<Expiration> expirations = loadAll();
        try
        {
            manager.loadedFromStorage( expirations );
        }
        catch ( final ExpirationManagerException e )
        {
            logger.error( "Failed to restore expirations from CouchDB. Reason: %s", e, e.getMessage() );
        }
    }

    public void initCouch()
    {
        final ShelflifeApp app = new ShelflifeApp();
        try
        {
            if ( couch.dbExists() )
            {
                // static in Couch, so safe to forcibly reload.
                couch.delete( app );
            }

            couch.initialize( app.getDescription() );
        }
        catch ( final CouchDBException e )
        {
            logger.error( "Failed to initialize CouchDB database for storing expiration information. Reason: %s", e,
                          e.getMessage() );
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

    public int waitForEvents( final int count, final int timeout, final int poll )
    {
        return changeSync.waitForChange( count, timeout, poll );
    }

    public int waitForEvents( final int timeout, final int poll )
    {
        return changeSync.waitForChange( 1, timeout, poll );
    }

    private void store( final Expiration expiration )
    {
        logger.info( "[STORING] %s", expiration );
        try
        {
            couch.store( new ExpirationWrapperDoc( expiration ), true );
        }
        catch ( final CouchDBException e )
        {
            logger.error( "Failed to store expiration: %s in CouchDB. Reason: %s", e, expiration.getKey(),
                          e.getMessage() );
        }
        logger.info( "[STORED] %s", expiration );
    }

    private void delete( final Expiration expiration )
    {
        logger.info( "[DELETING] %s", expiration );
        try
        {
            couch.delete( new ExpirationDocRef( expiration ) );
        }
        catch ( final CouchDBException e )
        {
            logger.error( "Failed to delete expiration: %s from CouchDB. Reason: %s", e, expiration.getKey(),
                          e.getMessage() );
        }
        logger.info( "[DELETED] %s", expiration );
    }

    private Set<Expiration> loadAll()
    {
        List<ExpirationWrapperDoc> docs = null;
        try
        {
            docs =
                couch.getViewListing( new ExpirationViewRequest( ShelflifeApp.ShelflifeViews.ALL_EXPIRATIONS ),
                                      ExpirationWrapperDoc.class );
        }
        catch ( final CouchDBException e )
        {
            logger.error( "Failed to load stored expirations from CouchDB. Reason: %s", e, e.getMessage() );
        }

        final Set<Expiration> loaded = new LinkedHashSet<Expiration>();
        if ( docs != null )
        {
            for ( final ExpirationWrapperDoc doc : docs )
            {
                loaded.add( doc.getExpiration() );
            }
        }

        return loaded;
    }

}
