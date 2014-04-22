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

import org.junit.Test;

public class ExpirationKeyTest
{

    @Test
    public void hashcodeAndEquals()
    {
        final ExpirationKey k1 = new ExpirationKey( "foo", "bar", "baz" );
        final ExpirationKey k2 = new ExpirationKey( "foo", "bar", "baz" );

        assertThat( k1.hashCode(), equalTo( k2.hashCode() ) );
        assertThat( k1.equals( k2 ), equalTo( true ) );
    }

}
