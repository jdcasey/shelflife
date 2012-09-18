package org.commonjava.shelflife.store.infinispan;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class BlockKeyUtilsTest
{

    @Test
    public void generateCurrentBlockKey()
    {
        final String blockKey = BlockKeyUtils.generateCurrentBlockKey();
        System.out.println( blockKey );
    }

    @Test
    public void generateNextBlockKey_CurrentTime()
    {
        final String blockKey = BlockKeyUtils.generateNextBlockKey( System.currentTimeMillis() );
        System.out.println( blockKey );
    }

    @Test
    public void nextBlockKeyOffsetByOneFromCurrent()
    {
        final String current = BlockKeyUtils.generateCurrentBlockKey();
        final String next = BlockKeyUtils.generateNextBlockKey( System.currentTimeMillis() );

        int idx = current.indexOf( '-' );
        final String currentBlock = current.substring( idx + 1 );

        idx = next.indexOf( '-' );
        final String nextBlock = next.substring( idx + 1 );

        final int currentBlockInt = Integer.parseInt( currentBlock );
        final int nextBlockInt = Integer.parseInt( nextBlock );

        assertThat( ( nextBlockInt - currentBlockInt ), equalTo( 1 ) );
    }

}
