package org.commonjava.shelflife.store.flat;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.enterprise.inject.Alternative;

@Alternative
public class FlatBlockStoreConfiguration
{

    private static final long DEFAULT_CACHE_FLUSH_TIMEOUT = TimeUnit.MILLISECONDS.convert( 1, TimeUnit.MINUTES );

    private final File storageDir;

    private long cacheFlushMillis = DEFAULT_CACHE_FLUSH_TIMEOUT;

    public FlatBlockStoreConfiguration( final File storageDir )
    {
        this.storageDir = storageDir;
    }

    public File getStorageDirectory()
    {
        return storageDir;
    }

    public void setCacheFlushSeconds( final long cacheFlushSeconds )
    {
        this.cacheFlushMillis = TimeUnit.MILLISECONDS.convert( cacheFlushSeconds, TimeUnit.SECONDS );
    }

    public long getCacheFlushMillis()
    {
        return cacheFlushMillis;
    }

}
