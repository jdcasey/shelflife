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
