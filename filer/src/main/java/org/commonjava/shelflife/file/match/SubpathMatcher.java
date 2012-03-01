package org.commonjava.shelflife.file.match;

import java.io.File;

import org.commonjava.shelflife.expire.match.ExpirationMatcher;
import org.commonjava.shelflife.file.ShelflifeFiler;
import org.commonjava.shelflife.model.Expiration;

public class SubpathMatcher
    implements ExpirationMatcher
{

    private final String basepath;

    public SubpathMatcher( final File dir )
    {
        basepath = dir.getAbsolutePath();
    }

    @Override
    public boolean matches( final Expiration expiration )
    {
        if ( !ShelflifeFiler.FILE_TYPE_MATCHER.matches( expiration ) )
        {
            return false;
        }

        final String path = (String) expiration.getData();
        return path != null && path.startsWith( basepath );
    }

    @Override
    public String formatQuery()
    {
        return basepath + "/**";
    }

}
