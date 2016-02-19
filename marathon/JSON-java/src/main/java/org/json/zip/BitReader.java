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
package org.json.zip;

import java.io.IOException;

public interface BitReader {
    /**
     * Read one bit.
     * 
     * @return true if it is a 1 bit.
     */
    public boolean bit() throws IOException;

    /**
     * Returns the number of bits that have been read from this bitreader.
     * 
     * @return The number of bits read so far.
     */
    public long nrBits();

    /**
     * Check that the rest of the block has been padded with zeroes.
     * 
     * @param factor
     *            The size in bits of the block to pad. This will typically be
     *            8, 16, 32, 64, 128, 256, etc.
     * @return true if the block was zero padded, or false if the the padding
     *         contained any one bits.
     * @throws IOException
     */
    public boolean pad(int factor) throws IOException;

    /**
     * Read some bits.
     * 
     * @param width
     *            The number of bits to read. (0..32)
     * @throws IOException
     * @return the bits
     */
    public int read(int width) throws IOException;
}
