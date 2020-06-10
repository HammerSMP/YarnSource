/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.raid;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.raid.Raid;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.PersistentState;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;

public class RaidManager
extends PersistentState {
    private final Map<Integer, Raid> raids = Maps.newHashMap();
    private final ServerWorld world;
    private int nextAvailableId;
    private int currentTime;

    public RaidManager(ServerWorld arg) {
        super(RaidManager.nameFor(arg.getDimension()));
        this.world = arg;
        this.nextAvailableId = 1;
        this.markDirty();
    }

    public Raid getRaid(int i) {
        return this.raids.get(i);
    }

    public void tick() {
        ++this.currentTime;
        Iterator<Raid> iterator = this.raids.values().iterator();
        while (iterator.hasNext()) {
            Raid lv = iterator.next();
            if (this.world.getGameRules().getBoolean(GameRules.DISABLE_RAIDS)) {
                lv.invalidate();
            }
            if (lv.hasStopped()) {
                iterator.remove();
                this.markDirty();
                continue;
            }
            lv.tick();
        }
        if (this.currentTime % 200 == 0) {
            this.markDirty();
        }
        DebugInfoSender.sendRaids(this.world, this.raids.values());
    }

    public static boolean isValidRaiderFor(RaiderEntity arg, Raid arg2) {
        if (arg != null && arg2 != null && arg2.getWorld() != null) {
            return arg.isAlive() && arg.canJoinRaid() && arg.getDespawnCounter() <= 2400 && arg.world.getDimension() == arg2.getWorld().getDimension();
        }
        return false;
    }

    @Nullable
    public Raid startRaid(ServerPlayerEntity arg) {
        BlockPos lv7;
        if (arg.isSpectator()) {
            return null;
        }
        if (this.world.getGameRules().getBoolean(GameRules.DISABLE_RAIDS)) {
            return null;
        }
        DimensionType lv = arg.world.getDimension();
        if (!lv.method_29958()) {
            return null;
        }
        BlockPos lv2 = arg.getBlockPos();
        List list = this.world.getPointOfInterestStorage().getInCircle(PointOfInterestType.ALWAYS_TRUE, lv2, 64, PointOfInterestStorage.OccupationStatus.IS_OCCUPIED).collect(Collectors.toList());
        int i = 0;
        Vec3d lv3 = Vec3d.ZERO;
        for (PointOfInterest lv4 : list) {
            BlockPos lv5 = lv4.getPos();
            lv3 = lv3.add(lv5.getX(), lv5.getY(), lv5.getZ());
            ++i;
        }
        if (i > 0) {
            lv3 = lv3.multiply(1.0 / (double)i);
            BlockPos lv6 = new BlockPos(lv3);
        } else {
            lv7 = lv2;
        }
        Raid lv8 = this.getOrCreateRaid(arg.getServerWorld(), lv7);
        boolean bl = false;
        if (!lv8.hasStarted()) {
            if (!this.raids.containsKey(lv8.getRaidId())) {
                this.raids.put(lv8.getRaidId(), lv8);
            }
            bl = true;
        } else if (lv8.getBadOmenLevel() < lv8.getMaxAcceptableBadOmenLevel()) {
            bl = true;
        } else {
            arg.removeStatusEffect(StatusEffects.BAD_OMEN);
            arg.networkHandler.sendPacket(new EntityStatusS2CPacket(arg, 43));
        }
        if (bl) {
            lv8.start(arg);
            arg.networkHandler.sendPacket(new EntityStatusS2CPacket(arg, 43));
            if (!lv8.hasSpawned()) {
                arg.incrementStat(Stats.RAID_TRIGGER);
                Criteria.VOLUNTARY_EXILE.trigger(arg);
            }
        }
        this.markDirty();
        return lv8;
    }

    private Raid getOrCreateRaid(ServerWorld arg, BlockPos arg2) {
        Raid lv = arg.getRaidAt(arg2);
        return lv != null ? lv : new Raid(this.nextId(), arg, arg2);
    }

    @Override
    public void fromTag(CompoundTag arg) {
        this.nextAvailableId = arg.getInt("NextAvailableID");
        this.currentTime = arg.getInt("Tick");
        ListTag lv = arg.getList("Raids", 10);
        for (int i = 0; i < lv.size(); ++i) {
            CompoundTag lv2 = lv.getCompound(i);
            Raid lv3 = new Raid(this.world, lv2);
            this.raids.put(lv3.getRaidId(), lv3);
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag arg) {
        arg.putInt("NextAvailableID", this.nextAvailableId);
        arg.putInt("Tick", this.currentTime);
        ListTag lv = new ListTag();
        for (Raid lv2 : this.raids.values()) {
            CompoundTag lv3 = new CompoundTag();
            lv2.toTag(lv3);
            lv.add(lv3);
        }
        arg.put("Raids", lv);
        return arg;
    }

    public static String nameFor(DimensionType arg) {
        return "raids" + arg.getSuffix();
    }

    private int nextId() {
        return ++this.nextAvailableId;
    }

    @Nullable
    public Raid getRaidAt(BlockPos arg, int i) {
        Raid lv = null;
        double d = i;
        for (Raid lv2 : this.raids.values()) {
            double e = lv2.getCenter().getSquaredDistance(arg);
            if (!lv2.isActive() || !(e < d)) continue;
            lv = lv2;
            d = e;
        }
        return lv;
    }
}

