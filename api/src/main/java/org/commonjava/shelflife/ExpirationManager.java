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
