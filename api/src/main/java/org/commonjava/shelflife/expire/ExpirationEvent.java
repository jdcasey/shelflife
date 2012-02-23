package org.commonjava.shelflife.expire;

import org.commonjava.shelflife.model.Expiration;

public class ExpirationEvent
{

    private final Expiration expiration;

    private final ExpirationEventType type;

    public ExpirationEvent( final Expiration exp, final ExpirationEventType type )
    {
        this.expiration = exp;
        this.type = type;
    }

    public ExpirationEventType getType()
    {
        return type;
    }

    public Expiration getExpiration()
    {
        return expiration;
    }

}
