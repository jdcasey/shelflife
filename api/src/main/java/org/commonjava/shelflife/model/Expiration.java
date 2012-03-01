package org.commonjava.shelflife.model;

public final class Expiration
{

    private final ExpirationKey key;

    private final long expires;

    private final Object data;

    private transient boolean active = true;

    private transient boolean canceled = false;

    public Expiration( final ExpirationKey key, final long expires, final Object data )
    {
        this.key = key;
        this.expires = expires < System.currentTimeMillis() ? expires + System.currentTimeMillis() : expires;
        this.data = data;
    }

    public Expiration( final ExpirationKey key, final long expires )
    {
        this( key, expires, null );
    }

    // dead; for queries
    public Expiration( final ExpirationKey key )
    {
        this.key = key;
        this.expires = 0;
        this.data = null;
        this.active = false;
    }

    public ExpirationKey getKey()
    {
        return key;
    }

    public long getExpires()
    {
        return expires;
    }

    public boolean isActive()
    {
        return active;
    }

    public boolean isCanceled()
    {
        return canceled;
    }

    public void expire()
    {
        active = false;
    }

    public void cancel()
    {
        active = false;
        canceled = true;
    }

    public Object getData()
    {
        return data;
    }

    @Override
    public String toString()
    {
        return String.format( "Expiration [key=%s]", key );
    }

}
