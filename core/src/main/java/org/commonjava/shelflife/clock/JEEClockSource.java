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
