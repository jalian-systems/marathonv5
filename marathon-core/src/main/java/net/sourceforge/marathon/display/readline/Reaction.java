/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package net.sourceforge.marathon.display.readline;

public abstract class Reaction {
    final int[] indices;
    final long mask;
    final long asyncMask;

    private static int[] toIndices(Enum<?> head, Enum<?>[] channels) {
        final int[] indices = new int[channels.length + 1];
        indices[0] = head.ordinal();
        for (int i = 0; i < channels.length; ++i) {
            indices[i + 1] = channels[i].ordinal();
        }
        return indices;
    }

    Reaction(Enum<?> head, Enum<?>[] channels, boolean isAsync) {
        this(toIndices(head, channels), isAsync);
    }

    Reaction(int[] indices, boolean isAsync) {
        long mask = 0;
        for (int i = 0; i < indices.length; ++i) {
            final int index = indices[i];
            if (index < 0 || index > 63) {
                throw new IndexOutOfBoundsException();
            }
            if ((mask & (1L << index)) != 0) {
                throw new IllegalArgumentException("Duplicate channels in reaction");
            }
            mask |= 1L << index;
        }
        this.indices = indices;
        this.mask = mask;
        if (isAsync) {
            this.asyncMask = mask;
        } else {
            this.asyncMask = mask & ~(1L << indices[0]);
        }
    }

    abstract void dispatch(Join join, Object[] args);
}
