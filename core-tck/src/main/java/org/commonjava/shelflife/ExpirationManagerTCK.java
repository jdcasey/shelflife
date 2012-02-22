package org.commonjava.shelflife;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.commonjava.shelflife.expire.ExpirationEvent;
import org.commonjava.shelflife.expire.ExpirationManager;
import org.commonjava.shelflife.expire.ExpirationManagerException;
import org.commonjava.shelflife.fixture.TestExpiration;
import org.commonjava.shelflife.fixture.TestExpirationListener;
import org.commonjava.shelflife.model.Expiration;
import org.junit.Test;

public abstract class ExpirationManagerTCK
{

    protected abstract ExpirationManager getManager();

    protected abstract TestExpirationListener getListener();

    @Test
    public void scheduleOneAndWaitForExpiration()
        throws ExpirationManagerException, InterruptedException
    {
        final TestExpiration ex = new TestExpiration( "one", 500 );
        getManager().schedule( ex );

        Thread.sleep( 600 );

        assertThat( ex.isActive(), equalTo( false ) );
    }

    @Test
    public void scheduleOneAndWaitForExpirationEvent()
        throws ExpirationManagerException, InterruptedException
    {
        final Expiration ex = new TestExpiration( "one", 500 );
        getManager().schedule( ex );

        final List<ExpirationEvent> events = getListener().waitForEvents( 600 );

        assertThat( ex.isActive(), equalTo( false ) );
        assertThat( events, notNullValue() );
        assertThat( events.size(), equalTo( 1 ) );
        assertThat( events.get( 0 )
                          .getExpiration(), equalTo( ex ) );
        assertThat( events.get( 0 )
                          .getExpiration()
                          .getKey(), equalTo( ex.getKey() ) );
    }

    @Test
    public void scheduleOneAndCancelBeforeExpiration()
        throws ExpirationManagerException, InterruptedException
    {
        final TestExpiration ex = new TestExpiration( "one", 500 );
        getManager().schedule( ex );
        final long start = System.currentTimeMillis();

        getManager().cancel( ex );

        final long stop = System.currentTimeMillis();

        assertThat( ex.isActive(), equalTo( false ) );
        assertThat( stop - start < 500, equalTo( true ) );
    }

    @Test
    public void scheduleOneCancelAndVerifyNoExpirationEvent()
        throws ExpirationManagerException, InterruptedException
    {
        final TestExpiration ex = new TestExpiration( "one", 500 );
        getManager().schedule( ex );
        final long start = System.currentTimeMillis();

        getManager().cancel( ex );

        final long stop = System.currentTimeMillis();

        final List<ExpirationEvent> events = getListener().waitForEvents( 600 );

        assertThat( ex.isActive(), equalTo( false ) );
        assertThat( stop - start < 500, equalTo( true ) );
        assertThat( events, nullValue() );
    }

}
