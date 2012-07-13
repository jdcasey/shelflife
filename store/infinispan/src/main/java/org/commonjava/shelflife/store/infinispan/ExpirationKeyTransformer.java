package org.commonjava.shelflife.store.infinispan;

import org.commonjava.shelflife.util.ExpirationKeyBridge;
import org.infinispan.query.Transformer;

public class ExpirationKeyTransformer
    implements Transformer
{

    @Override
    public Object fromString( final String s )
    {
        return new ExpirationKeyBridge().stringToObject( s );
    }

    @Override
    public String toString( final Object customType )
    {
        return new ExpirationKeyBridge().objectToString( customType );
    }

}
