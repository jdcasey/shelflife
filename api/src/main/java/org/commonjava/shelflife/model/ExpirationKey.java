package org.commonjava.shelflife.model;

import static org.apache.commons.lang.StringUtils.join;

import java.io.Serializable;
import java.util.Arrays;

public class ExpirationKey
    implements Serializable
{

    private static final long serialVersionUID = 1L;

    private final String[] parts;

    public ExpirationKey( final String firstPart, final String... parts )
    {
        if ( firstPart == null )
        {
            throw new NullPointerException();
        }

        final String[] all = new String[parts.length + 1];
        all[0] = firstPart;
        System.arraycopy( parts, 0, all, 1, parts.length );

        this.parts = all;
    }

    public String[] getParts()
    {
        final String[] result = new String[parts.length];
        System.arraycopy( parts, 0, result, 0, parts.length );

        return result;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode( parts );
        return result;
    }

    @Override
    public boolean equals( final Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null )
        {
            return false;
        }
        if ( getClass() != obj.getClass() )
        {
            return false;
        }
        final ExpirationKey other = (ExpirationKey) obj;
        if ( !Arrays.equals( parts, other.parts ) )
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return join( parts, ":" );
    }

}
