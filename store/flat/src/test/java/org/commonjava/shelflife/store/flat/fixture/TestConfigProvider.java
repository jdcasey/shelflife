package org.commonjava.shelflife.store.flat.fixture;

import java.io.File;
import java.io.IOException;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.commonjava.shelflife.store.flat.FlatShelflifeStoreConfiguration;

@Singleton
public class TestConfigProvider
{

    private FlatShelflifeStoreConfiguration config;

    @Produces
    @Default
    public synchronized FlatShelflifeStoreConfiguration getConfig()
        throws IOException
    {
        if ( config == null )
        {
            final File dir = File.createTempFile( "shelflife-store.", ".dir" );
            dir.delete();
            dir.mkdirs();

            config = new FlatShelflifeStoreConfiguration( dir );
        }

        return config;
    }

}
