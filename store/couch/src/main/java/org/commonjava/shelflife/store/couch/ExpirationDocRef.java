package org.commonjava.shelflife.store.couch;

import org.commonjava.couch.model.CouchDocRef;
import org.commonjava.shelflife.model.Expiration;

public class ExpirationDocRef
    extends CouchDocRef
{

    public ExpirationDocRef( final Expiration expiration )
    {
        super( ExpirationWrapperDoc.docId( expiration ) );
    }

}
