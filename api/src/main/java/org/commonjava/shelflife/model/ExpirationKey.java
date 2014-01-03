/*******************************************************************************
 * Copyright (C) 2014 John Casey.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.commonjava.shelflife.model;

import static org.apache.commons.lang.StringUtils.join;

import java.io.Serializable;
import java.util.Arrays;

public class ExpirationKey
    implements Serializable, Comparable<ExpirationKey>
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
        result = prime * result;

        int idx = 1;
        for ( final String part : parts )
        {
            result += part.hashCode() / idx++;
        }

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

    @Override
    public int compareTo( final ExpirationKey other )
    {
        int comp = 0;

        int i = 0;
        for ( ; comp == 0 && i < parts.length; i++ )
        {
            final String part = parts[i];
            if ( other.parts.length > i )
            {
                comp = part.compareTo( other.parts[i] );
            }
            else
            {
                comp = 1;
                break;
            }
        }

        if ( comp == 0 && parts.length < other.parts.length )
        {
            comp = -1;
        }

        return comp;
    }

}
