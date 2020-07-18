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
import net.minecraft.class_5425;
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

    public static Info setupSpawn(int spawningChunkCount, Iterable<Entity> entities, ChunkSource chunkSource) {
        GravityField lv = new GravityField();
        Object2IntOpenHashMap object2IntOpenHashMap = new Object2IntOpenHashMap();
        for (Entity lv2 : entities) {
            SpawnGroup lv4;
            MobEntity lv3;
            if (lv2 instanceof MobEntity && ((lv3 = (MobEntity)lv2).isPersistent() || lv3.cannotDespawn()) || (lv4 = lv2.getType().getSpawnGroup()) == SpawnGroup.MISC) continue;
            BlockPos lv5 = lv2.getBlockPos();
            long l = ChunkPos.toLong(lv5.getX() >> 4, lv5.getZ() >> 4);
            chunkSource.query(l, arg5 -> {
                Biome lv = SpawnHelper.getBiomeDirectly(lv5, arg5);
                Biome.SpawnDensity lv2 = lv.getSpawnDensity(lv2.getType());
                if (lv2 != null) {
                    lv.addPoint(lv2.getBlockPos(), lv2.getMass());
                }
                object2IntOpenHashMap.addTo((Object)lv4, 1);
            });
        }
        return new Info(spawningChunkCount, object2IntOpenHashMap, lv);
    }

    private static Biome getBiomeDirectly(BlockPos pos, Chunk chunk) {
        return DirectBiomeAccessType.INSTANCE.getBiome(0L, pos.getX(), pos.getY(), pos.getZ(), chunk.getBiomeArray());
    }

    public static void spawn(ServerWorld world, WorldChunk chunk, Info info, boolean spawnAnimals, boolean spawnMonsters, boolean shouldSpawnAnimals) {
        world.getProfiler().push("spawner");
        for (SpawnGroup mobSpawnGroup : SPAWNABLE_GROUPS) {
            if (!spawnAnimals && mobSpawnGroup.isPeaceful() || !spawnMonsters && !mobSpawnGroup.isPeaceful() || !shouldSpawnAnimals && mobSpawnGroup.isAnimal() || !info.isBelowCap(mobSpawnGroup)) continue;
            SpawnHelper.spawnEntitiesInChunk(mobSpawnGroup, world, chunk, (arg2, arg3, arg4) -> info.test(arg2, arg3, arg4), (arg2, arg3) -> info.run(arg2, arg3));
        }
        world.getProfiler().pop();
    }

    public static void spawnEntitiesInChunk(SpawnGroup group, ServerWorld world, WorldChunk chunk, Checker checker, Runner runner) {
        BlockPos lv = SpawnHelper.getSpawnPos(world, chunk);
        if (lv.getY() < 1) {
            return;
        }
        SpawnHelper.spawnEntitiesInChunk(group, world, chunk, lv, checker, runner);
    }

    public static void spawnEntitiesInChunk(SpawnGroup group, ServerWorld world, Chunk chunk, BlockPos pos, Checker checker, Runner runner) {
        StructureAccessor lv = world.getStructureAccessor();
        ChunkGenerator lv2 = world.getChunkManager().getChunkGenerator();
        int i = pos.getY();
        BlockState lv3 = chunk.getBlockState(pos);
        if (lv3.isSolidBlock(chunk, pos)) {
            return;
        }
        BlockPos.Mutable lv4 = new BlockPos.Mutable();
        int j = 0;
        block0: for (int k = 0; k < 3; ++k) {
            int l = pos.getX();
            int m = pos.getZ();
            int n = 6;
            Biome.SpawnEntry lv5 = null;
            EntityData lv6 = null;
            int o = MathHelper.ceil(world.random.nextFloat() * 4.0f);
            int p = 0;
            for (int q = 0; q < o; ++q) {
                double f;
                lv4.set(l += world.random.nextInt(6) - world.random.nextInt(6), i, m += world.random.nextInt(6) - world.random.nextInt(6));
                double d = (double)l + 0.5;
                double e = (double)m + 0.5;
                PlayerEntity lv7 = world.getClosestPlayer(d, (double)i, e, -1.0, false);
                if (lv7 == null || !SpawnHelper.isAcceptableSpawnPosition(world, chunk, lv4, f = lv7.squaredDistanceTo(d, i, e))) continue;
                if (lv5 == null) {
                    lv5 = SpawnHelper.pickRandomSpawnEntry(world, lv, lv2, group, world.random, lv4);
                    if (lv5 == null) continue block0;
                    o = lv5.minGroupSize + world.random.nextInt(1 + lv5.maxGroupSize - lv5.minGroupSize);
                }
                if (!SpawnHelper.canSpawn(world, group, lv, lv2, lv5, lv4, f) || !checker.test(lv5.type, lv4, chunk)) continue;
                MobEntity lv8 = SpawnHelper.createMob(world, lv5.type);
                if (lv8 == null) {
                    return;
                }
                lv8.refreshPositionAndAngles(d, i, e, world.random.nextFloat() * 360.0f, 0.0f);
                if (!SpawnHelper.isValidSpawn(world, lv8, f)) continue;
                lv6 = lv8.initialize(world, world.getLocalDifficulty(lv8.getBlockPos()), SpawnReason.NATURAL, lv6, null);
                ++p;
                world.spawnEntity(lv8);
                runner.run(lv8, chunk);
                if (++j >= lv8.getLimitPerChunk()) {
                    return;
                }
                if (lv8.spawnsTooManyForEachTry(p)) continue block0;
            }
        }
    }

    private static boolean isAcceptableSpawnPosition(ServerWorld world, Chunk chunk, BlockPos.Mutable pos, double squaredDistance) {
        if (squaredDistance <= 576.0) {
            return false;
        }
        if (world.getSpawnPos().isWithinDistance(new Vec3d((double)pos.getX() + 0.5, pos.getY(), (double)pos.getZ() + 0.5), 24.0)) {
            return false;
        }
        ChunkPos lv = new ChunkPos(pos);
        return Objects.equals(lv, chunk.getPos()) || world.getChunkManager().shouldTickChunk(lv);
    }

    private static boolean canSpawn(ServerWorld world, SpawnGroup group, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Biome.SpawnEntry spawnEntry, BlockPos.Mutable pos, double squaredDistance) {
        EntityType<?> lv = spawnEntry.type;
        if (lv.getSpawnGroup() == SpawnGroup.MISC) {
            return false;
        }
        if (!lv.isSpawnableFarFromPlayer() && squaredDistance > (double)(lv.getSpawnGroup().getImmediateDespawnRange() * lv.getSpawnGroup().getImmediateDespawnRange())) {
            return false;
        }
        if (!lv.isSummonable() || !SpawnHelper.containsSpawnEntry(world, structureAccessor, chunkGenerator, group, spawnEntry, pos)) {
            return false;
        }
        SpawnRestriction.Location lv2 = SpawnRestriction.getLocation(lv);
        if (!SpawnHelper.canSpawn(lv2, world, pos, lv)) {
            return false;
        }
        if (!SpawnRestriction.canSpawn(lv, world, SpawnReason.NATURAL, pos, world.random)) {
            return false;
        }
        return world.doesNotCollide(lv.createSimpleBoundingBox((double)pos.getX() + 0.5, pos.getY(), (double)pos.getZ() + 0.5));
    }

    /*
     * WARNING - void declaration
     */
    @Nullable
    private static MobEntity createMob(ServerWorld world, EntityType<?> type) {
        void lv3;
        try {
            Object lv = type.create(world);
            if (!(lv instanceof MobEntity)) {
                throw new IllegalStateException("Trying to spawn a non-mob: " + Registry.ENTITY_TYPE.getId(type));
            }
            MobEntity lv2 = (MobEntity)lv;
        }
        catch (Exception exception) {
            LOGGER.warn("Failed to create mob", (Throwable)exception);
            return null;
        }
        return lv3;
    }

    private static boolean isValidSpawn(ServerWorld world, MobEntity entity, double squaredDistance) {
        if (squaredDistance > (double)(entity.getType().getSpawnGroup().getImmediateDespawnRange() * entity.getType().getSpawnGroup().getImmediateDespawnRange()) && entity.canImmediatelyDespawn(squaredDistance)) {
            return false;
        }
        return entity.canSpawn(world, SpawnReason.NATURAL) && entity.canSpawn(world);
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

    private static BlockPos getSpawnPos(World world, WorldChunk chunk) {
        ChunkPos lv = chunk.getPos();
        int i = lv.getStartX() + world.random.nextInt(16);
        int j = lv.getStartZ() + world.random.nextInt(16);
        int k = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, i, j) + 1;
        int l = world.random.nextInt(k + 1);
        return new BlockPos(i, l, j);
    }

    public static boolean isClearForSpawn(BlockView blockView, BlockPos pos, BlockState state, FluidState fluidState, EntityType<?> arg5) {
        if (state.isFullCube(blockView, pos)) {
            return false;
        }
        if (state.emitsRedstonePower()) {
            return false;
        }
        if (!fluidState.isEmpty()) {
            return false;
        }
        if (state.isIn(BlockTags.PREVENT_MOB_SPAWNING_INSIDE)) {
            return false;
        }
        return !arg5.method_29496(state);
    }

    public static boolean canSpawn(SpawnRestriction.Location location, WorldView world, BlockPos pos, @Nullable EntityType<?> entityType) {
        if (location == SpawnRestriction.Location.NO_RESTRICTIONS) {
            return true;
        }
        if (entityType == null || !world.getWorldBorder().contains(pos)) {
            return false;
        }
        BlockState lv = world.getBlockState(pos);
        FluidState lv2 = world.getFluidState(pos);
        BlockPos lv3 = pos.up();
        BlockPos lv4 = pos.down();
        switch (location) {
            case IN_WATER: {
                return lv2.isIn(FluidTags.WATER) && world.getFluidState(lv4).isIn(FluidTags.WATER) && !world.getBlockState(lv3).isSolidBlock(world, lv3);
            }
            case IN_LAVA: {
                return lv2.isIn(FluidTags.LAVA);
            }
        }
        BlockState lv5 = world.getBlockState(lv4);
        if (!lv5.allowsSpawning(world, lv4, entityType)) {
            return false;
        }
        return SpawnHelper.isClearForSpawn(world, pos, lv, lv2, entityType) && SpawnHelper.isClearForSpawn(world, lv3, world.getBlockState(lv3), world.getFluidState(lv3), entityType);
    }

    /*
     * WARNING - void declaration
     */
    public static void populateEntities(class_5425 arg, Biome biome, int chunkX, int chunkZ, Random random) {
        List<Biome.SpawnEntry> list = biome.getEntitySpawnList(SpawnGroup.CREATURE);
        if (list.isEmpty()) {
            return;
        }
        int k = chunkX << 4;
        int l = chunkZ << 4;
        while (random.nextFloat() < biome.getMaxSpawnChance()) {
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

    private static BlockPos getEntitySpawnPos(WorldView world, EntityType<?> entityType, int x, int z) {
        Vec3i lv2;
        int k = world.getTopY(SpawnRestriction.getHeightmapType(entityType), x, z);
        BlockPos.Mutable lv = new BlockPos.Mutable(x, k, z);
        if (world.getDimension().hasCeiling()) {
            do {
                lv.move(Direction.DOWN);
            } while (!world.getBlockState(lv).isAir());
            do {
                lv.move(Direction.DOWN);
            } while (world.getBlockState(lv).isAir() && lv.getY() > 0);
        }
        if (SpawnRestriction.getLocation(entityType) == SpawnRestriction.Location.ON_GROUND && world.getBlockState((BlockPos)(lv2 = lv.down())).canPathfindThrough(world, (BlockPos)lv2, NavigationType.LAND)) {
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

        private Info(int spawningChunkCount, Object2IntOpenHashMap<SpawnGroup> groupToCount, GravityField densityField) {
            this.spawningChunkCount = spawningChunkCount;
            this.groupToCount = groupToCount;
            this.densityField = densityField;
            this.groupToCountView = Object2IntMaps.unmodifiable(groupToCount);
        }

        private boolean test(EntityType<?> type, BlockPos pos, Chunk chunk) {
            double d;
            this.cachedPos = pos;
            this.cachedEntityType = type;
            Biome lv = SpawnHelper.getBiomeDirectly(pos, chunk);
            Biome.SpawnDensity lv2 = lv.getSpawnDensity(type);
            if (lv2 == null) {
                this.cachedDensityMass = 0.0;
                return true;
            }
            this.cachedDensityMass = d = lv2.getMass();
            double e = this.densityField.calculate(pos, d);
            return e <= lv2.getGravityLimit();
        }

        private void run(MobEntity entity, Chunk chunk) {
            double f;
            EntityType<?> lv = entity.getType();
            BlockPos lv2 = entity.getBlockPos();
            if (lv2.equals(this.cachedPos) && lv == this.cachedEntityType) {
                double d = this.cachedDensityMass;
            } else {
                Biome lv3 = SpawnHelper.getBiomeDirectly(lv2, chunk);
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

        private boolean isBelowCap(SpawnGroup group) {
            int i = group.getCapacity() * this.spawningChunkCount / CHUNK_AREA;
            return this.groupToCount.getInt((Object)group) < i;
        }
    }
}

