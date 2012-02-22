package org.commonjava.shelflife.fixture;

import java.util.Map;

import org.commonjava.shelflife.model.Expiration;
import org.commonjava.shelflife.model.ExpirationKey;

public class TestExpiration
    implements Expiration
{

    private final ExpirationKey key;

    private final long expires;

    private boolean active = true;

    private final Map<?, ?> data;

    public TestExpiration( final String name, final int expireMillis )
    {
        this( name, expireMillis, null );
    }

    public TestExpiration( final String name, final int expireMillis, final Map<?, ?> data )
    {
        key = new ExpirationKey( "test", name );
        expires = System.currentTimeMillis() + expireMillis;
        this.data = data;
    }

    @Override
    public ExpirationKey getKey()
    {
        return key;
    }

    @Override
    public long getExpires()
    {
        return expires;
    }

    @Override
    public boolean isActive()
    {
        return active;
    }

    @Override
    public void deactivate()
    {
        active = false;
    }

    @Override
    public Map<?, ?> getData()
    {
        return data;
    }

}
