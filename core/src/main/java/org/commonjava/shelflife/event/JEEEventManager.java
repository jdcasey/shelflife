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
