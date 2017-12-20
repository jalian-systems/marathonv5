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

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

public class Spec {

    public static final Logger LOGGER = Logger.getLogger(Spec.class.getName());

    private ArrayList<ArrayList<Reaction>> reactionsPerChannel = new ArrayList<ArrayList<Reaction>>();
    private long asyncMask = 0;
    private long mask = 0;
    private volatile Reaction[][] cachedReactionsPerChannel = null;

    public Spec() {
    }

    public void addReaction(Reaction reaction) {
        if ((mask & ~asyncMask & reaction.asyncMask) != 0) {
            throw new IllegalArgumentException("Cannot use a synchronous channel in a non-head position");
        }
        if ((reaction.mask & ~reaction.asyncMask & asyncMask) != 0) {
            throw new IllegalArgumentException("Cannot use an asynchronous channel in the head position of a synchronous reaction");
        }
        cachedReactionsPerChannel = null;
        final int[] indices = reaction.indices;
        for (final int index : indices) {
            if (reactionsPerChannel.size() <= index) {
                reactionsPerChannel.ensureCapacity(index + 1);
                while (reactionsPerChannel.size() <= index) {
                    reactionsPerChannel.add(null);
                }
            }
            ArrayList<Reaction> reactions = reactionsPerChannel.get(index);
            if (reactions == null) {
                reactions = new ArrayList<Reaction>();
                reactionsPerChannel.set(index, reactions);
            }
            reactions.add(reaction);
        }
        asyncMask |= reaction.asyncMask;
        mask |= reaction.mask;
    }

    public Join createJoin() {
        return createJoin(Join.TRIVIAL_EXECUTOR);
    }

    private static final Reaction[] EMPTY_REACTIONS = new Reaction[0];

    public Join createJoin(final Executor executor) {
        if (cachedReactionsPerChannel == null) {
            final int length = reactionsPerChannel.size();
            final Reaction[][] localReactionsPerChannel = new Reaction[length][];
            for (int i = 0; i < length; ++i) {
                final ArrayList<Reaction> reactions = reactionsPerChannel.get(i);
                if (reactions != null) {
                    localReactionsPerChannel[i] = reactions.toArray(EMPTY_REACTIONS);
                }
            }
            cachedReactionsPerChannel = localReactionsPerChannel;
        }
        return new Join(asyncMask, cachedReactionsPerChannel, executor);
    }
}
