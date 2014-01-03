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
package org.commonjava.shelflife.store.flat;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.concurrent.Executors;

import org.commonjava.shelflife.ExpirationBlockStoreTCK;
import org.commonjava.shelflife.store.ExpirationBlockStore;
import org.commonjava.shelflife.util.BlockKeyUtils;
import org.commonjava.web.json.ser.JsonSerializer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FlatExpirationBlockStoreTest
    extends ExpirationBlockStoreTCK
{

    private FlatBlockStore store;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void setupStore()
    {
        final FlatBlockStoreConfiguration config = new FlatBlockStoreConfiguration( tempFolder.newFolder( "expiration-store" ) );

        final JsonSerializer serializer = new JsonSerializer();

        store = new FlatBlockStore( config, serializer, Executors.newScheduledThreadPool( 1 ) );
    }

    @Override
    protected ExpirationBlockStore getStore()
    {
        return store;
    }

    @Test
    public void testKeyFormatting()
    {
        final String key = BlockKeyUtils.generateCurrentBlockKey();
        final File f = store.getBlockFile( key );

        assertThat( f, notNullValue() );
        assertThat( store.isBlockFile( f ), equalTo( true ) );

        final String extractedKey = store.getKey( f );
        assertThat( extractedKey, equalTo( key ) );
    }

}
