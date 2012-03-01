package org.commonjava.shelflife.file;

import java.io.File;
import java.io.IOException;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.io.FileUtils;
import org.commonjava.shelflife.expire.ExpirationEvent;
import org.commonjava.shelflife.expire.ExpirationEventType;
import org.commonjava.shelflife.expire.ExpirationManager;
import org.commonjava.shelflife.expire.ExpirationManagerException;
import org.commonjava.shelflife.expire.match.PrefixMatcher;
import org.commonjava.shelflife.file.match.DirMatcher;
import org.commonjava.shelflife.file.match.SubpathMatcher;
import org.commonjava.shelflife.model.Expiration;
import org.commonjava.shelflife.model.ExpirationKey;
import org.commonjava.shelflife.util.ChangeSynchronizer;
import org.commonjava.util.logging.Logger;

@Singleton
public class ShelflifeFiler
{

    public static final String FILE_TYPE_NAMESPACE = "file";

    public static final PrefixMatcher FILE_TYPE_MATCHER = new PrefixMatcher( FILE_TYPE_NAMESPACE );

    @Inject
    private ExpirationManager manager;

    private final ChangeSynchronizer synchronizer = new ChangeSynchronizer();

    public ShelflifeFiler( final ExpirationManager manager )
    {
        this.manager = manager;
    }

    ShelflifeFiler()
    {
    }

    public void scheduleExpiration( final long expiration, final File... files )
        throws ExpirationManagerException
    {
        for ( final File file : files )
        {
            manager.schedule( new Expiration( new ExpirationKey( FILE_TYPE_NAMESPACE, file.getAbsolutePath() ),
                                              expiration, file.getAbsolutePath() ) );
        }
    }

    public void triggerExpiration( final File file )
        throws ExpirationManagerException
    {
        manager.trigger( new Expiration( new ExpirationKey( FILE_TYPE_NAMESPACE, file.getAbsolutePath() ) ) );
    }

    public void cancelExpiration( final File file )
        throws ExpirationManagerException
    {
        manager.cancel( new Expiration( new ExpirationKey( FILE_TYPE_NAMESPACE, file.getAbsolutePath() ) ) );
    }

    public void triggerExpirationsIn( final File dir, final boolean subtree )
        throws ExpirationManagerException
    {
        manager.triggerAll( subtree ? new SubpathMatcher( dir ) : new DirMatcher( dir ) );
    }

    public void cancelExpirationsIn( final File dir, final boolean subtree )
        throws ExpirationManagerException
    {
        manager.cancelAll( subtree ? new SubpathMatcher( dir ) : new DirMatcher( dir ) );
    }

    public int waitForChanges( final long timeout, final long poll )
    {
        return waitForChanges( 1, timeout, poll );
    }

    public int waitForChanges( final int count, final long timeout, final long poll )
    {
        return synchronizer.waitForChange( count, timeout, poll );
    }

    @Singleton
    public static final class Observer
    {
        private final Logger logger = new Logger( getClass() );

        @Inject
        private ShelflifeFiler filer;

        public Observer( final ShelflifeFiler filer )
        {
            this.filer = filer;
        }

        Observer()
        {
        }

        public void expire( @Observes final ExpirationEvent event )
        {
            final Expiration exp = event.getExpiration();
            if ( FILE_TYPE_MATCHER.matches( exp ) && event.getType() == ExpirationEventType.EXPIRE )
            {
                final String path = (String) exp.getData();
                if ( path != null )
                {
                    final File file = new File( path );
                    if ( file.exists() )
                    {
                        logger.info( "[EXPIRE] Deleting file: %s", file );
                        try
                        {
                            FileUtils.forceDelete( file );
                            filer.synchronizer.addChanged();
                        }
                        catch ( final IOException e )
                        {
                            logger.error( "Failed to delete expired file: %s. Reason: %s", e, path, e.getMessage() );
                        }
                    }
                }
            }
        }
    }

}
