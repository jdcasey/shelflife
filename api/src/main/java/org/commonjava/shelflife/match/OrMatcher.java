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
