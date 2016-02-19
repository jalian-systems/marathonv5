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
