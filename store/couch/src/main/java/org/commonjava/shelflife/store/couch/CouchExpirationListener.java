package org.commonjava.shelflife.store.couch;

import java.util.Set;
import java.util.concurrent.ExecutorService;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.commonjava.couch.db.CouchManager;
import org.commonjava.shelflife.expire.ExpirationEvent;
import org.commonjava.shelflife.expire.ExpirationEventType;
import org.commonjava.shelflife.expire.ExpirationManager;
import org.commonjava.shelflife.expire.ExpirationManagerException;
import org.commonjava.shelflife.model.Expiration;
import org.commonjava.util.logging.Logger;

public class CouchExpirationListener
{

    private final Logger logger = new Logger( getClass() );

    @Inject
    private ExpirationManager manager;

    @Inject
    @ShelflifeStore
    private ExecutorService storageExecutor;

    @Inject
    @ShelflifeStore
    private CouchManager couch;

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
            logger.warn( "Failed to restore expirations from CouchDB. Reason: %s", e, e.getMessage() );
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
                break;
            }

            case CANCEL:
            case EXPIRE:
            {
                delete( event.getExpiration() );
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
        // TODO Auto-generated method stub

    }

    private void delete( final Expiration expiration )
    {
        // TODO Auto-generated method stub

    }

    private Set<Expiration> loadAll()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
