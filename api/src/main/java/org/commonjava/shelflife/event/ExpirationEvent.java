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

import org.commonjava.shelflife.model.Expiration;

public class ExpirationEvent
{

    private final Expiration expiration;

    private final ExpirationEventType type;

    public ExpirationEvent( final Expiration exp, final ExpirationEventType type )
    {
        this.expiration = exp;
        this.type = type;
    }

    public ExpirationEventType getType()
    {
        return type;
    }

    public Expiration getExpiration()
    {
        return expiration;
    }

    @Override
    public String toString()
    {
        return String.format( "ExpirationEvent [exp: %s, type: %s]", expiration, type );
    }

}
