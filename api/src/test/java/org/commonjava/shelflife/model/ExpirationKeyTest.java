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
