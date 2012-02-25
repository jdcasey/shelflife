package org.commonjava.shelflife.model;

public final class Expiration
{

    private final ExpirationKey key;

    private final long expires;

    private final Object data;

    private transient boolean active = true;

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

    public void deactivate()
    {
        active = false;
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
