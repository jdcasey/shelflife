package org.commonjava.shelflife.store.couch;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.lang.annotation.Annotation;

import javax.enterprise.util.AnnotationLiteral;

import org.apache.log4j.Level;
import org.commonjava.couch.db.CouchManager;
import org.commonjava.shelflife.ExpirationManagerTCK;
import org.commonjava.shelflife.expire.ExpirationManager;
import org.commonjava.shelflife.fixture.TestExpirationListener;
import org.commonjava.shelflife.inject.Shelflife;
import org.commonjava.shelflife.model.Expiration;
import org.commonjava.util.logging.Log4jUtil;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.Before;

public class CouchStoreListenerTest
    extends ExpirationManagerTCK
{

    private TestExpirationListener listener;

    private ExpirationManager manager;

    private CouchManager couch;

    private CouchStoreListener couchListener;

    @Before
    public void before()
        throws Exception
    {
        Log4jUtil.configure( Level.DEBUG );

        final WeldContainer weld = new Weld().initialize();

        final Annotation slLiteral = new AnnotationLiteral<Shelflife>()
        {
            private static final long serialVersionUID = 1L;
        };

        couch = weld.instance()
                    .select( CouchManager.class, slLiteral )
                    .get();

        couch.dropDatabase();

        couchListener = weld.instance()
                            .select( CouchStoreListener.class )
                            .get();

        couchListener.initCouch();

        manager = weld.instance()
                      .select( ExpirationManager.class )
                      .get();

        listener = weld.instance()
                       .select( TestExpirationListener.class )
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
        couchListener.waitForEvents( 1000, 250 );

        System.out.println( "\n\n\nChecking for absence of couch document for: " + ex + "\n\n\n" );
        final ExpirationWrapperDoc doc = couch.getDocument( new ExpirationDocRef( ex ), ExpirationWrapperDoc.class );
        assertThat( doc, nullValue() );
    }

    @Override
    protected void assertExpirationCanceled( final Expiration ex )
        throws Exception
    {
        couchListener.waitForEvents( 1000, 250 );

        System.out.println( "\n\n\nChecking for absence of couch document for: " + ex + "\n\n\n" );
        final ExpirationWrapperDoc doc = couch.getDocument( new ExpirationDocRef( ex ), ExpirationWrapperDoc.class );
        assertThat( doc, nullValue() );
    }

    @Override
    protected void assertExpirationScheduled( final Expiration ex )
        throws Exception
    {
        couchListener.waitForEvents( 1000, 250 );

        System.out.println( "\n\n\nChecking for existence of couch document for: " + ex + "\n\n\n" );
        final ExpirationWrapperDoc doc = couch.getDocument( new ExpirationDocRef( ex ), ExpirationWrapperDoc.class );
        assertThat( doc, notNullValue() );
    }

    @Override
    protected long getEventTimeout()
    {
        return 1000;
    }

}
