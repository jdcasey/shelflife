package org.commonjava.shelflife.event;

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

    @Override
    public String toString()
    {
        return String.format( "ExpirationEvent [exp: %s, type: %s]", expiration, type );
    }

}
