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

public class DateMatcher
    implements ExpirationMatcher
{

    private final Long before;

    private final Long after;

    private DateMatcher( final Long before, final Long after )
    {
        this.before = before;
        this.after = after;
    }

    @Override
    public boolean matches( final Expiration expiration )
    {
        final long expires = expiration.getExpires();
        if ( before != null && expires >= before )
        {
            return false;
        }

        if ( after != null && expires <= after )
        {
            return false;
        }

        return true;
    }

    @Override
    public String formatQuery()
    {
        final StringBuilder sb = new StringBuilder();
        sb.append( '[' );
        if ( before == null && after == null )
        {
            sb.append( '*' );
        }
        else
        {
            if ( after != null )
            {
                // account for non-inclusive bounds.
                sb.append( ( after + 1 ) );
            }
            else
            {
                sb.append( '*' );
            }

            sb.append( ',' );

            if ( before != null )
            {
                // account for non-inclusive bounds.
                sb.append( ( before - 1 ) );
            }
            else
            {
                sb.append( '*' );
            }
        }

        sb.append( ']' );

        return sb.toString();
    }

    public static final class Builder
    {
        private Long before;

        private Long after;

        public Builder before( final long before )
        {
            this.before = before;
            return this;
        }

        public Builder after( final long after )
        {
            this.after = after;
            return this;
        }

        public DateMatcher build()
        {
            return new DateMatcher( before, after );
        }
    }

}
