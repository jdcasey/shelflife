package org.commonjava.shelflife.model;

import java.io.Serializable;

import org.commonjava.shelflife.util.ExpirationKeyBridge;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.ProvidedId;

@Indexed
@ProvidedId
public final class Expiration
    implements Serializable
{

    private static final long serialVersionUID = 1L;

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

    @Field
    @FieldBridge( impl = ExpirationKeyBridge.class )
    public ExpirationKey getKey()
    {
        return key;
    }

    @Field
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

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( key == null ) ? 0 : key.hashCode() );
        return result;
    }

    @Override
    public boolean equals( final Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null )
        {
            return false;
        }
        if ( getClass() != obj.getClass() )
        {
            return false;
        }
        final Expiration other = (Expiration) obj;
        if ( key == null )
        {
            if ( other.key != null )
            {
                return false;
            }
        }
        else if ( !key.equals( other.key ) )
        {
            return false;
        }
        return true;
    }

}
