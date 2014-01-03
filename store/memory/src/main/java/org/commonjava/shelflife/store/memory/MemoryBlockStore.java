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
package org.commonjava.shelflife.store.memory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.commonjava.shelflife.ExpirationManagerException;
import org.commonjava.shelflife.model.Expiration;
import org.commonjava.shelflife.store.ExpirationBlockStore;
import org.commonjava.util.logging.Logger;

@javax.enterprise.context.ApplicationScoped
public class MemoryBlockStore
    implements ExpirationBlockStore
{

    private final Logger logger = new Logger( getClass() );

    private final Map<String, Set<Expiration>> blocks = new ConcurrentHashMap<>();

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
            synchronized ( block )
            {
                logger.debug( "Removing from block: %s", expiration );
                block.remove( expiration );
            }
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

        synchronized ( block )
        {
            logger.debug( "Adding to block: %s", expiration );
            block.add( expiration );
        }
    }

    @Override
    public Set<Expiration> getBlock( final String key )
        throws ExpirationManagerException
    {
        final Set<Expiration> block = blocks.get( key );
        logger.debug( "Retrieving block: %s\n\n%s\n\n", key, block );
        return block == null ? null : new TreeSet<>( block );
    }

    @Override
    public void flushCaches()
        throws ExpirationManagerException
    {
        // NOP, we're all cache here, baby!
    }

    @Override
    public List<String> listKeysInOrder()
    {
        final List<String> keys = new ArrayList<>( blocks.keySet() );
        Collections.sort( keys );
        return keys;
    }

}
