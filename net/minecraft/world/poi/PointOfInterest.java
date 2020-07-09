/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.world.poi;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.poi.PointOfInterestType;

public class PointOfInterest {
    private final BlockPos pos;
    private final PointOfInterestType type;
    private int freeTickets;
    private final Runnable updateListener;

    public static Codec<PointOfInterest> method_28359(Runnable runnable) {
        return RecordCodecBuilder.create(instance -> instance.group((App)BlockPos.field_25064.fieldOf("pos").forGetter(arg -> arg.pos), (App)Registry.POINT_OF_INTEREST_TYPE.fieldOf("type").forGetter(arg -> arg.type), (App)Codec.INT.fieldOf("free_tickets").orElse((Object)0).forGetter(arg -> arg.freeTickets), (App)RecordCodecBuilder.point((Object)runnable)).apply((Applicative)instance, PointOfInterest::new));
    }

    private PointOfInterest(BlockPos arg, PointOfInterestType arg2, int i, Runnable runnable) {
        this.pos = arg.toImmutable();
        this.type = arg2;
        this.freeTickets = i;
        this.updateListener = runnable;
    }

    public PointOfInterest(BlockPos arg, PointOfInterestType arg2, Runnable runnable) {
        this(arg, arg2, arg2.getTicketCount(), runnable);
    }

    protected boolean reserveTicket() {
        if (this.freeTickets <= 0) {
            return false;
        }
        --this.freeTickets;
        this.updateListener.run();
        return true;
    }

    protected boolean releaseTicket() {
        if (this.freeTickets >= this.type.getTicketCount()) {
            return false;
        }
        ++this.freeTickets;
        this.updateListener.run();
        return true;
    }

    public boolean hasSpace() {
        return this.freeTickets > 0;
    }

    public boolean isOccupied() {
        return this.freeTickets != this.type.getTicketCount();
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public PointOfInterestType getType() {
        return this.type;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        return Objects.equals(this.pos, ((PointOfInterest)object).pos);
    }

    public int hashCode() {
        return this.pos.hashCode();
    }
}

