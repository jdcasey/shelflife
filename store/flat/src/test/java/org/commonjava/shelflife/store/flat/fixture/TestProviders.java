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
package org.commonjava.shelflife.store.flat.fixture;

import java.io.File;
import java.io.IOException;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.commons.io.FileUtils;
import org.commonjava.shelflife.store.flat.FlatBlockStoreConfiguration;

@ApplicationScoped
public class TestProviders
{

    private File storeDir;

    private FlatBlockStoreConfiguration config;

    @Produces
    public FlatBlockStoreConfiguration getConfig()
        throws IOException
    {
        if ( config == null )
        {
            if ( storeDir == null )
            {
                storeDir = File.createTempFile( "flat.", ".store" );
                storeDir.delete();
                storeDir.mkdirs();
            }

            config = new FlatBlockStoreConfiguration( storeDir );
        }

        return config;
    }

    @PreDestroy
    public void stop()
        throws IOException
    {
        if ( storeDir != null )
        {
            FileUtils.forceDelete( storeDir );
        }
    }

}
