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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@javax.enterprise.context.ApplicationScoped
public class MemoryBlockStore
    implements ExpirationBlockStore
{

    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final Map<String, Set<Expiration>> blocks = new ConcurrentHashMap<>();

    @Override
    public void removeBlocks( final Set<String> currentKeys )
        throws ExpirationManagerException
    {
        for ( final String key : currentKeys )
        {
            logger.debug( "Removing block: {}", key );
            blocks.remove( key );
        }
    }

    @Override
    public void removeBlocks( final String... currentKeys )
        throws ExpirationManagerException
    {
        for ( final String key : currentKeys )
        {
            logger.debug( "Removing block: {}", key );
            blocks.remove( key );
        }
    }

    @Override
    public void removeFromBlock( final String key, final Expiration expiration )
        throws ExpirationManagerException
    {
        logger.debug( "Retrieving block: {}", key );
        final Set<Expiration> block = blocks.get( key );
        if ( block != null )
        {
            synchronized ( block )
            {
                logger.debug( "Removing from block: {}", expiration );
                block.remove( expiration );
            }
        }
    }

    @Override
    public void writeBlocks( final Map<String, Set<Expiration>> currentBlocks )
        throws ExpirationManagerException
    {
        logger.debug( "Writing blocks: {}", currentBlocks );
        blocks.putAll( currentBlocks );
    }

    @Override
    public void addToBlock( final String key, final Expiration expiration )
        throws ExpirationManagerException
    {
        logger.debug( "Retrieving block: {}", key );
        Set<Expiration> block = blocks.get( key );
        if ( block == null )
        {
            block = new TreeSet<Expiration>();
            blocks.put( key, block );
        }

        synchronized ( block )
        {
            logger.debug( "Adding to block: {}", expiration );
            block.add( expiration );
        }
    }

    @Override
    public Set<Expiration> getBlock( final String key )
        throws ExpirationManagerException
    {
        final Set<Expiration> block = blocks.get( key );
        logger.debug( "Retrieving block: {}\n\n{}\n\n", key, block );
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
