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
package org.commonjava.shelflife.store;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.commonjava.shelflife.ExpirationManagerException;
import org.commonjava.shelflife.model.Expiration;

public interface ExpirationBlockStore
{

    List<String> listKeysInOrder();

    void writeBlocks( Map<String, Set<Expiration>> currentBlocks )
        throws ExpirationManagerException;

    void addToBlock( String key, Expiration expiration )
        throws ExpirationManagerException;

    Set<Expiration> getBlock( String key )
        throws ExpirationManagerException;

    void removeFromBlock( String key, Expiration expiration )
        throws ExpirationManagerException;

    void removeBlocks( Set<String> currentKeys )
        throws ExpirationManagerException;

    void removeBlocks( String... currentKeys )
        throws ExpirationManagerException;

    void flushCaches()
        throws ExpirationManagerException;
}
