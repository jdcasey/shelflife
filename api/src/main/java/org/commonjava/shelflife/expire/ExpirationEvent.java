package org.commonjava.shelflife.expire;

import org.commonjava.shelflife.model.Expiration;

public class ExpirationEvent
{

    private final Expiration expiration;

    public ExpirationEvent( final Expiration exp )
    {
        this.expiration = exp;
    }

    public Expiration getExpiration()
    {
        return expiration;
    }

}
