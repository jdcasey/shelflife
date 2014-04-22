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
package org.commonjava.shelflife.store.flat;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.commonjava.shelflife.inject.Shelflife;
import org.commonjava.web.json.ser.JsonSerializer;

@ApplicationScoped
public class ShelflifeSerializerProvider
{

    private JsonSerializer serializer;

    @Produces
    @Shelflife
    public synchronized JsonSerializer getShelflifeSerializer()
    {
        if ( serializer == null )
        {
            serializer = new JsonSerializer();
        }

        return serializer;
    }

}
