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
