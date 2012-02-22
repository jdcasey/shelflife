package org.commonjava.shelflife.model;

import java.util.Map;

public interface Expiration
{

    ExpirationKey getKey();

    long getExpires();

    boolean isActive();

    void deactivate();

    Map<?, ?> getData();

}
