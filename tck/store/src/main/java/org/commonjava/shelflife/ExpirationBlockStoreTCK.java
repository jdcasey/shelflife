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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.commonjava.shelflife.model.Expiration;
import org.commonjava.shelflife.model.ExpirationKey;
import org.commonjava.shelflife.store.ExpirationBlockStore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ExpirationBlockStoreTCK
{

    protected final Logger logger = LoggerFactory.getLogger( getClass() );

    protected abstract ExpirationBlockStore getStore();

    @Test
    public void writeSingletonBlockMapAndRetrieveBlockByKey()
        throws Exception
    {
        final Map<String, Set<Expiration>> blocks = new HashMap<String, Set<Expiration>>();
        final Set<Expiration> expirations = new TreeSet<Expiration>();

        final Expiration e1 = new Expiration( new ExpirationKey( "test", "one" ), 500 );
        final Expiration e2 = new Expiration( new ExpirationKey( "test", "two" ), 750 );

        assertThat( expirations.add( e1 ), equalTo( true ) );
        assertThat( expirations.add( e2 ), equalTo( true ) );

        final String key = "key";
        blocks.put( key, expirations );

        final ExpirationBlockStore store = getStore();
        store.writeBlocks( blocks );

        store.flushCaches();

        final Set<Expiration> block = store.getBlock( key );

        assertThat( block.size(), equalTo( 2 ) );

        assertThat( block.contains( e1 ), equalTo( true ) );
        assertThat( block.contains( e2 ), equalTo( true ) );
    }

    @Test
    public void retrievedBlockIsSorted()
        throws Exception
    {
        final Map<String, Set<Expiration>> blocks = new HashMap<String, Set<Expiration>>();
        final Set<Expiration> expirations = new TreeSet<Expiration>();

        final Expiration e1 = new Expiration( new ExpirationKey( "test", "one" ), 500 );
        final Expiration e2 = new Expiration( new ExpirationKey( "test", "two" ), 750 );

        assertThat( expirations.add( e1 ), equalTo( true ) );
        assertThat( expirations.add( e2 ), equalTo( true ) );

        final String key = "key";
        blocks.put( key, expirations );

        final ExpirationBlockStore store = getStore();

        logger.debug( "storing blocks: {}", blocks );
        store.writeBlocks( blocks );

        store.flushCaches();

        final Set<Expiration> block = store.getBlock( key );

        logger.debug( "For key: {}, retrieved block: {}", key, block );
        assertThat( block.size(), equalTo( 2 ) );

        final Iterator<Expiration> mine = expirations.iterator();
        final Iterator<Expiration> retrieved = block.iterator();
        while ( mine.hasNext() )
        {
            final Expiration my = mine.next();
            assertThat( my + " has no corresponding element in: " + retrieved, retrieved.hasNext(), equalTo( true ) );

            final Expiration other = retrieved.next();
            assertThat( my + " != " + other, other, equalTo( my ) );
        }
    }

    @Test
    public void addToBlockAndRetrieveBlockIncludingNewExpirationInProperOrder()
        throws Exception
    {
        final Map<String, Set<Expiration>> blocks = new HashMap<String, Set<Expiration>>();
        final Set<Expiration> expirations = new TreeSet<Expiration>();

        final Expiration e1 = new Expiration( new ExpirationKey( "test", "1" ), 500 );
        final Expiration e2 = new Expiration( new ExpirationKey( "test", "2" ), 700 );
        final Expiration e3 = new Expiration( new ExpirationKey( "test", "3" ), 1000 );

        assertThat( expirations.add( e1 ), equalTo( true ) );
        assertThat( expirations.add( e3 ), equalTo( true ) );

        final String key = "key";
        blocks.put( key, expirations );

        final ExpirationBlockStore store = getStore();

        logger.debug( "storing blocks: {}", blocks );
        store.writeBlocks( blocks );

        store.flushCaches();

        store.addToBlock( key, e2 );

        store.flushCaches();

        final Set<Expiration> block = store.getBlock( key );

        assertThat( block.size(), equalTo( 3 ) );

        final Iterator<Expiration> exps = block.iterator();
        assertThat( exps.hasNext(), equalTo( true ) );
        assertThat( exps.next(), equalTo( e1 ) );

        assertThat( exps.hasNext(), equalTo( true ) );
        assertThat( exps.next(), equalTo( e2 ) );

        assertThat( exps.hasNext(), equalTo( true ) );
        assertThat( exps.next(), equalTo( e3 ) );
    }

    @Test
    public void removeOneFromBlockOfTwoLeavesOne()
        throws Exception
    {
        final Map<String, Set<Expiration>> blocks = new HashMap<String, Set<Expiration>>();
        final Set<Expiration> expirations = new TreeSet<Expiration>();

        final Expiration e1 = new Expiration( new ExpirationKey( "test", "one" ), 500 );
        final Expiration e2 = new Expiration( new ExpirationKey( "test", "two" ), 750 );

        assertThat( expirations.add( e1 ), equalTo( true ) );
        assertThat( expirations.add( e2 ), equalTo( true ) );

        final String key = "key";
        blocks.put( key, expirations );

        final ExpirationBlockStore store = getStore();

        logger.debug( "storing blocks: {}", blocks );
        store.writeBlocks( blocks );

        store.flushCaches();

        Set<Expiration> block = store.getBlock( key );

        logger.debug( "For key: {}, retrieved block: {}", key, block );
        assertThat( block.size(), equalTo( 2 ) );

        store.removeFromBlock( key, e2 );

        store.flushCaches();

        block = store.getBlock( key );

        logger.debug( "After removal; for key: {}, retrieved block: {}", key, block );
        assertThat( block.size(), equalTo( 1 ) );
        assertThat( block.contains( e1 ), equalTo( true ) );
    }

    @Test
    public void writeThenRemoveBlockResultsInRetrievalOfNullBlock()
        throws Exception
    {
        final Map<String, Set<Expiration>> blocks = new HashMap<String, Set<Expiration>>();
        final Set<Expiration> expirations = new TreeSet<Expiration>();

        final Expiration e1 = new Expiration( new ExpirationKey( "test", "one" ), 500 );
        final Expiration e2 = new Expiration( new ExpirationKey( "test", "two" ), 750 );

        assertThat( expirations.add( e1 ), equalTo( true ) );
        assertThat( expirations.add( e2 ), equalTo( true ) );

        final String key = "key";
        blocks.put( key, expirations );

        final ExpirationBlockStore store = getStore();

        logger.debug( "storing blocks: {}", blocks );
        store.writeBlocks( blocks );

        store.flushCaches();

        Set<Expiration> block = store.getBlock( key );

        logger.debug( "For key: {}, retrieved block: {}", key, block );
        assertThat( block.size(), equalTo( 2 ) );

        store.removeBlocks( key );

        store.flushCaches();

        block = store.getBlock( key );

        logger.debug( "After removal; for key: {}, retrieved block: {}", key, block );
        assertThat( block, nullValue() );
    }

    @Test
    public void writeThreeBlocksThenRemoveTwoAndRetrieveRemaining()
        throws Exception
    {
        final Map<String, Set<Expiration>> blocks = new HashMap<String, Set<Expiration>>();
        final Set<Expiration> expirations = new TreeSet<Expiration>();

        final Expiration e1 = new Expiration( new ExpirationKey( "test", "one" ), 500 );
        final Expiration e2 = new Expiration( new ExpirationKey( "test", "two" ), 750 );

        assertThat( expirations.add( e1 ), equalTo( true ) );
        assertThat( expirations.add( e2 ), equalTo( true ) );

        final String key = "key";
        blocks.put( key, expirations );

        final Set<Expiration> expirations2 = new TreeSet<Expiration>();

        final Expiration e3 = new Expiration( new ExpirationKey( "test", "three" ), 500 );
        final Expiration e4 = new Expiration( new ExpirationKey( "test", "four" ), 750 );

        assertThat( expirations2.add( e3 ), equalTo( true ) );
        assertThat( expirations2.add( e4 ), equalTo( true ) );

        final String key2 = "key2";
        blocks.put( key2, expirations2 );

        final Set<Expiration> expirations3 = new TreeSet<Expiration>();

        final Expiration e5 = new Expiration( new ExpirationKey( "test", "five" ), 500 );
        final Expiration e6 = new Expiration( new ExpirationKey( "test", "six" ), 750 );

        assertThat( expirations3.add( e5 ), equalTo( true ) );
        assertThat( expirations3.add( e6 ), equalTo( true ) );

        final String key3 = "key3";
        blocks.put( key3, expirations3 );

        final Set<String> toRemove = new HashSet<String>();
        toRemove.add( key );
        toRemove.add( key3 );

        final ExpirationBlockStore store = getStore();

        logger.debug( "storing blocks: {}", blocks );
        store.writeBlocks( blocks );

        store.flushCaches();

        Set<Expiration> block = store.getBlock( key );

        logger.debug( "For key: {}, retrieved block: {}", key, block );
        assertThat( block.size(), equalTo( 2 ) );

        block = store.getBlock( key2 );

        logger.debug( "For key: {}, retrieved block: {}", key2, block );
        assertThat( block.size(), equalTo( 2 ) );

        block = store.getBlock( key3 );

        logger.debug( "For key: {}, retrieved block: {}", key3, block );
        assertThat( block.size(), equalTo( 2 ) );

        store.removeBlocks( toRemove );

        store.flushCaches();

        block = store.getBlock( key );

        logger.debug( "After removal; for key: {}, retrieved block: {}", key, block );
        assertThat( block, nullValue() );

        block = store.getBlock( key2 );

        logger.debug( "After removal; for key: {}, retrieved block: {}", key2, block );
        assertThat( block, notNullValue() );
        assertThat( block.size(), equalTo( 2 ) );

        block = store.getBlock( key3 );

        logger.debug( "After removal; for key: {}, retrieved block: {}", key3, block );
        assertThat( block, nullValue() );
    }

}
