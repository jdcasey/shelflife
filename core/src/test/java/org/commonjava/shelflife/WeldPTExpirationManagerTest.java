/*******************************************************************************
 * Copyright (C) 2014 John Casey.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
