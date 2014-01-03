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
