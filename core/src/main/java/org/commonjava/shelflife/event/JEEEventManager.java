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
package org.commonjava.shelflife.event;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.commonjava.shelflife.event.ExpirationEvent;
import org.commonjava.shelflife.event.ExpirationEventManager;
import org.commonjava.shelflife.event.ExpirationEventType;
import org.commonjava.shelflife.model.Expiration;

@ApplicationScoped
@Alternative
public class JEEEventManager
    implements ExpirationEventManager
{

    @Inject
    private Event<ExpirationEvent> trigger;

    @Override
    public ExpirationEvent fire( final Expiration expiration, final ExpirationEventType type )
    {
        ExpirationEvent event = null;

        if ( trigger != null )
        {
            event = new ExpirationEvent( expiration, type );
            trigger.fire( event );
        }

        return event;
    }

}
