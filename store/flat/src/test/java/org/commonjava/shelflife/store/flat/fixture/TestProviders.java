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
