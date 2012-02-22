package org.commonjava.shelflife.expire.match;

import org.commonjava.shelflife.model.Expiration;

public interface ExpirationMatcher
{

    boolean matches( Expiration expiration );

    String formatQuery();

}
