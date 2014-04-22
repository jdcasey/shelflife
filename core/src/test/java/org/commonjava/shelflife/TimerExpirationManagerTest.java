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

import java.util.concurrent.Executors;

import org.commonjava.shelflife.DefaultExpirationManager;
import org.commonjava.shelflife.clock.ExpirationClockSource;
import org.commonjava.shelflife.clock.TimerClockSource;
import org.commonjava.shelflife.event.ThreadedEventManager;
import org.commonjava.shelflife.store.memory.MemoryBlockStore;

public class TimerExpirationManagerTest
    extends AbstractExpirationManagerTest
{

    protected ExpirationClockSource clock;

    @Override
    protected void setupComponents()
        throws Exception
    {
        final MemoryBlockStore store = new MemoryBlockStore();
        clock = new TimerClockSource( getEventTimeout() );

        final ThreadedEventManager events = new ThreadedEventManager( Executors.newFixedThreadPool( 2 ) );
        events.addListener( listener );

        manager = new DefaultExpirationManager( store, clock, events );
    }

    @Override
    protected ExpirationClockSource getClock()
    {
        return clock;
    }

}
