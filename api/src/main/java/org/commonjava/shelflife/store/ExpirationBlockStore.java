package org.commonjava.shelflife.store;

import java.util.Map;
import java.util.Set;

import org.commonjava.shelflife.ExpirationManagerException;
import org.commonjava.shelflife.model.Expiration;

public interface ExpirationBlockStore
{

    void writeBlocks( Map<String, Set<Expiration>> currentBlocks )
        throws ExpirationManagerException;

    void addToBlock( String key, Expiration expiration )
        throws ExpirationManagerException;

    Set<Expiration> getBlock( String key )
        throws ExpirationManagerException;

    void removeFromBlock( String key, Expiration expiration )
        throws ExpirationManagerException;

    void removeBlocks( Set<String> currentKeys )
        throws ExpirationManagerException;

    void removeBlocks( String... currentKeys )
        throws ExpirationManagerException;

    void flushCaches()
        throws ExpirationManagerException;
}
