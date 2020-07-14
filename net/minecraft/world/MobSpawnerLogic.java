/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.collection.WeightedPicker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.MobSpawnerEntry;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class MobSpawnerLogic {
    private static final Logger LOGGER = LogManager.getLogger();
    private int spawnDelay = 20;
    private final List<MobSpawnerEntry> spawnPotentials = Lists.newArrayList();
    private MobSpawnerEntry spawnEntry = new MobSpawnerEntry();
    private double field_9161;
    private double field_9159;
    private int minSpawnDelay = 200;
    private int maxSpawnDelay = 800;
    private int spawnCount = 4;
    @Nullable
    private Entity renderedEntity;
    private int maxNearbyEntities = 6;
    private int requiredPlayerRange = 16;
    private int spawnRange = 4;

    @Nullable
    private Identifier getEntityId() {
        String string = this.spawnEntry.getEntityTag().getString("id");
        try {
            return ChatUtil.isEmpty(string) ? null : new Identifier(string);
        }
        catch (InvalidIdentifierException lv) {
            BlockPos lv2 = this.getPos();
            LOGGER.warn("Invalid entity id '{}' at spawner {}:[{},{},{}]", (Object)string, (Object)this.getWorld().getRegistryKey().getValue(), (Object)lv2.getX(), (Object)lv2.getY(), (Object)lv2.getZ());
            return null;
        }
    }

    public void setEntityId(EntityType<?> type) {
        this.spawnEntry.getEntityTag().putString("id", Registry.ENTITY_TYPE.getId(type).toString());
    }

    private boolean isPlayerInRange() {
        BlockPos lv = this.getPos();
        return this.getWorld().isPlayerInRange((double)lv.getX() + 0.5, (double)lv.getY() + 0.5, (double)lv.getZ() + 0.5, this.requiredPlayerRange);
    }

    public void update() {
        if (!this.isPlayerInRange()) {
            this.field_9159 = this.field_9161;
            return;
        }
        World lv = this.getWorld();
        BlockPos lv2 = this.getPos();
        if (!(lv instanceof ServerWorld)) {
            double d = (double)lv2.getX() + lv.random.nextDouble();
            double e = (double)lv2.getY() + lv.random.nextDouble();
            double f = (double)lv2.getZ() + lv.random.nextDouble();
            lv.addParticle(ParticleTypes.SMOKE, d, e, f, 0.0, 0.0, 0.0);
            lv.addParticle(ParticleTypes.FLAME, d, e, f, 0.0, 0.0, 0.0);
            if (this.spawnDelay > 0) {
                --this.spawnDelay;
            }
            this.field_9159 = this.field_9161;
            this.field_9161 = (this.field_9161 + (double)(1000.0f / ((float)this.spawnDelay + 200.0f))) % 360.0;
        } else {
            if (this.spawnDelay == -1) {
                this.updateSpawns();
            }
            if (this.spawnDelay > 0) {
                --this.spawnDelay;
                return;
            }
            boolean bl = false;
            for (int i = 0; i < this.spawnCount; ++i) {
                double k;
                CompoundTag lv3 = this.spawnEntry.getEntityTag();
                Optional<EntityType<?>> optional = EntityType.fromTag(lv3);
                if (!optional.isPresent()) {
                    this.updateSpawns();
                    return;
                }
                ListTag lv4 = lv3.getList("Pos", 6);
                int j = lv4.size();
                double g = j >= 1 ? lv4.getDouble(0) : (double)lv2.getX() + (lv.random.nextDouble() - lv.random.nextDouble()) * (double)this.spawnRange + 0.5;
                double h = j >= 2 ? lv4.getDouble(1) : (double)(lv2.getY() + lv.random.nextInt(3) - 1);
                double d = k = j >= 3 ? lv4.getDouble(2) : (double)lv2.getZ() + (lv.random.nextDouble() - lv.random.nextDouble()) * (double)this.spawnRange + 0.5;
                if (!lv.doesNotCollide(optional.get().createSimpleBoundingBox(g, h, k))) continue;
                ServerWorld lv5 = (ServerWorld)lv;
                if (!SpawnRestriction.canSpawn(optional.get(), lv5, SpawnReason.SPAWNER, new BlockPos(g, h, k), lv.getRandom())) continue;
                Entity lv6 = EntityType.loadEntityWithPassengers(lv3, lv, arg -> {
                    arg.refreshPositionAndAngles(g, h, k, arg.yaw, arg.pitch);
                    return arg;
                });
                if (lv6 == null) {
                    this.updateSpawns();
                    return;
                }
                int l = lv.getNonSpectatingEntities(lv6.getClass(), new Box(lv2.getX(), lv2.getY(), lv2.getZ(), lv2.getX() + 1, lv2.getY() + 1, lv2.getZ() + 1).expand(this.spawnRange)).size();
                if (l >= this.maxNearbyEntities) {
                    this.updateSpawns();
                    return;
                }
                lv6.refreshPositionAndAngles(lv6.getX(), lv6.getY(), lv6.getZ(), lv.random.nextFloat() * 360.0f, 0.0f);
                if (lv6 instanceof MobEntity) {
                    MobEntity lv7 = (MobEntity)lv6;
                    if (!lv7.canSpawn(lv, SpawnReason.SPAWNER) || !lv7.canSpawn(lv)) continue;
                    if (this.spawnEntry.getEntityTag().getSize() == 1 && this.spawnEntry.getEntityTag().contains("id", 8)) {
                        ((MobEntity)lv6).initialize(lv5, lv.getLocalDifficulty(lv6.getBlockPos()), SpawnReason.SPAWNER, null, null);
                    }
                }
                this.spawnEntity(lv6);
                lv.syncWorldEvent(2004, lv2, 0);
                if (lv6 instanceof MobEntity) {
                    ((MobEntity)lv6).playSpawnEffects();
                }
                bl = true;
            }
            if (bl) {
                this.updateSpawns();
            }
        }
    }

    private void spawnEntity(Entity arg) {
        if (!this.getWorld().spawnEntity(arg)) {
            return;
        }
        for (Entity lv : arg.getPassengerList()) {
            this.spawnEntity(lv);
        }
    }

    private void updateSpawns() {
        this.spawnDelay = this.maxSpawnDelay <= this.minSpawnDelay ? this.minSpawnDelay : this.minSpawnDelay + this.getWorld().random.nextInt(this.maxSpawnDelay - this.minSpawnDelay);
        if (!this.spawnPotentials.isEmpty()) {
            this.setSpawnEntry(WeightedPicker.getRandom(this.getWorld().random, this.spawnPotentials));
        }
        this.sendStatus(1);
    }

    public void fromTag(CompoundTag tag) {
        this.spawnDelay = tag.getShort("Delay");
        this.spawnPotentials.clear();
        if (tag.contains("SpawnPotentials", 9)) {
            ListTag lv = tag.getList("SpawnPotentials", 10);
            for (int i = 0; i < lv.size(); ++i) {
                this.spawnPotentials.add(new MobSpawnerEntry(lv.getCompound(i)));
            }
        }
        if (tag.contains("SpawnData", 10)) {
            this.setSpawnEntry(new MobSpawnerEntry(1, tag.getCompound("SpawnData")));
        } else if (!this.spawnPotentials.isEmpty()) {
            this.setSpawnEntry(WeightedPicker.getRandom(this.getWorld().random, this.spawnPotentials));
        }
        if (tag.contains("MinSpawnDelay", 99)) {
            this.minSpawnDelay = tag.getShort("MinSpawnDelay");
            this.maxSpawnDelay = tag.getShort("MaxSpawnDelay");
            this.spawnCount = tag.getShort("SpawnCount");
        }
        if (tag.contains("MaxNearbyEntities", 99)) {
            this.maxNearbyEntities = tag.getShort("MaxNearbyEntities");
            this.requiredPlayerRange = tag.getShort("RequiredPlayerRange");
        }
        if (tag.contains("SpawnRange", 99)) {
            this.spawnRange = tag.getShort("SpawnRange");
        }
        if (this.getWorld() != null) {
            this.renderedEntity = null;
        }
    }

    public CompoundTag toTag(CompoundTag tag) {
        Identifier lv = this.getEntityId();
        if (lv == null) {
            return tag;
        }
        tag.putShort("Delay", (short)this.spawnDelay);
        tag.putShort("MinSpawnDelay", (short)this.minSpawnDelay);
        tag.putShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
        tag.putShort("SpawnCount", (short)this.spawnCount);
        tag.putShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
        tag.putShort("RequiredPlayerRange", (short)this.requiredPlayerRange);
        tag.putShort("SpawnRange", (short)this.spawnRange);
        tag.put("SpawnData", this.spawnEntry.getEntityTag().copy());
        ListTag lv2 = new ListTag();
        if (this.spawnPotentials.isEmpty()) {
            lv2.add(this.spawnEntry.serialize());
        } else {
            for (MobSpawnerEntry lv3 : this.spawnPotentials) {
                lv2.add(lv3.serialize());
            }
        }
        tag.put("SpawnPotentials", lv2);
        return tag;
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public Entity getRenderedEntity() {
        if (this.renderedEntity == null) {
            this.renderedEntity = EntityType.loadEntityWithPassengers(this.spawnEntry.getEntityTag(), this.getWorld(), Function.identity());
            if (this.spawnEntry.getEntityTag().getSize() != 1 || !this.spawnEntry.getEntityTag().contains("id", 8) || this.renderedEntity instanceof MobEntity) {
                // empty if block
            }
        }
        return this.renderedEntity;
    }

    public boolean method_8275(int i) {
        if (i == 1 && this.getWorld().isClient) {
            this.spawnDelay = this.minSpawnDelay;
            return true;
        }
        return false;
    }

    public void setSpawnEntry(MobSpawnerEntry spawnEntry) {
        this.spawnEntry = spawnEntry;
    }

    public abstract void sendStatus(int var1);

    public abstract World getWorld();

    public abstract BlockPos getPos();

    @Environment(value=EnvType.CLIENT)
    public double method_8278() {
        return this.field_9161;
    }

    @Environment(value=EnvType.CLIENT)
    public double method_8279() {
        return this.field_9159;
    }
}

