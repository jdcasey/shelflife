package org.commonjava.shelflife.store.flat;

import org.commonjava.shelflife.ExpirationBlockStoreTCK;
import org.commonjava.shelflife.store.ExpirationBlockStore;
import org.commonjava.web.json.ser.JsonSerializer;
import org.junit.Before;
import org.junit.Rule;
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
        final FlatBlockStoreConfiguration config =
            new FlatBlockStoreConfiguration( tempFolder.newFolder( "expiration-store" ) );

        final JsonSerializer serializer = new JsonSerializer();

        store = new FlatBlockStore( config, serializer );
    }

    @Override
    protected ExpirationBlockStore getStore()
    {
        return store;
    }

}
