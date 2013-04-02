package org.commonjava.shelflife.store.memory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.commonjava.shelflife.ExpirationManagerException;
import org.commonjava.shelflife.model.Expiration;
import org.commonjava.shelflife.store.ExpirationBlockStore;
import org.commonjava.util.logging.Logger;

@javax.enterprise.context.ApplicationScoped
public class MemoryBlockStore
    implements ExpirationBlockStore
{

    private final Logger logger = new Logger( getClass() );

    private final Map<String, Set<Expiration>> blocks = new HashMap<String, Set<Expiration>>();

    @Override
    public void removeBlocks( final Set<String> currentKeys )
        throws ExpirationManagerException
    {
        for ( final String key : currentKeys )
        {
            logger.debug( "Removing block: %s", key );
            blocks.remove( key );
        }
    }

    @Override
    public void removeBlocks( final String... currentKeys )
        throws ExpirationManagerException
    {
        for ( final String key : currentKeys )
        {
            logger.debug( "Removing block: %s", key );
            blocks.remove( key );
        }
    }

    @Override
    public void removeFromBlock( final String key, final Expiration expiration )
        throws ExpirationManagerException
    {
        logger.debug( "Retrieving block: %s", key );
        final Set<Expiration> block = blocks.get( key );
        if ( block != null )
        {
            logger.debug( "Removing from block: %s", expiration );
            block.remove( expiration );
        }
    }

    @Override
    public void writeBlocks( final Map<String, Set<Expiration>> currentBlocks )
        throws ExpirationManagerException
    {
        logger.debug( "Writing blocks: %s", currentBlocks );
        blocks.putAll( currentBlocks );
    }

    @Override
    public void addToBlock( final String key, final Expiration expiration )
        throws ExpirationManagerException
    {
        logger.debug( "Retrieving block: %s", key );
        Set<Expiration> block = blocks.get( key );
        if ( block == null )
        {
            block = new TreeSet<Expiration>();
            blocks.put( key, block );
        }

        logger.debug( "Adding to block: %s", expiration );
        block.add( expiration );
    }

    @Override
    public Set<Expiration> getBlock( final String key )
        throws ExpirationManagerException
    {
        final Set<Expiration> block = blocks.get( key );
        logger.debug( "Retrieving block: %s\n\n%s\n\n", key, block );
        return block;
    }

}
