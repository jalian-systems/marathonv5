package net.sourceforge.marathon.display.readline;

import net.sourceforge.marathon.display.readline.Join.Call;

public abstract class SyncReaction extends Reaction {
    public SyncReaction(int[] indices) {
        super(indices.clone(), false);
    }

    public SyncReaction(Enum<?> head, Enum<?>... channels) {
        super(head, channels, false);
    }

    @Override void dispatch(Join join, final Object[] args) {
        final Call call = (Call) args[0];
        args[0] = call.getMessage();
        call.activate(join, this, args);
    }

    public abstract Object react(Join join, Object[] args);
}
