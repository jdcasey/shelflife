package org.commonjava.shelflife.store.memory;

import org.commonjava.shelflife.ExpirationBlockStoreTCK;
import org.commonjava.shelflife.store.ExpirationBlockStore;

public class MemoryExpirationBlockStoreTest
    extends ExpirationBlockStoreTCK
{

    private final MemoryBlockStore store = new MemoryBlockStore();

    @Override
    protected ExpirationBlockStore getStore()
    {
        return store;
    }

}
