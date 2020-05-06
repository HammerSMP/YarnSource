/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 */
package net.minecraft.world.poi;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Map;
import java.util.Objects;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.DynamicSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.poi.PointOfInterestType;

public class PointOfInterest
implements DynamicSerializable {
    private final BlockPos pos;
    private final PointOfInterestType type;
    private int freeTickets;
    private final Runnable updateListener;

    private PointOfInterest(BlockPos arg, PointOfInterestType arg2, int i, Runnable runnable) {
        this.pos = arg.toImmutable();
        this.type = arg2;
        this.freeTickets = i;
        this.updateListener = runnable;
    }

    public PointOfInterest(BlockPos arg, PointOfInterestType arg2, Runnable runnable) {
        this(arg, arg2, arg2.getTicketCount(), runnable);
    }

    public <T> PointOfInterest(Dynamic<T> dynamic, Runnable runnable) {
        this(dynamic.get("pos").map(BlockPos::deserialize).orElse(new BlockPos(0, 0, 0)), Registry.POINT_OF_INTEREST_TYPE.get(new Identifier(dynamic.get("type").asString(""))), dynamic.get("free_tickets").asInt(0), runnable);
    }

    @Override
    public <T> T serialize(DynamicOps<T> dynamicOps) {
        return (T)dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("pos"), this.pos.serialize(dynamicOps), (Object)dynamicOps.createString("type"), (Object)dynamicOps.createString(Registry.POINT_OF_INTEREST_TYPE.getId(this.type).toString()), (Object)dynamicOps.createString("free_tickets"), (Object)dynamicOps.createInt(this.freeTickets)));
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

