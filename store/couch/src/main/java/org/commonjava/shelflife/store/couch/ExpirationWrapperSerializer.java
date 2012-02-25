package org.commonjava.shelflife.store.couch;

import java.lang.reflect.Type;

import org.commonjava.shelflife.model.Expiration;
import org.commonjava.shelflife.model.ExpirationKey;
import org.commonjava.web.json.ser.WebSerializationAdapter;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ExpirationWrapperSerializer
    implements WebSerializationAdapter, JsonSerializer<ExpirationWrapperDoc>, JsonDeserializer<ExpirationWrapperDoc>
{

    @Override
    public ExpirationWrapperDoc deserialize( final JsonElement json, final Type typeOfT,
                                             final JsonDeserializationContext context )
        throws JsonParseException
    {
        final JsonObject obj = json.getAsJsonObject();

        String rev = null;
        final JsonElement revEl = obj.get( "_rev" );
        if ( revEl != null )
        {
            rev = revEl.getAsString();
        }

        final long expires = obj.get( "expires" )
                                .getAsLong();

        final String fullKey = obj.get( "key" )
                                  .getAsString();

        final String[] allParts = fullKey.split( ":" );
        final String firstPart = allParts[0];
        final String[] additionParts = new String[allParts.length - 1];
        if ( additionParts.length > 0 )
        {
            System.arraycopy( allParts, 1, additionParts, 0, additionParts.length );
        }

        final ExpirationKey key = new ExpirationKey( firstPart, additionParts );

        Object data = null;

        final JsonElement dataEl = obj.get( "data" );
        final JsonElement dataTypeEl = obj.get( "data_type" );
        if ( dataEl != null && dataTypeEl != null )
        {
            final String cls = dataTypeEl.getAsString();

            if ( cls == null )
            {
                data = dataEl.getAsString();
            }
            else
            {
                Class<?> clazz;
                try
                {
                    clazz = Thread.currentThread()
                                  .getContextClassLoader()
                                  .loadClass( cls );

                    data = context.deserialize( dataEl, clazz );
                }
                catch ( final ClassNotFoundException e )
                {
                    throw new JsonParseException( "Cannot deserialize data field: " + dataEl + ". Class: " + cls
                        + " (listed in data_type element) was not found." );
                }
            }
        }

        final Expiration exp = new Expiration( key, expires, data );

        return new ExpirationWrapperDoc( exp, rev );
    }

    @Override
    public JsonElement serialize( final ExpirationWrapperDoc src, final Type typeOfSrc,
                                  final JsonSerializationContext context )
    {

        final JsonObject obj = new JsonObject();

        obj.addProperty( "_id", src.getCouchDocId() );
        obj.addProperty( "doctype", ExpirationWrapperDoc.NAMESPACE );

        if ( src.getCouchDocRev() != null )
        {
            obj.addProperty( "_rev", src.getCouchDocRev() );
        }

        final Expiration exp = src.getExpiration();
        obj.addProperty( "key", exp.getKey()
                                   .toString() );
        obj.addProperty( "expires", exp.getExpires() );

        final Object data = exp.getData();
        if ( data != null )
        {
            obj.add( "data", context.serialize( data ) );
            obj.addProperty( "data_type", data.getClass()
                                              .getName() );
        }

        return obj;
    }

    @Override
    public void register( final GsonBuilder gsonBuilder )
    {
        gsonBuilder.registerTypeAdapter( ExpirationWrapperDoc.class, this );
    }

}
