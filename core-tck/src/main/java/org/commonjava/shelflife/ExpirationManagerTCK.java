package org.commonjava.shelflife;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        return 1000;
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

        Thread.sleep( getEventTimeout() );

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
    public void scheduleOneAndTriggerBeforeExpiration()
        throws Exception
    {
        final Expiration ex = new Expiration( new ExpirationKey( "test", "one" ), 50000 );
        getManager().schedule( ex );
        assertExpirationScheduled( ex );

        getManager().trigger( ex );

        assertThat( ex.isActive(), equalTo( false ) );
        assertExpirationTriggered( ex );
    }

    @Test
    public void scheduleOneAndTriggerBeforeExpiration_CheckEvent()
        throws Exception
    {
        final Expiration ex = new Expiration( new ExpirationKey( "test", "one" ), 50000 );
        getManager().schedule( ex );
        assertExpirationScheduled( ex );

        getManager().trigger( ex );

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
    public void scheduleThreeAndTriggerAllBeforeExpiration()
        throws Exception
    {
        final Expiration[] exs =
            { new Expiration( new ExpirationKey( "test", "one" ), 50000 ),
                new Expiration( new ExpirationKey( "test", "two" ), 50000 ),
                new Expiration( new ExpirationKey( "test", "three" ), 50000 ) };

        for ( final Expiration ex : exs )
        {
            getManager().schedule( ex );
        }

        for ( final Expiration ex : exs )
        {
            assertExpirationScheduled( ex );
        }

        Thread.sleep( getEventTimeout() );

        getManager().triggerAll();

        Thread.sleep( getEventTimeout() );

        for ( final Expiration ex : exs )
        {
            assertThat( ex.isActive(), equalTo( false ) );
            assertExpirationTriggered( ex );
        }
    }

    @Test
    public void scheduleThreeAndTriggerAllBeforeExpiration_CheckEvent()
        throws Exception
    {
        final Expiration[] exs =
            { new Expiration( new ExpirationKey( "test", "one" ), 50000 ),
                new Expiration( new ExpirationKey( "test", "two" ), 50000 ),
                new Expiration( new ExpirationKey( "test", "three" ), 50000 ), };

        for ( final Expiration ex : exs )
        {
            getManager().schedule( ex );
        }

        final Set<Expiration> scheduled = new HashSet<Expiration>();
        List<ExpirationEvent> events = getListener().waitForEvents( exs.length, getEventTimeout() );

        assertThat( events, notNullValue() );
        assertThat( events.size(), equalTo( exs.length ) );

        for ( final ExpirationEvent evt : events )
        {
            final Expiration e = evt.getExpiration();
            final ExpirationEventType t = evt.getType();
            assertThat( t, equalTo( ExpirationEventType.SCHEDULE ) );
            scheduled.add( e );
        }

        for ( final Expiration ex : exs )
        {
            assertThat( scheduled.contains( ex ), equalTo( true ) );
            assertExpirationScheduled( ex );
        }

        getManager().triggerAll();

        events = getListener().waitForEvents( exs.length, getEventTimeout() );

        assertThat( events, notNullValue() );
        assertThat( events.size(), equalTo( exs.length ) );

        final Set<Expiration> triggered = new HashSet<Expiration>();

        for ( final ExpirationEvent evt : events )
        {
            final Expiration e = evt.getExpiration();
            final ExpirationEventType t = evt.getType();
            assertThat( t, equalTo( ExpirationEventType.EXPIRE ) );
            triggered.add( e );
        }

        for ( final Expiration ex : exs )
        {
            assertThat( ex.isActive(), equalTo( false ) );
            assertThat( ex.isCanceled(), equalTo( false ) );
            assertThat( triggered.contains( ex ), equalTo( true ) );

            assertExpirationTriggered( ex );
        }
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
        final Expiration ex = new Expiration( new ExpirationKey( "test", "one" ), 50000 );
        getManager().schedule( ex );
        assertExpirationScheduled( ex );

        getManager().cancel( ex );

        final List<ExpirationEvent> events = getListener().waitForEvents( 2, getEventTimeout() );

        assertThat( ex.isActive(), equalTo( false ) );

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

    @Test
    public void scheduleThreeAndCancelAllBeforeExpiration()
        throws Exception
    {
        final Expiration[] exs =
            { new Expiration( new ExpirationKey( "test", "one" ), 5000 ),
                new Expiration( new ExpirationKey( "test", "two" ), 5000 ),
                new Expiration( new ExpirationKey( "test", "three" ), 5000 ), };

        for ( final Expiration ex : exs )
        {
            getManager().schedule( ex );
        }

        for ( final Expiration ex : exs )
        {
            assertExpirationScheduled( ex );
        }

        Thread.sleep( getEventTimeout() );

        getManager().cancelAll();

        Thread.sleep( getEventTimeout() );

        for ( final Expiration ex : exs )
        {
            assertThat( ex + " still active!", ex.isActive(), equalTo( false ) );
            assertThat( ex + " not canceled!", ex.isCanceled(), equalTo( true ) );

            assertExpirationCanceled( ex );
        }
    }

    @Test
    public void scheduleThreeCancelAllAndVerifyNoExpirationEvent()
        throws Exception
    {
        final Expiration[] exs =
            { new Expiration( new ExpirationKey( "test", "one" ), 50000 ),
                new Expiration( new ExpirationKey( "test", "two" ), 50000 ),
                new Expiration( new ExpirationKey( "test", "three" ), 50000 ), };

        for ( final Expiration ex : exs )
        {
            getManager().schedule( ex );
        }

        final Set<Expiration> scheduled = new HashSet<Expiration>();
        List<ExpirationEvent> events = getListener().waitForEvents( exs.length, getEventTimeout() );

        assertThat( events, notNullValue() );
        assertThat( events.size(), equalTo( exs.length ) );

        for ( final ExpirationEvent evt : events )
        {
            final Expiration e = evt.getExpiration();
            final ExpirationEventType t = evt.getType();
            assertThat( t, equalTo( ExpirationEventType.SCHEDULE ) );
            scheduled.add( e );
        }

        for ( final Expiration ex : exs )
        {
            assertThat( scheduled.contains( ex ), equalTo( true ) );
            assertExpirationScheduled( ex );
        }

        getManager().cancelAll();

        events = getListener().waitForEvents( exs.length, getEventTimeout() );

        assertThat( events, notNullValue() );
        assertThat( events.size(), equalTo( exs.length ) );

        final Set<Expiration> canceled = new HashSet<Expiration>();

        for ( final ExpirationEvent evt : events )
        {
            final Expiration e = evt.getExpiration();
            final ExpirationEventType t = evt.getType();
            assertThat( t, equalTo( ExpirationEventType.CANCEL ) );
            canceled.add( e );
        }

        for ( final Expiration ex : exs )
        {
            assertThat( ex.isActive(), equalTo( false ) );
            assertThat( ex.isCanceled(), equalTo( true ) );
            assertThat( canceled.contains( ex ), equalTo( true ) );

            assertExpirationCanceled( ex );
        }
    }

}
