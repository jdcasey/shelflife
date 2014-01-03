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
package org.commonjava.shelflife.match;

import static org.apache.commons.lang.StringUtils.join;

import org.commonjava.shelflife.model.Expiration;

public class PrefixMatcher
    implements ExpirationMatcher
{

    private final String[] prefix;

    public PrefixMatcher( final String... prefix )
    {
        this.prefix = prefix;
    }

    @Override
    public boolean matches( final Expiration expiration )
    {
        final String[] parts = expiration.getKey()
                                         .getParts();
        if ( parts.length < prefix.length )
        {
            return false;
        }

        for ( int i = 0; i < prefix.length; i++ )
        {
            if ( !prefix[i].equals( parts[i] ) )
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public String formatQuery()
    {
        return join( prefix, ":" ) + ":*";
    }

}
