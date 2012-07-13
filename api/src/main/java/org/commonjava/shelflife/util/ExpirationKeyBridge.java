package org.commonjava.shelflife.util;

import org.commonjava.shelflife.model.ExpirationKey;
import org.hibernate.search.bridge.TwoWayStringBridge;

public class ExpirationKeyBridge
    implements TwoWayStringBridge
{

    @Override
    public String objectToString( final Object object )
    {
        return object.toString();
    }

    @Override
    public Object stringToObject( final String stringValue )
    {
        final String[] parts = stringValue.split( ":" );
        final String[] secondaryParts = new String[parts.length - 1];
        System.arraycopy( parts, 1, secondaryParts, 0, secondaryParts.length );

        return new ExpirationKey( parts[0], secondaryParts );
    }

}
