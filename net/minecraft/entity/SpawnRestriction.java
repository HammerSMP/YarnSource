/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 */
package net.minecraft.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.HuskEntity;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PatrolEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.mob.StrayEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;

public class SpawnRestriction {
    private static final Map<EntityType<?>, Entry> RESTRICTIONS = Maps.newHashMap();

    private static <T extends MobEntity> void register(EntityType<T> arg, Location arg2, Heightmap.Type arg3, SpawnPredicate<T> arg4) {
        Entry lv = RESTRICTIONS.put(arg, new Entry(arg3, arg2, arg4));
        if (lv != null) {
            throw new IllegalStateException("Duplicate registration for type " + Registry.ENTITY_TYPE.getId(arg));
        }
    }

    public static Location getLocation(EntityType<?> arg) {
        Entry lv = RESTRICTIONS.get(arg);
        return lv == null ? Location.NO_RESTRICTIONS : lv.location;
    }

    public static Heightmap.Type getHeightmapType(@Nullable EntityType<?> arg) {
        Entry lv = RESTRICTIONS.get(arg);
        return lv == null ? Heightmap.Type.MOTION_BLOCKING_NO_LEAVES : lv.heightmapType;
    }

    public static <T extends Entity> boolean canSpawn(EntityType<T> arg, IWorld arg2, SpawnType arg3, BlockPos arg4, Random random) {
        Entry lv = RESTRICTIONS.get(arg);
        return lv == null || lv.predicate.test(arg, arg2, arg3, arg4, random);
    }

