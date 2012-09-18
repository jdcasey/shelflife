package org.commonjava.shelflife.store.infinispan;

import java.util.Calendar;
import java.util.Date;

public final class BlockKeyUtils
{

    private BlockKeyUtils()
    {
    }

    public static String generateCurrentBlockKey()
    {
        return generateBlockKey( System.currentTimeMillis(), false );
    }

    public static String generateNextBlockKey( final long d )
    {
        return generateBlockKey( d, true );
    }

    private static String generateBlockKey( final long d, final boolean useNext )
    {
        final Calendar cal = Calendar.getInstance();
        cal.setTime( new Date( d ) );

        final String key =
            String.format( "%d.%d.%d.%d-%d",
                           cal.get( Calendar.YEAR ),
                           ( cal.get( Calendar.MONTH ) + 1 ),
                           cal.get( Calendar.DATE ),
                           cal.get( Calendar.HOUR_OF_DAY ),
                           ( ( cal.get( Calendar.MINUTE ) / InfinispanExpirationManager.NEXT_EXPIRATION_OFFSET_MINUTES ) + ( useNext ? 1
                                           : 0 ) ) );

        return key;
    }

}
