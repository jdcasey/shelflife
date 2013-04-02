package org.commonjava.shelflife.store.flat;

import java.io.File;

import javax.enterprise.inject.Alternative;

@Alternative
public class FlatBlockStoreConfiguration
{

    private final File storageDir;

    public FlatBlockStoreConfiguration( final File storageDir )
    {
        this.storageDir = storageDir;
    }

    public File getStorageDirectory()
    {
        return storageDir;
    }

}
