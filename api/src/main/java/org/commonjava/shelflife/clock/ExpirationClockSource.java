package org.commonjava.shelflife.clock;

import org.commonjava.shelflife.ExpirationManager;
import org.commonjava.shelflife.ExpirationManagerException;

public interface ExpirationClockSource
{

    void start( ExpirationManager manager )
        throws ExpirationManagerException;

    void stop();

}
