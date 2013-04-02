package org.commonjava.shelflife.event;

import org.commonjava.shelflife.model.Expiration;

public interface ExpirationEventManager
{

    ExpirationEvent fire( Expiration expiration, ExpirationEventType type );

}
