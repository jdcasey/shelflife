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
