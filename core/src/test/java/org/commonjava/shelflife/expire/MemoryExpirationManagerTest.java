package org.commonjava.shelflife.expire;

import org.apache.log4j.Level;
import org.commonjava.shelflife.ExpirationManagerTCK;
import org.commonjava.shelflife.fixture.TestExpirationListener;
import org.commonjava.util.logging.Log4jUtil;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.Before;

public class MemoryExpirationManagerTest
    extends ExpirationManagerTCK
{

    private TestExpirationListener listener;

    private ExpirationManager manager;

    @Before
    public void before()
    {
        Log4jUtil.configure( Level.DEBUG );

        final WeldContainer weld = new Weld().initialize();
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

}
