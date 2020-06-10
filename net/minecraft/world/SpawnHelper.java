/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMaps
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.collection.WeightedPicker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.GravityField;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.DirectBiomeAccessType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.StructureFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class SpawnHelper {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int CHUNK_AREA = (int)Math.pow(17.0, 2.0);
    private static final SpawnGroup[] SPAWNABLE_GROUPS = (SpawnGroup[])Stream.of(SpawnGroup.values()).filter(arg -> arg != SpawnGroup.MISC).toArray(SpawnGroup[]::new);

    public static Info setupSpawn(int i, Iterable<Entity> iterable, ChunkSource arg) {
        GravityField lv = new GravityField();
        Object2IntOpenHashMap object2IntOpenHashMap = new Object2IntOpenHashMap();
        for (Entity lv2 : iterable) {
            SpawnGroup lv4;
            MobEntity lv3;
            if (lv2 instanceof MobEntity && ((lv3 = (MobEntity)lv2).isPersistent() || lv3.cannotDespawn()) || (lv4 = lv2.getType().getSpawnGroup()) == SpawnGroup.MISC) continue;
            BlockPos lv5 = lv2.getBlockPos();
            long l = ChunkPos.toLong(lv5.getX() >> 4, lv5.getZ() >> 4);
            arg.query(l, arg5 -> {
                Biome lv = SpawnHelper.getBiomeDirectly(lv5, arg5);
                Biome.SpawnDensity lv2 = lv.getSpawnDensity(lv2.getType());
                if (lv2 != null) {
                    lv.addPoint(lv2.getBlockPos(), lv2.getMass());
                }
                object2IntOpenHashMap.addTo((Object)lv4, 1);
            });
        }
        return new Info(i, object2IntOpenHashMap, lv);
    }

    private static Biome getBiomeDirectly(BlockPos arg, Chunk arg2) {
        return DirectBiomeAccessType.INSTANCE.getBiome(0L, arg.getX(), arg.getY(), arg.getZ(), arg2.getBiomeArray());
    }

    public static void spawn(ServerWorld arg, WorldChunk arg22, Info arg32, boolean bl, boolean bl2, boolean bl3) {
        arg.getProfiler().push("spawner");
        for (SpawnGroup lv : SPAWNABLE_GROUPS) {
            if (!bl && lv.isPeaceful() || !bl2 && !lv.isPeaceful() || !bl3 && lv.isAnimal() || !arg32.isBelowCap(lv)) continue;
            SpawnHelper.spawnEntitiesInChunk(lv, arg, arg22, (arg2, arg3, arg4) -> arg32.test(arg2, arg3, arg4), (arg2, arg3) -> arg32.run(arg2, arg3));
        }
        arg.getProfiler().pop();
    }

    public static void spawnEntitiesInChunk(SpawnGroup arg, ServerWorld arg2, WorldChunk arg3, Checker arg4, Runner arg5) {
        BlockPos lv = SpawnHelper.getSpawnPos(arg2, arg3);
        if (lv.getY() < 1) {
            return;
        }
        SpawnHelper.spawnEntitiesInChunk(arg, arg2, arg3, lv, arg4, arg5);
    }

    public static void spawnEntitiesInChunk(SpawnGroup arg, ServerWorld arg2, Chunk arg3, BlockPos arg4, Checker arg5, Runner arg6) {
        StructureAccessor lv = arg2.getStructureAccessor();
        ChunkGenerator lv2 = arg2.getChunkManager().getChunkGenerator();
        int i = arg4.getY();
        BlockState lv3 = arg3.getBlockState(arg4);
        if (lv3.isSolidBlock(arg3, arg4)) {
            return;
        }
        BlockPos.Mutable lv4 = new BlockPos.Mutable();
        int j = 0;
        block0: for (int k = 0; k < 3; ++k) {
            int l = arg4.getX();
            int m = arg4.getZ();
            int n = 6;
            Biome.SpawnEntry lv5 = null;
            EntityData lv6 = null;
            int o = MathHelper.ceil(arg2.random.nextFloat() * 4.0f);
            int p = 0;
            for (int q = 0; q < o; ++q) {
                double f;
                lv4.set(l += arg2.random.nextInt(6) - arg2.random.nextInt(6), i, m += arg2.random.nextInt(6) - arg2.random.nextInt(6));
                double d = (double)l + 0.5;
                double e = (double)m + 0.5;
                PlayerEntity lv7 = arg2.getClosestPlayer(d, (double)i, e, -1.0, false);
                if (lv7 == null || !SpawnHelper.isAcceptableSpawnPosition(arg2, arg3, lv4, f = lv7.squaredDistanceTo(d, i, e))) continue;
                if (lv5 == null) {
                    lv5 = SpawnHelper.pickRandomSpawnEntry(arg2, lv, lv2, arg, arg2.random, lv4);
                    if (lv5 == null) continue block0;
                    o = lv5.minGroupSize + arg2.random.nextInt(1 + lv5.maxGroupSize - lv5.minGroupSize);
                }
                if (!SpawnHelper.canSpawn(arg2, arg, lv, lv2, lv5, lv4, f) || !arg5.test(lv5.type, lv4, arg3)) continue;
                MobEntity lv8 = SpawnHelper.createMob(arg2, lv5.type);
                if (lv8 == null) {
                    return;
                }
                lv8.refreshPositionAndAngles(d, i, e, arg2.random.nextFloat() * 360.0f, 0.0f);
                if (!SpawnHelper.isValidSpawn(arg2, lv8, f)) continue;
                lv6 = lv8.initialize(arg2, arg2.getLocalDifficulty(lv8.getBlockPos()), SpawnReason.NATURAL, lv6, null);
                ++p;
                arg2.spawnEntity(lv8);
                arg6.run(lv8, arg3);
                if (++j >= lv8.getLimitPerChunk()) {
                    return;
                }
                if (lv8.spawnsTooManyForEachTry(p)) continue block0;
            }
        }
    }

    private static boolean isAcceptableSpawnPosition(ServerWorld arg, Chunk arg2, BlockPos.Mutable arg3, double d) {
        if (d <= 576.0) {
            return false;
        }
        if (arg.getSpawnPos().isWithinDistance(new Vec3d((double)arg3.getX() + 0.5, arg3.getY(), (double)arg3.getZ() + 0.5), 24.0)) {
            return false;
        }
        ChunkPos lv = new ChunkPos(arg3);
        return Objects.equals(lv, arg2.getPos()) || arg.getChunkManager().shouldTickChunk(lv);
    }

    private static boolean canSpawn(ServerWorld arg, SpawnGroup arg2, StructureAccessor arg3, ChunkGenerator arg4, Biome.SpawnEntry arg5, BlockPos.Mutable arg6, double d) {
        EntityType<?> lv = arg5.type;
        if (lv.getSpawnGroup() == SpawnGroup.MISC) {
            return false;
        }
        if (!lv.isSpawnableFarFromPlayer() && d > (double)(lv.getSpawnGroup().getImmediateDespawnRange() * lv.getSpawnGroup().getImmediateDespawnRange())) {
            return false;
        }
        if (!lv.isSummonable() || !SpawnHelper.containsSpawnEntry(arg, arg3, arg4, arg2, arg5, arg6)) {
            return false;
        }
        SpawnRestriction.Location lv2 = SpawnRestriction.getLocation(lv);
        if (!SpawnHelper.canSpawn(lv2, arg, arg6, lv)) {
            return false;
        }
        if (!SpawnRestriction.canSpawn(lv, arg, SpawnReason.NATURAL, arg6, arg.random)) {
            return false;
        }
        return arg.doesNotCollide(lv.createSimpleBoundingBox((double)arg6.getX() + 0.5, arg6.getY(), (double)arg6.getZ() + 0.5));
    }

    /*
     * WARNING - void declaration
     */
    @Nullable
    private static MobEntity createMob(ServerWorld arg, EntityType<?> arg2) {
        void lv3;
        try {
            Object lv = arg2.create(arg);
            if (!(lv instanceof MobEntity)) {
                throw new IllegalStateException("Trying to spawn a non-mob: " + Registry.ENTITY_TYPE.getId(arg2));
            }
            MobEntity lv2 = (MobEntity)lv;
        }
        catch (Exception exception) {
            LOGGER.warn("Failed to create mob", (Throwable)exception);
            return null;
        }
        return lv3;
    }

    private static boolean isValidSpawn(ServerWorld arg, MobEntity arg2, double d) {
        if (d > (double)(arg2.getType().getSpawnGroup().getImmediateDespawnRange() * arg2.getType().getSpawnGroup().getImmediateDespawnRange()) && arg2.canImmediatelyDespawn(d)) {
            return false;
        }
        return arg2.canSpawn(arg, SpawnReason.NATURAL) && arg2.canSpawn(arg);
    }

    @Nullable
    private static Biome.SpawnEntry pickRandomSpawnEntry(ServerWorld arg, StructureAccessor arg2, ChunkGenerator arg3, SpawnGroup arg4, Random random, BlockPos arg5) {
        Biome lv = arg.getBiome(arg5);
        if (arg4 == SpawnGroup.WATER_AMBIENT && lv.getCategory() == Biome.Category.RIVER && random.nextFloat() < 0.98f) {
            return null;
        }
        List<Biome.SpawnEntry> list = SpawnHelper.method_29950(arg, arg2, arg3, arg4, arg5, lv);
        if (list.isEmpty()) {
            return null;
        }
        return WeightedPicker.getRandom(random, list);
    }

    private static boolean containsSpawnEntry(ServerWorld arg, StructureAccessor arg2, ChunkGenerator arg3, SpawnGroup arg4, Biome.SpawnEntry arg5, BlockPos arg6) {
        return SpawnHelper.method_29950(arg, arg2, arg3, arg4, arg6, null).contains(arg5);
    }

    private static List<Biome.SpawnEntry> method_29950(ServerWorld arg, StructureAccessor arg2, ChunkGenerator arg3, SpawnGroup arg4, BlockPos arg5, @Nullable Biome arg6) {
        if (arg4 == SpawnGroup.MONSTER && arg.getBlockState(arg5.down()).getBlock() == Blocks.NETHER_BRICKS && arg2.method_28388(arg5, false, StructureFeature.FORTRESS).hasChildren()) {
            return StructureFeature.FORTRESS.getMonsterSpawns();
        }
        return arg3.getEntitySpawnList(arg6 != null ? arg6 : arg.getBiome(arg5), arg2, arg4, arg5);
    }

    private static BlockPos getSpawnPos(World arg, WorldChunk arg2) {
        ChunkPos lv = arg2.getPos();
        int i = lv.getStartX() + arg.random.nextInt(16);
        int j = lv.getStartZ() + arg.random.nextInt(16);
        int k = arg2.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, i, j) + 1;
        int l = arg.random.nextInt(k + 1);
        return new BlockPos(i, l, j);
    }

    public static boolean isClearForSpawn(BlockView arg, BlockPos arg2, BlockState arg3, FluidState arg4, EntityType arg5) {
        if (arg3.isFullCube(arg, arg2)) {
            return false;
        }
        if (arg3.emitsRedstonePower()) {
            return false;
        }
        if (!arg4.isEmpty()) {
            return false;
        }
        if (arg3.isIn(BlockTags.PREVENT_MOB_SPAWNING_INSIDE)) {
            return false;
        }
        return !arg5.method_29496(arg3);
    }

    public static boolean canSpawn(SpawnRestriction.Location arg, WorldView arg2, BlockPos arg3, @Nullable EntityType<?> arg4) {
        if (arg == SpawnRestriction.Location.NO_RESTRICTIONS) {
            return true;
        }
        if (arg4 == null || !arg2.getWorldBorder().contains(arg3)) {
            return false;
        }
        BlockState lv = arg2.getBlockState(arg3);
        FluidState lv2 = arg2.getFluidState(arg3);
        BlockPos lv3 = arg3.up();
        BlockPos lv4 = arg3.down();
        switch (arg) {
            case IN_WATER: {
                return lv2.matches(FluidTags.WATER) && arg2.getFluidState(lv4).matches(FluidTags.WATER) && !arg2.getBlockState(lv3).isSolidBlock(arg2, lv3);
            }
            case IN_LAVA: {
                return lv2.matches(FluidTags.LAVA);
            }
        }
        BlockState lv5 = arg2.getBlockState(lv4);
        if (!lv5.allowsSpawning(arg2, lv4, arg4)) {
            return false;
        }
        return SpawnHelper.isClearForSpawn(arg2, arg3, lv, lv2, arg4) && SpawnHelper.isClearForSpawn(arg2, lv3, arg2.getBlockState(lv3), arg2.getFluidState(lv3), arg4);
    }

    /*
     * WARNING - void declaration
     */
    public static void populateEntities(WorldAccess arg, Biome arg2, int i, int j, Random random) {
        List<Biome.SpawnEntry> list = arg2.getEntitySpawnList(SpawnGroup.CREATURE);
        if (list.isEmpty()) {
            return;
        }
        int k = i << 4;
        int l = j << 4;
        while (random.nextFloat() < arg2.getMaxSpawnChance()) {
            Biome.SpawnEntry lv = WeightedPicker.getRandom(random, list);
            int m = lv.minGroupSize + random.nextInt(1 + lv.maxGroupSize - lv.minGroupSize);
            EntityData lv2 = null;
            int n = k + random.nextInt(16);
            int o = l + random.nextInt(16);
            int p = n;
            int q = o;
            for (int r = 0; r < m; ++r) {
                boolean bl = false;
                for (int s = 0; !bl && s < 4; ++s) {
                    BlockPos lv3 = SpawnHelper.getEntitySpawnPos(arg, lv.type, n, o);
                    if (lv.type.isSummonable() && SpawnHelper.canSpawn(SpawnRestriction.getLocation(lv.type), arg, lv3, lv.type)) {
                        MobEntity lv6;
                        void lv5;
                        float f = lv.type.getWidth();
                        double d = MathHelper.clamp((double)n, (double)k + (double)f, (double)k + 16.0 - (double)f);
                        double e = MathHelper.clamp((double)o, (double)l + (double)f, (double)l + 16.0 - (double)f);
                        if (!arg.doesNotCollide(lv.type.createSimpleBoundingBox(d, lv3.getY(), e)) || !SpawnRestriction.canSpawn(lv.type, arg, SpawnReason.CHUNK_GENERATION, new BlockPos(d, (double)lv3.getY(), e), arg.getRandom())) continue;
                        try {
                            Object lv4 = lv.type.create(arg.getWorld());
                        }
                        catch (Exception exception) {
                            LOGGER.warn("Failed to create mob", (Throwable)exception);
                            continue;
                        }
                        lv5.refreshPositionAndAngles(d, lv3.getY(), e, random.nextFloat() * 360.0f, 0.0f);
                        if (lv5 instanceof MobEntity && (lv6 = (MobEntity)lv5).canSpawn(arg, SpawnReason.CHUNK_GENERATION) && lv6.canSpawn(arg)) {
                            lv2 = lv6.initialize(arg, arg.getLocalDifficulty(lv6.getBlockPos()), SpawnReason.CHUNK_GENERATION, lv2, null);
                            arg.spawnEntity(lv6);
                            bl = true;
                        }
                    }
                    n += random.nextInt(5) - random.nextInt(5);
                    o += random.nextInt(5) - random.nextInt(5);
                    while (n < k || n >= k + 16 || o < l || o >= l + 16) {
                        n = p + random.nextInt(5) - random.nextInt(5);
                        o = q + random.nextInt(5) - random.nextInt(5);
                    }
                }
            }
        }
    }

    private static BlockPos getEntitySpawnPos(WorldView arg, EntityType<?> arg2, int i, int j) {
        Vec3i lv2;
        int k = arg.getTopY(SpawnRestriction.getHeightmapType(arg2), i, j);
        BlockPos.Mutable lv = new BlockPos.Mutable(i, k, j);
        if (arg.getDimension().hasCeiling()) {
            do {
                lv.move(Direction.DOWN);
            } while (!arg.getBlockState(lv).isAir());
            do {
                lv.move(Direction.DOWN);
            } while (arg.getBlockState(lv).isAir() && lv.getY() > 0);
        }
        if (SpawnRestriction.getLocation(arg2) == SpawnRestriction.Location.ON_GROUND && arg.getBlockState((BlockPos)(lv2 = lv.down())).canPathfindThrough(arg, (BlockPos)lv2, NavigationType.LAND)) {
            return lv2;
        }
        return lv.toImmutable();
    }

    @FunctionalInterface
    public static interface ChunkSource {
        public void query(long var1, Consumer<WorldChunk> var3);
    }

    @FunctionalInterface
    public static interface Runner {
        public void run(MobEntity var1, Chunk var2);
    }

    @FunctionalInterface
    public static interface Checker {
        public boolean test(EntityType<?> var1, BlockPos var2, Chunk var3);
    }

    public static class Info {
        private final int spawningChunkCount;
        private final Object2IntOpenHashMap<SpawnGroup> groupToCount;
        private final GravityField densityField;
        private final Object2IntMap<SpawnGroup> groupToCountView;
        @Nullable
        private BlockPos cachedPos;
        @Nullable
        private EntityType<?> cachedEntityType;
        private double cachedDensityMass;

        private Info(int i, Object2IntOpenHashMap<SpawnGroup> object2IntOpenHashMap, GravityField arg) {
            this.spawningChunkCount = i;
            this.groupToCount = object2IntOpenHashMap;
            this.densityField = arg;
            this.groupToCountView = Object2IntMaps.unmodifiable(object2IntOpenHashMap);
        }

        private boolean test(EntityType<?> arg, BlockPos arg2, Chunk arg3) {
            double d;
            this.cachedPos = arg2;
            this.cachedEntityType = arg;
            Biome lv = SpawnHelper.getBiomeDirectly(arg2, arg3);
            Biome.SpawnDensity lv2 = lv.getSpawnDensity(arg);
            if (lv2 == null) {
                this.cachedDensityMass = 0.0;
                return true;
            }
            this.cachedDensityMass = d = lv2.getMass();
            double e = this.densityField.calculate(arg2, d);
            return e <= lv2.getGravityLimit();
        }

        private void run(MobEntity arg, Chunk arg2) {
            double f;
            EntityType<?> lv = arg.getType();
            BlockPos lv2 = arg.getBlockPos();
            if (lv2.equals(this.cachedPos) && lv == this.cachedEntityType) {
                double d = this.cachedDensityMass;
            } else {
                Biome lv3 = SpawnHelper.getBiomeDirectly(lv2, arg2);
                Biome.SpawnDensity lv4 = lv3.getSpawnDensity(lv);
                if (lv4 != null) {
                    double e = lv4.getMass();
                } else {
                    f = 0.0;
                }
            }
            this.densityField.addPoint(lv2, f);
            this.groupToCount.addTo((Object)lv.getSpawnGroup(), 1);
        }

        @Environment(value=EnvType.CLIENT)
        public int getSpawningChunkCount() {
            return this.spawningChunkCount;
        }

        public Object2IntMap<SpawnGroup> getGroupToCount() {
            return this.groupToCountView;
        }

        private boolean isBelowCap(SpawnGroup arg) {
            int i = arg.getCapacity() * this.spawningChunkCount / CHUNK_AREA;
            return this.groupToCount.getInt((Object)arg) < i;
        }
    }
}

