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
