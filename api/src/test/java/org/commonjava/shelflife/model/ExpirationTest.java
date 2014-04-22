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
package org.commonjava.shelflife.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

public class ExpirationTest
{

    @Test
    public void addTwoWithSameExpirationToTreeSet()
    {
        final long expires = System.currentTimeMillis() + 10000;
        final Expiration e1 = new Expiration( new ExpirationKey( "test", "one" ), expires );
        final Expiration e2 = new Expiration( new ExpirationKey( "test", "two" ), expires );

        final Set<Expiration> block = new TreeSet<Expiration>();
        assertThat( block.add( e1 ), equalTo( true ) );
        assertThat( block.add( e2 ), equalTo( true ) );
    }

    @Test
    public void comparisonPrefersExpirationOverKeyComp()
    {
        // e1 sorts first by key, but last by expires.
        final Expiration e1 = new Expiration( new ExpirationKey( "test", "1" ), 10000 );
        final Expiration e2 = new Expiration( new ExpirationKey( "test", "2" ), 100 );

        // expiration is the same, so the key must be compared.
        assertThat( e1.compareTo( e2 ) > 0, equalTo( true ) );
    }

    @Test
    public void comparisonFallsBackToKeyComp()
    {
        final long expires = System.currentTimeMillis() + 10000;
        final Expiration e1 = new Expiration( new ExpirationKey( "test", "1" ), expires );
        final Expiration e2 = new Expiration( new ExpirationKey( "test", "2" ), expires );

        // expiration is the same, so the key must be compared.
        assertThat( e1.compareTo( e2 ) < 0, equalTo( true ) );
    }

}
