package org.commonjava.shelflife.util;

import java.util.Calendar;
import java.util.Date;

import org.commonjava.shelflife.expire.ExpirationManager;

public final class BlockKeyUtils
{

    private BlockKeyUtils()
    {
    }

    public static String generateCurrentBlockKey()
    {
        return generateBlockKey( System.currentTimeMillis(), false );
    }

    public static String generateBlockKey( final long d )
    {
        return generateBlockKey( d, true );
    }

    public static Date getNextBlockStart( final long d )
    {
        final Calendar cal = Calendar.getInstance();
        cal.setTime( new Date( d ) );

        final int block = ( cal.get( Calendar.MINUTE ) / ExpirationManager.NEXT_EXPIRATION_OFFSET_MINUTES ) + 1;
        cal.set( Calendar.MINUTE, ExpirationManager.NEXT_EXPIRATION_OFFSET_MINUTES * block );

        return cal.getTime();
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
                           ( ( cal.get( Calendar.MINUTE ) / ExpirationManager.NEXT_EXPIRATION_OFFSET_MINUTES ) + ( useNext ? 1
                                           : 0 ) ) );

        return key;
    }

}
