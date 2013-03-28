package org.commonjava.shelflife.expire.clock;

import javax.annotation.ManagedBean;
import javax.ejb.Schedule;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.commonjava.shelflife.expire.ExpirationManager;

@ApplicationScoped
@ManagedBean
public class JEEClockSource
{

    @Inject
    private ExpirationManager manager;

    @Schedule( minute = "/5", hour = "*" )
    public void tick()
    {
        manager.clearExpired();
    }

}
