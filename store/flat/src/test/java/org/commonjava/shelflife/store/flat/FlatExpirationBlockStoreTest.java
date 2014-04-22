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
