package org.commonjava.shelflife.store.couch;

import static org.commonjava.couch.util.IdUtils.namespaceId;

import org.commonjava.couch.model.AbstractCouchDocument;
import org.commonjava.shelflife.model.Expiration;
import org.commonjava.web.json.ser.JsonAdapters;

@JsonAdapters( ExpirationWrapperSerializer.class )
public class ExpirationWrapperDoc
    extends AbstractCouchDocument
{

    public static final String NAMESPACE = "expiration";

    private final Expiration expiration;

    public ExpirationWrapperDoc( final Expiration expiration )
    {
        this( expiration, null );
    }

    public ExpirationWrapperDoc( final Expiration expiration, final String rev )
    {
        this.expiration = expiration;
        setCouchDocId( docId( expiration ) );

        if ( rev != null )
        {
            setCouchDocRev( rev );
        }
    }

    public Expiration getExpiration()
    {
        return expiration;
    }

    public static String docId( final Expiration expiration )
    {
        return namespaceId( NAMESPACE, expiration.getKey()
                                                 .toString() );
    }

}
