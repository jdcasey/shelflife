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
package org.commonjava.shelflife;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.commonjava.shelflife.match.ExpirationMatcher;
import org.commonjava.shelflife.model.Expiration;
import org.commonjava.shelflife.model.ExpirationKey;

public interface ExpirationManager
{

    final int NEXT_EXPIRATION_OFFSET_MINUTES = 5;

    final long NEXT_EXPIRATION_BATCH_OFFSET = TimeUnit.MILLISECONDS.convert( NEXT_EXPIRATION_OFFSET_MINUTES,
                                                                             TimeUnit.MINUTES );

    final long MIN_PURGE_PERIOD = 500;

    boolean contains( Expiration expiration )
        throws ExpirationManagerException;

    void schedule( Expiration expiration )
        throws ExpirationManagerException;

    void cancel( Expiration expiration )
        throws ExpirationManagerException;

    void cancel( ExpirationKey expiration )
        throws ExpirationManagerException;

    public void trigger( final Expiration expiration )
        throws ExpirationManagerException;

    public void trigger( final ExpirationKey expiration )
        throws ExpirationManagerException;

    void triggerAll()
        throws ExpirationManagerException;

    void triggerAll( ExpirationMatcher matcher )
        throws ExpirationManagerException;

    void cancelAll()
        throws ExpirationManagerException;

    void cancelAll( ExpirationMatcher matcher )
        throws ExpirationManagerException;

    void loadedFromStorage( Collection<Expiration> expirations )
        throws ExpirationManagerException;

    void loadNextExpirations()
        throws ExpirationManagerException;

    boolean hasExpiration( ExpirationKey key );

    void clearExpired();

}
