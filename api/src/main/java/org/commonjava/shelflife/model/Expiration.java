/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.commonjava.shelflife.model;

import java.io.Serializable;

import org.commonjava.shelflife.event.ExpirationEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Expiration
    implements Serializable, Comparable<Expiration>
{

    private transient final Logger logger = LoggerFactory.getLogger( getClass() );

    private static final long serialVersionUID = 1L;

    private final ExpirationKey key;

    private final long expires;

    private final Object data;

    private transient boolean active = true;

    private transient boolean canceled = false;

    private transient ExpirationEventType lastType;

    public Expiration( final ExpirationKey key, final long expires, final Object data )
    {
        this.key = key;
        final long current = System.currentTimeMillis();
        this.expires = expires < current ? expires + current : expires;

        logger.debug( "[{}] Expire given as: {}, actual expires set to: {}", current, expires, this.expires );

        this.data = data;
    }

    public Expiration( final ExpirationKey key, final long expires )
    {
        this( key, expires, null );
    }

    // dead; for queries
    public Expiration( final ExpirationKey key )
    {
        this.key = key;
        this.expires = 0;
        this.data = null;
        this.active = false;
    }

    public ExpirationEventType getLastEventType()
    {
        return lastType;
    }

    public ExpirationEventType setLastEventType( final ExpirationEventType type )
    {
        final ExpirationEventType t = lastType;
        lastType = type;

        return t;
    }

    public ExpirationKey getKey()
    {
        return key;
    }

    public long getExpires()
    {
        return expires;
    }

    public boolean isActive()
    {
        return active;
    }

    public boolean isCanceled()
    {
        return canceled;
    }

    public void schedule()
    {
        this.lastType = ExpirationEventType.SCHEDULE;
    }

    public void expire()
    {
        active = false;
        lastType = ExpirationEventType.EXPIRE;
    }

    public void cancel()
    {
        active = false;
        canceled = true;
        lastType = ExpirationEventType.CANCEL;
    }

    public Object getData()
    {
        return data;
    }

    @Override
    public String toString()
    {
        return String.format( "Expiration [key=%s]", key );
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( key == null ) ? 0 : key.hashCode() );
        return result;
    }

    @Override
    public boolean equals( final Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null )
        {
            return false;
        }
        if ( getClass() != obj.getClass() )
        {
            return false;
        }
        final Expiration other = (Expiration) obj;
        if ( key == null )
        {
            if ( other.key != null )
            {
                return false;
            }
        }
        else if ( !key.equals( other.key ) )
        {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo( final Expiration other )
    {
        final Long exp = expires;
        int comp = exp.compareTo( other.expires );
        if ( comp == 0 )
        {
            comp = key.compareTo( other.key );
        }

        return comp;
    }

}
