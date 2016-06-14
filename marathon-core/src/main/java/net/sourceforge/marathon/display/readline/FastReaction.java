package net.sourceforge.marathon.display.readline;

public abstract class FastReaction extends Reaction {
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
