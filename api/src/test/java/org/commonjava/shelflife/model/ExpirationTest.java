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
