package org.commonjava.shelflife.store.couch;

import java.util.HashSet;
import java.util.Set;

import org.commonjava.couch.db.model.AppDescription;
import org.commonjava.couch.model.CouchApp;

public class ShelflifeApp
    extends CouchApp
{

    public static final String APP_NAME = "shelflife-logic";

    public ShelflifeApp()
    {
        super( APP_NAME, new ShelflifeAppDescription() );
    }

    public static final class ShelflifeAppDescription
        implements AppDescription
    {

        @Override
        public String getAppName()
        {
            return ShelflifeApp.APP_NAME;
        }

        @Override
        public String getClasspathAppResource()
        {
            return ShelflifeApp.APP_NAME;
        }

        @Override
        public Set<String> getViewNames()
        {
            return ShelflifeViews.viewNames();
        }

    }

    public enum ShelflifeViews
    {
        ALL_EXPIRATIONS( "all-expirations" );

        private String viewName;

        private ShelflifeViews( final String viewName )
        {
            this.viewName = viewName;
        }

        public String viewName()
        {
            return viewName;
        }

        public static Set<String> viewNames()
        {
            final Set<String> names = new HashSet<String>();
            for ( final ShelflifeViews view : ShelflifeViews.values() )
            {
                names.add( view.viewName() );
            }

            return names;
        }
    }

}
