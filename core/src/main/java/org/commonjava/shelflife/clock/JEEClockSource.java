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
package org.commonjava.shelflife.clock;

import javax.annotation.ManagedBean;
import javax.ejb.Schedule;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

import org.commonjava.shelflife.ExpirationManager;
import org.commonjava.shelflife.ExpirationManagerException;

@ApplicationScoped
@ManagedBean
@Alternative
public class JEEClockSource
    implements ExpirationClockSource
{

    private ExpirationManager manager;

    @Schedule( minute = "/5", hour = "*" )
    public void tick()
    {
        if ( manager != null )
        {
            manager.clearExpired();
        }
    }

    @Override
    public void start( final ExpirationManager manager )
        throws ExpirationManagerException
    {
        this.manager = manager;
    }

    @Override
    public void stop()
    {
        manager = null;
    }

}
