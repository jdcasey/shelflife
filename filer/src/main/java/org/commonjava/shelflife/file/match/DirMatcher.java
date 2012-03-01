package org.commonjava.shelflife.file.match;

import static org.apache.commons.lang.StringUtils.isEmpty;

import java.io.File;

import org.commonjava.shelflife.expire.match.ExpirationMatcher;
import org.commonjava.shelflife.file.ShelflifeFiler;
import org.commonjava.shelflife.model.Expiration;

public class DirMatcher
    implements ExpirationMatcher
{

    private final String basepath;

    public DirMatcher( final File dir )
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
        if ( isEmpty( path ) )
        {
            return false;
        }

        final File f = new File( path );
        final File d = f.isDirectory() ? f : f.getParentFile();
        return d != null && basepath.equals( d.getAbsolutePath() );
    }

    @Override
    public String formatQuery()
    {
        return basepath + "/*";
    }

}
