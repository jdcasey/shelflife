package org.commonjava.shelflife;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.commonjava.shelflife.expire.ExpirationEvent;
import org.commonjava.shelflife.expire.ExpirationEventType;
import org.commonjava.shelflife.expire.ExpirationManager;
import org.commonjava.shelflife.fixture.TestExpirationListener;
import org.commonjava.shelflife.model.Expiration;
import org.commonjava.shelflife.model.ExpirationKey;
import org.junit.Test;

public abstract class ExpirationManagerTCK
{

    protected abstract ExpirationManager getManager();

    protected abstract TestExpirationListener getListener();

    protected long getEventTimeout()
    {
        return 600;
    }

    protected void assertExpirationTriggered( final Expiration ex )
        throws Exception
    {
    }

    protected void assertExpirationCanceled( final Expiration ex )
        throws Exception
    {
    }

    protected void assertExpirationScheduled( final Expiration ex )
        throws Exception
    {
    }

    @Test
    public void scheduleOneAndWaitForExpiration()
        throws Exception
    {
        final Expiration ex = new Expiration( new ExpirationKey( "test", "one" ), 500 );
        getManager().schedule( ex );
        assertExpirationScheduled( ex );

        Thread.sleep( 600 );

        assertThat( ex.isActive(), equalTo( false ) );
        assertExpirationTriggered( ex );
    }

    @Test
    public void scheduleOneAndWaitForExpirationEvent()
        throws Exception
    {
        final Expiration ex = new Expiration( new ExpirationKey( "test", "one" ), 500 );
        getManager().schedule( ex );
        assertExpirationScheduled( ex );

        final List<ExpirationEvent> events = getListener().waitForEvents( 2, getEventTimeout() );

        assertThat( ex.isActive(), equalTo( false ) );
        assertThat( events, notNullValue() );
        assertThat( events.size(), equalTo( 2 ) );

        int idx = 0;

        ExpirationEvent event = events.get( idx );
        assertThat( event.getType(), equalTo( ExpirationEventType.SCHEDULE ) );
        assertThat( event.getExpiration(), equalTo( ex ) );
        assertThat( event.getExpiration()
                         .getKey(), equalTo( ex.getKey() ) );

        idx++;

        event = events.get( idx );
        assertThat( event.getType(), equalTo( ExpirationEventType.EXPIRE ) );
        assertThat( event.getExpiration(), equalTo( ex ) );
        assertThat( event.getExpiration()
                         .getKey(), equalTo( ex.getKey() ) );

        assertExpirationTriggered( ex );
    }

    @Test
    public void scheduleOneAndCancelBeforeExpiration()
        throws Exception
    {
        final Expiration ex = new Expiration( new ExpirationKey( "test", "one" ), 500 );
        getManager().schedule( ex );
        assertExpirationScheduled( ex );
        final long start = System.currentTimeMillis();

        getManager().cancel( ex );

        final long stop = System.currentTimeMillis();

        assertThat( ex.isActive(), equalTo( false ) );
        assertThat( stop - start < 500, equalTo( true ) );

        assertExpirationCanceled( ex );
    }

    @Test
    public void scheduleOneCancelAndVerifyNoExpirationEvent()
        throws Exception
    {
        final Expiration ex = new Expiration( new ExpirationKey( "test", "one" ), 500 );
        getManager().schedule( ex );
        assertExpirationScheduled( ex );
        final long start = System.currentTimeMillis();

        getManager().cancel( ex );

        final long stop = System.currentTimeMillis();

        final List<ExpirationEvent> events = getListener().waitForEvents( 2, getEventTimeout() );

        assertThat( ex.isActive(), equalTo( false ) );
        assertThat( stop - start < 500, equalTo( true ) );

        int idx = 0;

        ExpirationEvent event = events.get( idx );
        assertThat( event.getType(), equalTo( ExpirationEventType.SCHEDULE ) );
        assertThat( event.getExpiration(), equalTo( ex ) );
        assertThat( event.getExpiration()
                         .getKey(), equalTo( ex.getKey() ) );

        idx++;

        event = events.get( idx );
        assertThat( event.getType(), equalTo( ExpirationEventType.CANCEL ) );
        assertThat( event.getExpiration(), equalTo( ex ) );
        assertThat( event.getExpiration()
                         .getKey(), equalTo( ex.getKey() ) );

        assertExpirationCanceled( ex );
    }

}
