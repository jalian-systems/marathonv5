/*******************************************************************************
 *  *  
 *  *  Copyright (C) 2015 Jalian Systems Private Ltd.
 *  *  Copyright (C) 2015 Contributors to Marathon OSS Project
 *  * 
 *  *  This library is free software; you can redistribute it and/or
 *  *  modify it under the terms of the GNU Library General Public
 *  *  License as published by the Free Software Foundation; either
 *  *  version 2 of the License, or (at your option) any later version.
 *  * 
 *  *  This library is distributed in the hope that it will be useful,
 *  *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  *  Library General Public License for more details.
 *  * 
 *  *  You should have received a copy of the GNU Library General Public
 *  *  License along with this library; if not, write to the Free Software
 *  *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *  * 
 *  *  Project website: http://www.marathontesting.com
 *  *  Help: Marathon help forum @ http://groups.google.com/group/marathon-testing
 *  * 
 *  *******************************************************************************/
package org.json;

/**
 * The JSONException is thrown by the JSON.org classes when things are amiss.
 * 
 * @author JSON.org
 * @version 2013-02-10
 */
public class JSONException extends RuntimeException {
    private static final long serialVersionUID = 0;
    private Throwable cause;

    /**
     * Constructs a JSONException with an explanatory message.
     * 
     * @param message
     *            Detail about the reason for the exception.
     */
    public JSONException(String message) {
        super(message);
    }

    /**
     * Constructs a new JSONException with the specified cause.
     */
    public JSONException(Throwable cause) {
        super(cause.getMessage());
        this.cause = cause;
    }

    /**
     * Returns the cause of this exception or null if the cause is nonexistent
     * or unknown.
     * 
     * @returns the cause of this exception or null if the cause is nonexistent
     *          or unknown.
     */
    public Throwable getCause() {
        return this.cause;
    }
}
