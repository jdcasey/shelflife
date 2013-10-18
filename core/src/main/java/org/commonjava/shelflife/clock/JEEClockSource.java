package org.commonjava.shelflife.clock;

import javax.annotation.ManagedBean;
import javax.ejb.Schedule;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

import org.commonjava.shelflife.ExpirationManager;
import org.commonjava.shelflife.ExpirationManagerException;

@ApplicationScoped
@ManagedBean
@Alternative
public class JEEClockSource
    implements ExpirationClockSource
{

    private ExpirationManager manager;

    @Schedule( minute = "/5", hour = "*" )
    public void tick()
    {
        if ( manager != null )
        {
            manager.clearExpired();
        }
    }

    @Override
    public void start( final ExpirationManager manager )
        throws ExpirationManagerException
    {
        this.manager = manager;
    }

    @Override
    public void stop()
    {
        manager = null;
    }

}
