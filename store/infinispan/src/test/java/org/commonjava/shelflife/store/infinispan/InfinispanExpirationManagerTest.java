package org.commonjava.shelflife.store.infinispan;


public class InfinispanExpirationManagerTest
//    extends ExpirationManagerTCK
{

    //    private TestExpirationListener listener;
    //
    //    private ExpirationManager manager;
    //
    //    private ChangeListener changeListener;
    //
    //    private TestConfigProvider configProvider;
    //
    //    private Weld weld;
    //
    //    @Before
    //    public void before()
    //        throws Exception
    //    {
    //        Log4jUtil.configure( Level.DEBUG );
    //
    //        weld = new Weld();
    //        final WeldContainer wc = weld.initialize();
    //
    //        configProvider = wc.instance()
    //                           .select( TestConfigProvider.class )
    //                           .get();
    //
    //        System.out.println( "\n\n\n\nGot test config provider.\n\n\n\n" );
    //
    //        manager = wc.instance()
    //                    .select( ExpirationManager.class )
    //                    .get();
    //
    //        listener = wc.instance()
    //                     .select( TestExpirationListener.class )
    //                     .get();
    //
    //        changeListener = wc.instance()
    //                           .select( ChangeListener.class )
    //                           .get();
    //
    //    }
    //
    //    @After
    //    public void after()
    //    {
    //        if ( configProvider != null )
    //        {
    //            configProvider.stopCacheManager();
    //        }
    //
    //        if ( weld != null )
    //        {
    //            weld.shutdown();
    //        }
    //    }
    //
    //    @Override
    //    protected ExpirationManager getManager()
    //    {
    //        return manager;
    //    }
    //
    //    @Override
    //    protected TestExpirationListener getListener()
    //    {
    //        return listener;
    //    }
    //
    //    @Override
    //    protected void assertExpirationTriggered( final Expiration ex )
    //        throws Exception
    //    {
    //        System.out.println( "waiting for trigger event..." );
    //        if ( !changeListener.isTriggered( ex ) )
    //        {
    //            changeListener.waitForEvents( 500, 250 );
    //        }
    //
    //        System.out.println( "Checking whether " + ex + " was triggered..." );
    //        assertThat( changeListener.isTriggered( ex ), equalTo( true ) );
    //        assertThat( manager.contains( ex ), equalTo( false ) );
    //    }
    //
    //    @Override
    //    protected void assertExpirationCanceled( final Expiration ex )
    //        throws Exception
    //    {
    //        System.out.println( "waiting for cancel event..." );
    //        if ( !changeListener.isCanceled( ex ) )
    //        {
    //            changeListener.waitForEvents( getEventTimeout(), 250 );
    //        }
    //
    //        System.out.println( "Checking whether " + ex + " was canceled..." );
    //        assertThat( changeListener.isCanceled( ex ), equalTo( true ) );
    //        assertThat( manager.contains( ex ), equalTo( false ) );
    //    }
    //
    //    @Override
    //    protected void assertExpirationScheduled( final Expiration ex )
    //        throws Exception
    //    {
    //        System.out.println( "waiting for schedule event..." );
    //        if ( !changeListener.isScheduled( ex ) )
    //        {
    //            changeListener.waitForEvents( getEventTimeout(), 250 );
    //        }
    //
    //        System.out.println( "Checking whether " + ex + " was scheduled..." );
    //        assertThat( changeListener.isScheduled( ex ), equalTo( true ) );
    //    }
    //
    //    @Override
    //    protected long getEventTimeout()
    //    {
    //        return super.getEventTimeout() + 3000; // for infinispan startup?
    //    }
    //
}
