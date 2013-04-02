package org.commonjava.shelflife.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Level;
import org.commonjava.util.logging.Log4jUtil;
import org.junit.BeforeClass;
import org.junit.Test;

public class ExpirationTest
{

    @BeforeClass
    public static void setupLogging()
    {
        Log4jUtil.configure( Level.DEBUG, "%5p %m%n" );
    }

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
