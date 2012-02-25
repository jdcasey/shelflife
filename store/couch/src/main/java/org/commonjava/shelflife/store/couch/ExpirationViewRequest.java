package org.commonjava.shelflife.store.couch;

import org.commonjava.couch.db.model.ViewRequest;

public class ExpirationViewRequest
    extends ViewRequest
{

    public ExpirationViewRequest( final ShelflifeApp.ShelflifeViews view )
    {
        super( ShelflifeApp.APP_NAME, view.viewName() );
    }

}
