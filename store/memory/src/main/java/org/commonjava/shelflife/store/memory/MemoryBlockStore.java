package org.commonjava.shelflife.store.memory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.commonjava.shelflife.expire.ExpirationManagerException;
import org.commonjava.shelflife.model.Expiration;
import org.commonjava.shelflife.store.ExpirationBlockStore;

@javax.enterprise.context.ApplicationScoped
public class MemoryBlockStore
    implements ExpirationBlockStore
{

    private final Map<String, Set<Expiration>> blocks = new HashMap<String, Set<Expiration>>();

    @Override
    public void removeBlocks( final Set<String> currentKeys )
        throws ExpirationManagerException
    {
        for ( final String key : currentKeys )
        {
            blocks.remove( key );
        }
    }

    @Override
    public void removeBlocks( final String... currentKeys )
        throws ExpirationManagerException
    {
        for ( final String key : currentKeys )
        {
            blocks.remove( key );
        }
    }

    @Override
    public void removeFromBlock( final String key, final Expiration expiration )
        throws ExpirationManagerException
    {
        final Set<Expiration> block = blocks.get( key );
        if ( block != null )
        {
            block.remove( expiration );
        }
    }

    @Override
    public void writeBlocks( final Map<String, Set<Expiration>> currentBlocks )
        throws ExpirationManagerException
    {
        blocks.putAll( currentBlocks );
    }

    @Override
    public void addToBlock( final String key, final Expiration expiration )
        throws ExpirationManagerException
    {
        final Set<Expiration> block = blocks.get( key );
        if ( block != null )
        {
            block.add( expiration );
        }
    }

    @Override
    public Set<Expiration> getBlock( final String key )
        throws ExpirationManagerException
    {
        return blocks.get( key );
    }

}
