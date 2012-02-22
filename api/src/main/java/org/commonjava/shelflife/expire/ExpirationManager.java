package org.commonjava.shelflife.expire;

import java.util.Collection;

import org.commonjava.shelflife.expire.match.ExpirationMatcher;
import org.commonjava.shelflife.model.Expiration;

public interface ExpirationManager
{

    void schedule( Expiration expiration )
        throws ExpirationManagerException;

    void cancel( Expiration expiration )
        throws ExpirationManagerException;

    public void trigger( final Expiration expiration )
        throws ExpirationManagerException;

    void triggerAll()
        throws ExpirationManagerException;

    void triggerAll( ExpirationMatcher matcher )
        throws ExpirationManagerException;

    void cancelAll()
        throws ExpirationManagerException;

    void cancelAll( ExpirationMatcher matcher )
        throws ExpirationManagerException;

    void _loadFromStorage( Collection<Expiration> expirations )
        throws ExpirationManagerException;

}
