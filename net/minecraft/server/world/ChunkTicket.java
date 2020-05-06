/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.server.world;

import java.util.Objects;
import net.minecraft.server.world.ChunkTicketType;

public final class ChunkTicket<T>
implements Comparable<ChunkTicket<?>> {
    private final ChunkTicketType<T> type;
    private final int level;
    private final T argument;
    private long tickCreated;

    protected ChunkTicket(ChunkTicketType<T> arg, int i, T object) {
        this.type = arg;
        this.level = i;
        this.argument = object;
    }

    @Override
    public int compareTo(ChunkTicket<?> arg) {
        int i = Integer.compare(this.level, arg.level);
        if (i != 0) {
            return i;
        }
        int j = Integer.compare(System.identityHashCode(this.type), System.identityHashCode(arg.type));
        if (j != 0) {
            return j;
        }
        return this.type.getArgumentComparator().compare(this.argument, arg.argument);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof ChunkTicket)) {
            return false;
        }
        ChunkTicket lv = (ChunkTicket)object;
        return this.level == lv.level && Objects.equals(this.type, lv.type) && Objects.equals(this.argument, lv.argument);
    }

    public int hashCode() {
        return Objects.hash(this.type, this.level, this.argument);
    }

    public String toString() {
        return "Ticket[" + this.type + " " + this.level + " (" + this.argument + ")] at " + this.tickCreated;
    }

    public ChunkTicketType<T> getType() {
        return this.type;
    }

    public int getLevel() {
        return this.level;
    }

    protected void setTickCreated(long l) {
        this.tickCreated = l;
    }

    protected boolean isExpired(long l) {
        long m = this.type.getExpiryTicks();
        return m != 0L && l - this.tickCreated > m;
    }

    @Override
    public /* synthetic */ int compareTo(Object object) {
        return this.compareTo((ChunkTicket)object);
    }
}

