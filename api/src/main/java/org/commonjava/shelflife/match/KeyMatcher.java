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
import org.commonjava.shelflife.model.ExpirationKey;

public class KeyMatcher
    implements ExpirationMatcher
{

    private final ExpirationKey key;

    public KeyMatcher( final ExpirationKey key )
    {
        this.key = key;
    }

    @Override
    public boolean matches( final Expiration expiration )
    {
        return key.equals( expiration.getKey() );
    }

    @Override
    public String formatQuery()
    {
        return "KEY match [" + key + "]";
    }

}
