/*******************************************************************************
 * /*******************************************************************************
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
 * The <code>JSONString</code> interface allows a <code>toJSONString()</code>
 * method so that a class can change the behavior of
 * <code>JSONObject.toString()</code>, <code>JSONArray.toString()</code>, and
 * <code>JSONWriter.value(</code>Object<code>)</code>. The
 * <code>toJSONString</code> method will be used instead of the default behavior
 * of using the Object's <code>toString()</code> method and quoting the result.
 */
public interface JSONString {
    /**
     * The <code>toJSONString</code> method allows a class to produce its own
     * JSON serialization.
     * 
     * @return A strictly syntactically correct JSON text.
     */
    public String toJSONString();
}