    static {
        SpawnRestriction.register(EntityType.COD, Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, FishEntity::canSpawn);
        SpawnRestriction.register(EntityType.DOLPHIN, Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, DolphinEntity::canSpawn);
        SpawnRestriction.register(EntityType.DROWNED, Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, DrownedEntity::canSpawn);
        SpawnRestriction.register(EntityType.GUARDIAN, Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, GuardianEntity::canSpawn);
        SpawnRestriction.register(EntityType.PUFFERFISH, Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, FishEntity::canSpawn);
        SpawnRestriction.register(EntityType.SALMON, Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, FishEntity::canSpawn);
        SpawnRestriction.register(EntityType.SQUID, Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, SquidEntity::canSpawn);
        SpawnRestriction.register(EntityType.TROPICAL_FISH, Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, FishEntity::canSpawn);
        SpawnRestriction.register(EntityType.BAT, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, BatEntity::canSpawn);
        SpawnRestriction.register(EntityType.BLAZE, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnIgnoreLightLevel);
        SpawnRestriction.register(EntityType.CAVE_SPIDER, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
        SpawnRestriction.register(EntityType.CHICKEN, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
        SpawnRestriction.register(EntityType.COW, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
        SpawnRestriction.register(EntityType.CREEPER, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
        SpawnRestriction.register(EntityType.DONKEY, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
        SpawnRestriction.register(EntityType.ENDERMAN, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
        SpawnRestriction.register(EntityType.ENDERMITE, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndermiteEntity::canSpawn);
        SpawnRestriction.register(EntityType.ENDER_DRAGON, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::canMobSpawn);
        SpawnRestriction.register(EntityType.GHAST, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, GhastEntity::canSpawn);
        SpawnRestriction.register(EntityType.GIANT, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
        SpawnRestriction.register(EntityType.HORSE, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
        SpawnRestriction.register(EntityType.HUSK, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HuskEntity::canSpawn);
        SpawnRestriction.register(EntityType.IRON_GOLEM, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::canMobSpawn);
        SpawnRestriction.register(EntityType.LLAMA, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
        SpawnRestriction.register(EntityType.MAGMA_CUBE, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MagmaCubeEntity::canMagmaCubeSpawn);
        SpawnRestriction.register(EntityType.MOOSHROOM, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MooshroomEntity::canSpawn);
        SpawnRestriction.register(EntityType.MULE, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
        SpawnRestriction.register(EntityType.OCELOT, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING, OcelotEntity::canSpawn);
        SpawnRestriction.register(EntityType.PARROT, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING, ParrotEntity::canSpawn);
        SpawnRestriction.register(EntityType.PIG, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
        SpawnRestriction.register(EntityType.HOGLIN, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HoglinEntity::canSpawn);
        SpawnRestriction.register(EntityType.PIGLIN, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, PiglinEntity::canSpawn);
        SpawnRestriction.register(EntityType.PILLAGER, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, PatrolEntity::canSpawn);
        SpawnRestriction.register(EntityType.POLAR_BEAR, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, PolarBearEntity::canSpawn);
        SpawnRestriction.register(EntityType.RABBIT, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, RabbitEntity::canSpawn);
        SpawnRestriction.register(EntityType.SHEEP, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
        SpawnRestriction.register(EntityType.SILVERFISH, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, SilverfishEntity::canSpawn);
        SpawnRestriction.register(EntityType.SKELETON, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
        SpawnRestriction.register(EntityType.SKELETON_HORSE, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
        SpawnRestriction.register(EntityType.SLIME, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, SlimeEntity::canSpawn);
        SpawnRestriction.register(EntityType.SNOW_GOLEM, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::canMobSpawn);
        SpawnRestriction.register(EntityType.SPIDER, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
        SpawnRestriction.register(EntityType.STRAY, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, StrayEntity::canSpawn);
        SpawnRestriction.register(EntityType.STRIDER, Location.IN_LAVA, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, StriderEntity::canSpawn);
        SpawnRestriction.register(EntityType.TURTLE, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, TurtleEntity::canSpawn);
        SpawnRestriction.register(EntityType.VILLAGER, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::canMobSpawn);
        SpawnRestriction.register(EntityType.WITCH, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
        SpawnRestriction.register(EntityType.WITHER, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
        SpawnRestriction.register(EntityType.WITHER_SKELETON, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
        SpawnRestriction.register(EntityType.WOLF, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
        SpawnRestriction.register(EntityType.ZOMBIE, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
        SpawnRestriction.register(EntityType.ZOMBIE_HORSE, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
        SpawnRestriction.register(EntityType.ZOMBIFIED_PIGLIN, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ZombifiedPiglinEntity::canSpawn);
        SpawnRestriction.register(EntityType.ZOMBIE_VILLAGER, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
        SpawnRestriction.register(EntityType.CAT, Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
        SpawnRestriction.register(EntityType.ELDER_GUARDIAN, Location.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, GuardianEntity::canSpawn);
        SpawnRestriction.register(EntityType.EVOKER, Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
        SpawnRestriction.register(EntityType.FOX, Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
        SpawnRestriction.register(EntityType.ILLUSIONER, Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
        SpawnRestriction.register(EntityType.PANDA, Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
        SpawnRestriction.register(EntityType.PHANTOM, Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::canMobSpawn);
        SpawnRestriction.register(EntityType.RAVAGER, Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
        SpawnRestriction.register(EntityType.SHULKER, Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::canMobSpawn);
        SpawnRestriction.register(EntityType.TRADER_LLAMA, Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::isValidNaturalSpawn);
        SpawnRestriction.register(EntityType.VEX, Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
        SpawnRestriction.register(EntityType.VINDICATOR, Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HostileEntity::canSpawnInDark);
        SpawnRestriction.register(EntityType.WANDERING_TRADER, Location.NO_RESTRICTIONS, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::canMobSpawn);
    }

    public static enum Location {
        ON_GROUND,
        IN_WATER,
        NO_RESTRICTIONS,
        IN_LAVA;

    }

    static class Entry {
        private final Heightmap.Type heightmapType;
        private final Location location;
        private final SpawnPredicate<?> predicate;

        public Entry(Heightmap.Type arg, Location arg2, SpawnPredicate<?> arg3) {
            this.heightmapType = arg;
            this.location = arg2;
            this.predicate = arg3;
        }
    }

    @FunctionalInterface
    public static interface SpawnPredicate<T extends Entity> {
        public boolean test(EntityType<T> var1, IWorld var2, SpawnType var3, BlockPos var4, Random var5);
    }
}

