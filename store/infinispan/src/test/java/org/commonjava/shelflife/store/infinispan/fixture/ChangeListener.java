package org.commonjava.shelflife.store.infinispan.fixture;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.inject.Singleton;

import org.commonjava.shelflife.expire.ExpirationEvent;
import org.commonjava.shelflife.expire.ExpirationEventType;
import org.commonjava.shelflife.model.Expiration;
import org.commonjava.shelflife.util.ChangeSynchronizer;
import org.commonjava.util.logging.Logger;

@Singleton
public class ChangeListener
{
    private final Logger logger = new Logger( getClass() );

    private final ChangeSynchronizer changeSync = new ChangeSynchronizer();

    private final Set<Expiration> scheduled = new HashSet<Expiration>();

    private final Set<Expiration> canceled = new HashSet<Expiration>();

    private final Set<Expiration> triggered = new HashSet<Expiration>();

    public void handleExpirationEvent( @Observes final ExpirationEvent event )
    {
        final ExpirationEventType type = event.getType();

        logger.info( "[%s] %s", type, event.getExpiration()
                                           .getKey() );

        switch ( type )
        {
            case SCHEDULE:
            {
                scheduled.add( event.getExpiration() );
                changeSync.addChanged();
                break;
            }
            case CANCEL:
            {
                canceled.add( event.getExpiration() );
                changeSync.addChanged();
                break;
            }
            case EXPIRE:
            {
                triggered.add( event.getExpiration() );
                changeSync.addChanged();
                break;
            }

            default:
            {
                logger.warn( "Unknown event type: %s", type );
            }
        }
    }

    public boolean isScheduled( final Expiration ex )
    {
        return scheduled.contains( ex );
    }

    public boolean isTriggered( final Expiration ex )
    {
        return triggered.contains( ex );
    }

    public boolean isCanceled( final Expiration ex )
    {
        return canceled.contains( ex );
    }

    public int waitForEvents( final int count, final long timeout, final long poll )
    {
        return changeSync.waitForChange( count, timeout, poll );
    }

    public int waitForEvents( final long timeout, final long poll )
    {
        return changeSync.waitForChange( 1, timeout, poll );
    }

}
