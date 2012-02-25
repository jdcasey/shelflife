package org.commonjava.shelflife.store.flat;

import java.io.File;

public class FlatShelflifeStoreConfiguration
{

    private final File storageDir;

    public FlatShelflifeStoreConfiguration( final File storageDir )
    {
        this.storageDir = storageDir;
    }

    public File getStorageDirectory()
    {
        return storageDir;
    }

}
