package org.commonjava.shelflife.match;

import org.commonjava.shelflife.model.Expiration;
import org.commonjava.shelflife.model.ExpirationKey;

public class KeyMatcher
    implements ExpirationMatcher
{

    private final ExpirationKey key;

    public KeyMatcher( final ExpirationKey key )
    {
        this.key = key;
    }

    @Override
    public boolean matches( final Expiration expiration )
    {
        return key.equals( expiration.getKey() );
    }

    @Override
    public String formatQuery()
    {
        return "KEY match [" + key + "]";
    }

}
