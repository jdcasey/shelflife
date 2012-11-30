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
