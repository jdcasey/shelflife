package org.commonjava.shelflife.store.couch.fixture;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.commonjava.couch.conf.DefaultCouchDBConfiguration;
import org.commonjava.couch.db.CouchFactory;
import org.commonjava.couch.db.CouchManager;
import org.commonjava.shelflife.inject.Shelflife;

@Singleton
public class TestCouchProvider
{

    @Inject
    private CouchFactory factory;

    private CouchManager couch;

    @Produces
    @Default
    @Shelflife
    public synchronized CouchManager getCouch()
    {
        if ( couch == null )
        {
            couch = factory.getCouchManager( new DefaultCouchDBConfiguration( "http://localhost:5984/test-shelflife" ) );
        }

        return couch;
    }

}
