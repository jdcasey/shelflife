package org.commonjava.shelflife.store.infinispan.inject;

public enum ShelflifeCaches
{

    BLOCKS( "shelflife-blocks" ), DATA( "shelflife-data" );

    private final String cacheName;

    private ShelflifeCaches( final String cn )
    {
        this.cacheName = cn;
    }

    public String cacheName()
    {
        return cacheName;
    }

}
