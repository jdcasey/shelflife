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
