/*******************************************************************************
 * Copyright 2011 John Casey
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.commonjava.shelflife.store.flat;

import javax.inject.Singleton;

import org.commonjava.util.logging.Logger;

@Singleton
public class ChangeSynchronizer
{

    private final Logger logger = new Logger( getClass() );

    private boolean changed = false;

    public synchronized void setChanged()
    {
        changed = true;
        notifyAll();
    }

    public void resetChanged()
    {
        changed = false;
    }

    public synchronized void waitForChange( final long totalMillis, final long pollMillis )
    {
        final long start = System.currentTimeMillis();
        double runningTotal = 0;

        while ( !changed )
        {
            runningTotal = ( System.currentTimeMillis() - start );
            logger.debug( "Waited (%s ms)...", runningTotal );

            if ( runningTotal > ( totalMillis ) )
            {
                logger.debug( "Wait (%s ms) expired.", totalMillis );
                break;
            }

            try
            {
                logger.debug( "Waiting (%s ms) for changes.", pollMillis );
                wait( pollMillis );
            }
            catch ( final InterruptedException e )
            {
                break;
            }
        }

        if ( changed )
        {
            logger.debug( "Setting changed state to false." );
            changed = false;
        }

        logger.debug( "waitFoChange() exiting." );
    }

}
