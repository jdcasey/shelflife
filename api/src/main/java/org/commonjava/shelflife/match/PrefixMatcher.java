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
