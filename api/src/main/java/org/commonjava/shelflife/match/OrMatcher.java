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

import org.commonjava.shelflife.model.Expiration;

public class OrMatcher
    implements ExpirationMatcher
{

    private final ExpirationMatcher[] matchers;

    public OrMatcher( final ExpirationMatcher... matchers )
    {
        this.matchers = matchers;
    }

    @Override
    public boolean matches( final Expiration expiration )
    {
        for ( final ExpirationMatcher matcher : matchers )
        {
            if ( matcher.matches( expiration ) )
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public String formatQuery()
    {
        final StringBuilder sb = new StringBuilder();

        for ( final ExpirationMatcher matcher : matchers )
        {
            if ( sb.length() > 0 )
            {
                sb.append( " || " );
            }

            sb.append( matcher.formatQuery() );
        }

        return sb.toString();
    }

}
