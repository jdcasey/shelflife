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
