package org.commonjava.shelflife.expire.match;

import org.commonjava.shelflife.model.Expiration;

public class AndMatcher
    implements ExpirationMatcher
{

    private final ExpirationMatcher[] matchers;

    public AndMatcher( final ExpirationMatcher... matchers )
    {
        this.matchers = matchers;
    }

    @Override
    public boolean matches( final Expiration expiration )
    {
        for ( final ExpirationMatcher matcher : matchers )
        {
            if ( !matcher.matches( expiration ) )
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public String formatQuery()
    {
        final StringBuilder sb = new StringBuilder();

        for ( final ExpirationMatcher matcher : matchers )
        {
            if ( sb.length() > 0 )
            {
                sb.append( " && " );
            }

            sb.append( matcher.formatQuery() );
        }

        return sb.toString();
    }

}
