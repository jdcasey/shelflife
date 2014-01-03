/*******************************************************************************
 * Copyright (C) 2014 John Casey.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.commonjava.shelflife;

import java.text.MessageFormat;
import java.util.IllegalFormatException;
import java.util.List;

public class ExpirationManagerException
    extends Exception
{
    private static final long serialVersionUID = 1L;

    private List<? extends Throwable> nested;

    private final Object[] params;

    public ExpirationManagerException( final String message, final Throwable error, final Object... params )
    {
        super( message, error );
        this.params = params;
    }

    public ExpirationManagerException( final String message, final Object... params )
    {
        super( message );
        this.params = params;
    }

    @Override
    public String getMessage()
    {
        final StringBuilder msg = new StringBuilder( formatMessage() );
        if ( nested != null && !nested.isEmpty() )
        {
            msg.append( "\nNested errors:\n" );

            int idx = 1;
            for ( final Throwable error : nested )
            {
                msg.append( "\n" )
                   .append( idx )
                   .append( ".  " )
                   .append( error.getMessage() );
                idx++;
            }
        }

        return msg.toString();
    }

    @Override
    public String getLocalizedMessage()
    {
        return getMessage();
    }

    public String formatMessage()
    {
        String message = super.getMessage();

        if ( params != null )
        {
            try
            {
                message = String.format( message, params );
            }
            catch ( final IllegalFormatException ife )
            {
                try
                {
                    message = MessageFormat.format( message, params );
                }
                catch ( final IllegalArgumentException iae )
                {
                }
            }
        }

        return message;
    }

    public ExpirationManagerException withNestedErrors( final List<? extends Throwable> errors )
    {
        this.nested = errors;
        return this;
    }
}
