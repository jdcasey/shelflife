/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.commonjava.shelflife;

import org.commonjava.shelflife.clock.ExpirationClockSource;
import org.commonjava.shelflife.clock.ThreadedClockSource;
import org.commonjava.shelflife.event.ThreadedEventManager;
import org.commonjava.shelflife.fixture.TestExpirationListener;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.After;

public class WeldPTExpirationManagerTest
    extends AbstractExpirationManagerTest
{

    private Weld weld;

    private WeldContainer container;

    private ThreadedClockSource clock;

    @Override
    protected void setupComponents()
        throws Exception
    {
        weld = new Weld();
        container = weld.initialize();

        clock = container.instance()
                         .select( ThreadedClockSource.class )
                         .get();
        clock.setPeriod( getEventTimeout() );

        final ThreadedEventManager em = container.instance()
                                                 .select( ThreadedEventManager.class )
                                                 .get();
        em.addListener( listener );

        listener = container.instance()
                            .select( TestExpirationListener.class )
                            .get();

        manager = container.instance()
                           .select( ExpirationManager.class )
                           .get();
    }

    //    @Override
    //    protected long getEventTimeout()
    //    {
    //        return 1500;
    //    }

    @Override
    protected ExpirationClockSource getClock()
    {
        return clock;
    }

    @After
    public void shutdown()
    {
        if ( container != null )
        {
            weld.shutdown();
        }
    }

}
