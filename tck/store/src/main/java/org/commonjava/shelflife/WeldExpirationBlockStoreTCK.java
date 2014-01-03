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

import org.commonjava.shelflife.store.ExpirationBlockStore;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.After;
import org.junit.Before;

public abstract class WeldExpirationBlockStoreTCK
    extends ExpirationBlockStoreTCK
{

    private Weld weld;

    private WeldContainer container;

    private ExpirationBlockStore store;

    @Before
    public void setupStore()
    {
        weld = new Weld();
        container = weld.initialize();

        store = container.instance()
                         .select( ExpirationBlockStore.class )
                         .get();
    }

    @Override
    protected ExpirationBlockStore getStore()
    {
        return store;
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
