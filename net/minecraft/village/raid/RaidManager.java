/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 */
package net.minecraft.village.raid;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.effect.StatusEffects;
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
import net.minecraft.village.raid.Raid;
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

    public RaidManager(ServerWorld world) {
        super(RaidManager.nameFor(world.getDimension()));
        this.world = world;
        this.nextAvailableId = 1;
        this.markDirty();
    }

    public Raid getRaid(int id) {
        return this.raids.get(id);
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

    public static boolean isValidRaiderFor(RaiderEntity raider, Raid raid) {
        if (raider != null && raid != null && raid.getWorld() != null) {
            return raider.isAlive() && raider.canJoinRaid() && raider.getDespawnCounter() <= 2400 && raider.world.getDimension() == raid.getWorld().getDimension();
        }
        return false;
    }

    @Nullable
    public Raid startRaid(ServerPlayerEntity player) {
        BlockPos lv7;
        if (player.isSpectator()) {
            return null;
        }
        if (this.world.getGameRules().getBoolean(GameRules.DISABLE_RAIDS)) {
            return null;
        }
        DimensionType lv = player.world.getDimension();
        if (!lv.hasRaids()) {
            return null;
        }
        BlockPos lv2 = player.getBlockPos();
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
        Raid lv8 = this.getOrCreateRaid(player.getServerWorld(), lv7);
        boolean bl = false;
        if (!lv8.hasStarted()) {
            if (!this.raids.containsKey(lv8.getRaidId())) {
                this.raids.put(lv8.getRaidId(), lv8);
            }
            bl = true;
        } else if (lv8.getBadOmenLevel() < lv8.getMaxAcceptableBadOmenLevel()) {
            bl = true;
        } else {
            player.removeStatusEffect(StatusEffects.BAD_OMEN);
            player.networkHandler.sendPacket(new EntityStatusS2CPacket(player, 43));
        }
        if (bl) {
            lv8.start(player);
            player.networkHandler.sendPacket(new EntityStatusS2CPacket(player, 43));
            if (!lv8.hasSpawned()) {
                player.incrementStat(Stats.RAID_TRIGGER);
                Criteria.VOLUNTARY_EXILE.trigger(player);
            }
        }
        this.markDirty();
        return lv8;
    }

    private Raid getOrCreateRaid(ServerWorld world, BlockPos pos) {
        Raid lv = world.getRaidAt(pos);
        return lv != null ? lv : new Raid(this.nextId(), world, pos);
    }

    @Override
    public void fromTag(CompoundTag tag) {
        this.nextAvailableId = tag.getInt("NextAvailableID");
        this.currentTime = tag.getInt("Tick");
        ListTag lv = tag.getList("Raids", 10);
        for (int i = 0; i < lv.size(); ++i) {
            CompoundTag lv2 = lv.getCompound(i);
            Raid lv3 = new Raid(this.world, lv2);
            this.raids.put(lv3.getRaidId(), lv3);
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.putInt("NextAvailableID", this.nextAvailableId);
        tag.putInt("Tick", this.currentTime);
        ListTag lv = new ListTag();
        for (Raid lv2 : this.raids.values()) {
            CompoundTag lv3 = new CompoundTag();
            lv2.toTag(lv3);
            lv.add(lv3);
        }
        tag.put("Raids", lv);
        return tag;
    }

    public static String nameFor(DimensionType arg) {
        return "raids" + arg.getSuffix();
    }

    private int nextId() {
        return ++this.nextAvailableId;
    }

    @Nullable
    public Raid getRaidAt(BlockPos pos, int i) {
        Raid lv = null;
        double d = i;
        for (Raid lv2 : this.raids.values()) {
            double e = lv2.getCenter().getSquaredDistance(pos);
            if (!lv2.isActive() || !(e < d)) continue;
            lv = lv2;
            d = e;
        }
        return lv;
    }
}

