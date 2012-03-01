package org.commonjava.shelflife.store.flat;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.apache.log4j.Level;
import org.commonjava.shelflife.ExpirationManagerTCK;
import org.commonjava.shelflife.expire.ExpirationManager;
import org.commonjava.shelflife.fixture.TestExpirationListener;
import org.commonjava.shelflife.model.Expiration;
import org.commonjava.util.logging.Log4jUtil;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.Before;

public class FlatStoreListenerTest
    extends ExpirationManagerTCK
{

    private TestExpirationListener listener;

    private ExpirationManager manager;

    private FlatStoreListener storeListener;

    @Before
    public void before()
        throws Exception
    {
        Log4jUtil.configure( Level.DEBUG );

        final WeldContainer weld = new Weld().initialize();

        manager = weld.instance()
                      .select( ExpirationManager.class )
                      .get();

        listener = weld.instance()
                       .select( TestExpirationListener.class )
                       .get();

        storeListener = weld.instance()
                            .select( FlatStoreListener.class )
                            .get();

    }

    @Override
    protected ExpirationManager getManager()
    {
        return manager;
    }

    @Override
    protected TestExpirationListener getListener()
    {
        return listener;
    }

    @Override
    protected void assertExpirationTriggered( final Expiration ex )
        throws Exception
    {
        storeListener.waitForEvents( 1000, 250 );

        System.out.println( "\n\n\nChecking for absence of flat file for: " + ex + "\n\n\n" );
        final File file = storeListener.getFile( ex );
        assertThat( file.exists(), equalTo( false ) );
    }

    @Override
    protected void assertExpirationCanceled( final Expiration ex )
        throws Exception
    {
        storeListener.waitForEvents( 1000, 250 );

        System.out.println( "\n\n\nChecking for absence of flat file for: " + ex + "\n\n\n" );
        final File file = storeListener.getFile( ex );
        assertThat( file.exists(), equalTo( false ) );
    }

    @Override
    protected void assertExpirationScheduled( final Expiration ex )
        throws Exception
    {
        storeListener.waitForEvents( 1000, 250 );

        System.out.println( "\n\n\nChecking for existence of flat file for: " + ex + "\n\n\n" );
        final File file = storeListener.getFile( ex );
        assertThat( ex + " flat-file storage does not exist!", file.exists(), equalTo( true ) );
    }

    @Override
    protected long getEventTimeout()
    {
        return 1000;
    }

}
