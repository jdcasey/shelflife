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
package org.commonjava.shelflife.util;

import java.util.Calendar;
import java.util.Date;

import org.commonjava.shelflife.ExpirationManager;

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
        return generateBlockKey( d, false );
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
            String.format( "%d.%02d.%02d.%02d-%02d", cal.get( Calendar.YEAR ), ( cal.get( Calendar.MONTH ) + 1 ), cal.get( Calendar.DATE ),
                           cal.get( Calendar.HOUR_OF_DAY ),
                           ( ( cal.get( Calendar.MINUTE ) / ExpirationManager.NEXT_EXPIRATION_OFFSET_MINUTES ) + ( useNext ? 1 : 0 ) ) );

        return key;
    }

}
