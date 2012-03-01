package org.commonjava.shelflife.file;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.commonjava.util.logging.Log4jUtil;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ShelflifeFilerTest
{

    private ShelflifeFiler filer;

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Before
    public void setup()
    {
        Log4jUtil.configure( Level.DEBUG );

        final WeldContainer weld = new Weld().initialize();

        filer = weld.instance()
                    .select( ShelflifeFiler.class )
                    .get();
    }

    @Test
    public void scheduleOneFileAndWaitForDeletion()
        throws Exception
    {
        final File f = temp.newFile( "expiring.txt" );
        FileUtils.write( f, "Test data" );

        assertThat( f.exists(), equalTo( true ) );

        filer.scheduleExpiration( 500, f );

        Thread.sleep( 600 );

        assertThat( f.exists(), equalTo( false ) );
    }

}
