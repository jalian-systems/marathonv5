/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
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
package net.sourceforge.marathon.display.readline;

import java.util.logging.Logger;

public abstract class FastReaction extends Reaction {

    public static final Logger LOGGER = Logger.getLogger(FastReaction.class.getName());

    public FastReaction(int[] indices) {
        super(indices.clone(), true);
    }

    public FastReaction(Enum<?> head, Enum<?>... channels) {
        super(head, channels, true);
    }

    @Override void dispatch(final Join join, final Object[] args) {
        try {
            react(join, args);
        } catch (Exception e) {
        }
    }

    public abstract void react(Join join, Object[] args);
}
