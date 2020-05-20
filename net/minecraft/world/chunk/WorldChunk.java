/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.shorts.ShortList
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.chunk;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.world.DummyClientTickScheduler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerTickScheduler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.SimpleTickScheduler;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.collection.TypeFilterableList;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkTickScheduler;
import net.minecraft.world.Heightmap;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldChunk
implements Chunk {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final ChunkSection EMPTY_SECTION = null;
    private final ChunkSection[] sections = new ChunkSection[16];
    private BiomeArray biomeArray;
    private final Map<BlockPos, CompoundTag> pendingBlockEntityTags = Maps.newHashMap();
    private boolean loadedToWorld;
    private final World world;
    private final Map<Heightmap.Type, Heightmap> heightmaps = Maps.newEnumMap(Heightmap.Type.class);
    private final UpgradeData upgradeData;
    private final Map<BlockPos, BlockEntity> blockEntities = Maps.newHashMap();
    private final TypeFilterableList<Entity>[] entitySections;
    private final Map<String, StructureStart> structureStarts = Maps.newHashMap();
    private final Map<String, LongSet> structureReferences = Maps.newHashMap();
    private final ShortList[] postProcessingLists = new ShortList[16];
    private TickScheduler<Block> blockTickScheduler;
    private TickScheduler<Fluid> fluidTickScheduler;
    private boolean unsaved;
    private long lastSaveTime;
    private volatile boolean shouldSave;
    private long inhabitedTime;
    @Nullable
    private Supplier<ChunkHolder.LevelType> levelTypeProvider;
    @Nullable
    private Consumer<WorldChunk> loadToWorldConsumer;
    private final ChunkPos pos;
    private volatile boolean lightOn;

    public WorldChunk(World arg, ChunkPos arg2, BiomeArray arg3) {
        this(arg, arg2, arg3, UpgradeData.NO_UPGRADE_DATA, DummyClientTickScheduler.get(), DummyClientTickScheduler.get(), 0L, null, null);
    }

    public WorldChunk(World arg, ChunkPos arg2, BiomeArray arg3, UpgradeData arg4, TickScheduler<Block> arg5, TickScheduler<Fluid> arg6, long l, @Nullable ChunkSection[] args, @Nullable Consumer<WorldChunk> consumer) {
        this.entitySections = new TypeFilterableList[16];
        this.world = arg;
        this.pos = arg2;
        this.upgradeData = arg4;
        for (Heightmap.Type lv : Heightmap.Type.values()) {
            if (!ChunkStatus.FULL.getHeightmapTypes().contains((Object)lv)) continue;
            this.heightmaps.put(lv, new Heightmap(this, lv));
        }
        for (int i = 0; i < this.entitySections.length; ++i) {
            this.entitySections[i] = new TypeFilterableList<Entity>(Entity.class);
        }
        this.biomeArray = arg3;
        this.blockTickScheduler = arg5;
        this.fluidTickScheduler = arg6;
        this.inhabitedTime = l;
        this.loadToWorldConsumer = consumer;
        if (args != null) {
            if (this.sections.length == args.length) {
                System.arraycopy(args, 0, this.sections, 0, this.sections.length);
            } else {
                LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", (Object)args.length, (Object)this.sections.length);
            }
        }
    }

    public WorldChunk(World arg2, ProtoChunk arg22) {
        this(arg2, arg22.getPos(), arg22.getBiomeArray(), arg22.getUpgradeData(), arg22.getBlockTickScheduler(), arg22.getFluidTickScheduler(), arg22.getInhabitedTime(), arg22.getSectionArray(), null);
        for (CompoundTag lv : arg22.getEntities()) {
            EntityType.loadEntityWithPassengers(lv, arg2, arg -> {
                this.addEntity((Entity)arg);
                return arg;
            });
        }
        for (BlockEntity lv2 : arg22.getBlockEntities().values()) {
            this.addBlockEntity(lv2);
        }
        this.pendingBlockEntityTags.putAll(arg22.getBlockEntityTags());
        for (int i = 0; i < arg22.getPostProcessingLists().length; ++i) {
            this.postProcessingLists[i] = arg22.getPostProcessingLists()[i];
        }
        this.setStructureStarts(arg22.getStructureStarts());
        this.setStructureReferences(arg22.getStructureReferences());
        for (Map.Entry<Heightmap.Type, Heightmap> entry : arg22.getHeightmaps()) {
            if (!ChunkStatus.FULL.getHeightmapTypes().contains((Object)entry.getKey())) continue;
            this.getHeightmap(entry.getKey()).setTo(entry.getValue().asLongArray());
        }
        this.setLightOn(arg22.isLightOn());
        this.shouldSave = true;
    }

    @Override
    public Heightmap getHeightmap(Heightmap.Type arg2) {
        return this.heightmaps.computeIfAbsent(arg2, arg -> new Heightmap(this, (Heightmap.Type)((Object)arg)));
    }

    @Override
    public Set<BlockPos> getBlockEntityPositions() {
        HashSet set = Sets.newHashSet(this.pendingBlockEntityTags.keySet());
        set.addAll(this.blockEntities.keySet());
        return set;
    }

    @Override
    public ChunkSection[] getSectionArray() {
        return this.sections;
    }

    @Override
    public BlockState getBlockState(BlockPos arg) {
        int i = arg.getX();
        int j = arg.getY();
        int k = arg.getZ();
        if (this.world.method_27982()) {
            BlockState lv = null;
            if (j == 60) {
                lv = Blocks.BARRIER.getDefaultState();
            }
            if (j == 70) {
                lv = DebugChunkGenerator.getBlockState(i, k);
            }
            return lv == null ? Blocks.AIR.getDefaultState() : lv;
        }
        try {
            ChunkSection lv2;
            if (j >= 0 && j >> 4 < this.sections.length && !ChunkSection.isEmpty(lv2 = this.sections[j >> 4])) {
                return lv2.getBlockState(i & 0xF, j & 0xF, k & 0xF);
            }
            return Blocks.AIR.getDefaultState();
        }
        catch (Throwable throwable) {
            CrashReport lv3 = CrashReport.create(throwable, "Getting block state");
            CrashReportSection lv4 = lv3.addElement("Block being got");
            lv4.add("Location", () -> CrashReportSection.createPositionString(i, j, k));
            throw new CrashException(lv3);
        }
    }

    @Override
    public FluidState getFluidState(BlockPos arg) {
        return this.getFluidState(arg.getX(), arg.getY(), arg.getZ());
    }

    public FluidState getFluidState(int i, int j, int k) {
        try {
            ChunkSection lv;
            if (j >= 0 && j >> 4 < this.sections.length && !ChunkSection.isEmpty(lv = this.sections[j >> 4])) {
                return lv.getFluidState(i & 0xF, j & 0xF, k & 0xF);
            }
            return Fluids.EMPTY.getDefaultState();
        }
        catch (Throwable throwable) {
            CrashReport lv2 = CrashReport.create(throwable, "Getting fluid state");
            CrashReportSection lv3 = lv2.addElement("Block being got");
            lv3.add("Location", () -> CrashReportSection.createPositionString(i, j, k));
            throw new CrashException(lv2);
        }
    }

    @Override
    @Nullable
    public BlockState setBlockState(BlockPos arg, BlockState arg2, boolean bl) {
        BlockEntity lv5;
        int i = arg.getX() & 0xF;
        int j = arg.getY();
        int k = arg.getZ() & 0xF;
        ChunkSection lv = this.sections[j >> 4];
        if (lv == EMPTY_SECTION) {
            if (arg2.isAir()) {
                return null;
            }
            this.sections[j >> 4] = lv = new ChunkSection(j >> 4 << 4);
        }
        boolean bl2 = lv.isEmpty();
        BlockState lv2 = lv.setBlockState(i, j & 0xF, k, arg2);
        if (lv2 == arg2) {
            return null;
        }
        Block lv3 = arg2.getBlock();
        Block lv4 = lv2.getBlock();
        this.heightmaps.get((Object)Heightmap.Type.MOTION_BLOCKING).trackUpdate(i, j, k, arg2);
        this.heightmaps.get((Object)Heightmap.Type.MOTION_BLOCKING_NO_LEAVES).trackUpdate(i, j, k, arg2);
        this.heightmaps.get((Object)Heightmap.Type.OCEAN_FLOOR).trackUpdate(i, j, k, arg2);
        this.heightmaps.get((Object)Heightmap.Type.WORLD_SURFACE).trackUpdate(i, j, k, arg2);
        boolean bl3 = lv.isEmpty();
        if (bl2 != bl3) {
            this.world.getChunkManager().getLightingProvider().updateSectionStatus(arg, bl3);
        }
        if (!this.world.isClient) {
            lv2.onStateReplaced(this.world, arg, arg2, bl);
        } else if (lv4 != lv3 && lv4 instanceof BlockEntityProvider) {
            this.world.removeBlockEntity(arg);
        }
        if (!lv.getBlockState(i, j & 0xF, k).isOf(lv3)) {
            return null;
        }
        if (lv4 instanceof BlockEntityProvider && (lv5 = this.getBlockEntity(arg, CreationType.CHECK)) != null) {
            lv5.resetBlock();
        }
        if (!this.world.isClient) {
            arg2.onBlockAdded(this.world, arg, lv2, bl);
        }
        if (lv3 instanceof BlockEntityProvider) {
            BlockEntity lv6 = this.getBlockEntity(arg, CreationType.CHECK);
            if (lv6 == null) {
                lv6 = ((BlockEntityProvider)((Object)lv3)).createBlockEntity(this.world);
                this.world.setBlockEntity(arg, lv6);
            } else {
                lv6.resetBlock();
            }
        }
        this.shouldSave = true;
        return lv2;
    }

    @Nullable
    public LightingProvider getLightingProvider() {
        return this.world.getChunkManager().getLightingProvider();
    }

    @Override
    public void addEntity(Entity arg) {
        int k;
        this.unsaved = true;
        int i = MathHelper.floor(arg.getX() / 16.0);
        int j = MathHelper.floor(arg.getZ() / 16.0);
        if (i != this.pos.x || j != this.pos.z) {
            LOGGER.warn("Wrong location! ({}, {}) should be ({}, {}), {}", (Object)i, (Object)j, (Object)this.pos.x, (Object)this.pos.z, (Object)arg);
            arg.removed = true;
        }
        if ((k = MathHelper.floor(arg.getY() / 16.0)) < 0) {
            k = 0;
        }
        if (k >= this.entitySections.length) {
            k = this.entitySections.length - 1;
        }
        arg.updateNeeded = true;
        arg.chunkX = this.pos.x;
        arg.chunkY = k;
        arg.chunkZ = this.pos.z;
        this.entitySections[k].add(arg);
    }

    @Override
    public void setHeightmap(Heightmap.Type arg, long[] ls) {
        this.heightmaps.get((Object)arg).setTo(ls);
    }

    public void remove(Entity arg) {
        this.remove(arg, arg.chunkY);
    }

    public void remove(Entity arg, int i) {
        if (i < 0) {
            i = 0;
        }
        if (i >= this.entitySections.length) {
            i = this.entitySections.length - 1;
        }
        this.entitySections[i].remove(arg);
    }

    @Override
    public int sampleHeightmap(Heightmap.Type arg, int i, int j) {
        return this.heightmaps.get((Object)arg).get(i & 0xF, j & 0xF) - 1;
    }

    @Nullable
    private BlockEntity createBlockEntity(BlockPos arg) {
        BlockState lv = this.getBlockState(arg);
        Block lv2 = lv.getBlock();
        if (!lv2.hasBlockEntity()) {
            return null;
        }
        return ((BlockEntityProvider)((Object)lv2)).createBlockEntity(this.world);
    }

    @Override
    @Nullable
    public BlockEntity getBlockEntity(BlockPos arg) {
        return this.getBlockEntity(arg, CreationType.CHECK);
    }

    @Nullable
    public BlockEntity getBlockEntity(BlockPos arg, CreationType arg2) {
        BlockEntity lv3;
        CompoundTag lv2;
        BlockEntity lv = this.blockEntities.get(arg);
        if (lv == null && (lv2 = this.pendingBlockEntityTags.remove(arg)) != null && (lv3 = this.loadBlockEntity(arg, lv2)) != null) {
            return lv3;
        }
        if (lv == null) {
            if (arg2 == CreationType.IMMEDIATE) {
                lv = this.createBlockEntity(arg);
                this.world.setBlockEntity(arg, lv);
            }
        } else if (lv.isRemoved()) {
            this.blockEntities.remove(arg);
            return null;
        }
        return lv;
    }

    public void addBlockEntity(BlockEntity arg) {
        this.setBlockEntity(arg.getPos(), arg);
        if (this.loadedToWorld || this.world.isClient()) {
            this.world.setBlockEntity(arg.getPos(), arg);
        }
    }

    @Override
    public void setBlockEntity(BlockPos arg, BlockEntity arg2) {
        if (!(this.getBlockState(arg).getBlock() instanceof BlockEntityProvider)) {
            return;
        }
        arg2.setLocation(this.world, arg);
        arg2.cancelRemoval();
        BlockEntity lv = this.blockEntities.put(arg.toImmutable(), arg2);
        if (lv != null && lv != arg2) {
            lv.markRemoved();
        }
    }

    @Override
    public void addPendingBlockEntityTag(CompoundTag arg) {
        this.pendingBlockEntityTags.put(new BlockPos(arg.getInt("x"), arg.getInt("y"), arg.getInt("z")), arg);
    }

    @Override
    @Nullable
    public CompoundTag method_20598(BlockPos arg) {
        BlockEntity lv = this.getBlockEntity(arg);
        if (lv != null && !lv.isRemoved()) {
            CompoundTag lv2 = lv.toTag(new CompoundTag());
            lv2.putBoolean("keepPacked", false);
            return lv2;
        }
        CompoundTag lv3 = this.pendingBlockEntityTags.get(arg);
        if (lv3 != null) {
            lv3 = lv3.copy();
            lv3.putBoolean("keepPacked", true);
        }
        return lv3;
    }

    @Override
    public void removeBlockEntity(BlockPos arg) {
        BlockEntity lv;
        if ((this.loadedToWorld || this.world.isClient()) && (lv = this.blockEntities.remove(arg)) != null) {
            lv.markRemoved();
        }
    }

    public void loadToWorld() {
        if (this.loadToWorldConsumer != null) {
            this.loadToWorldConsumer.accept(this);
            this.loadToWorldConsumer = null;
        }
    }

    public void markDirty() {
        this.shouldSave = true;
    }

    public void getEntities(@Nullable Entity arg, Box arg2, List<Entity> list, @Nullable Predicate<? super Entity> predicate) {
        int i = MathHelper.floor((arg2.minY - 2.0) / 16.0);
        int j = MathHelper.floor((arg2.maxY + 2.0) / 16.0);
        i = MathHelper.clamp(i, 0, this.entitySections.length - 1);
        j = MathHelper.clamp(j, 0, this.entitySections.length - 1);
        for (int k = i; k <= j; ++k) {
            if (this.entitySections[k].isEmpty()) continue;
            for (Entity lv : this.entitySections[k]) {
                if (!lv.getBoundingBox().intersects(arg2) || lv == arg) continue;
                if (predicate == null || predicate.test(lv)) {
                    list.add(lv);
                }
                if (!(lv instanceof EnderDragonEntity)) continue;
                for (EnderDragonPart lv2 : ((EnderDragonEntity)lv).getBodyParts()) {
                    if (lv2 == arg || !lv2.getBoundingBox().intersects(arg2) || predicate != null && !predicate.test(lv2)) continue;
                    list.add(lv2);
                }
            }
        }
    }

    public <T extends Entity> void getEntities(@Nullable EntityType<?> arg, Box arg2, List<? super T> list, Predicate<? super T> predicate) {
        int i = MathHelper.floor((arg2.minY - 2.0) / 16.0);
        int j = MathHelper.floor((arg2.maxY + 2.0) / 16.0);
        i = MathHelper.clamp(i, 0, this.entitySections.length - 1);
        j = MathHelper.clamp(j, 0, this.entitySections.length - 1);
        for (int k = i; k <= j; ++k) {
            for (Entity lv : this.entitySections[k].getAllOfType(Entity.class)) {
                if (arg != null && lv.getType() != arg) continue;
                Entity lv2 = lv;
                if (!lv.getBoundingBox().intersects(arg2) || !predicate.test(lv2)) continue;
                list.add(lv2);
            }
        }
    }

    public <T extends Entity> void getEntities(Class<? extends T> arg, Box arg2, List<T> list, @Nullable Predicate<? super T> predicate) {
        int i = MathHelper.floor((arg2.minY - 2.0) / 16.0);
        int j = MathHelper.floor((arg2.maxY + 2.0) / 16.0);
        i = MathHelper.clamp(i, 0, this.entitySections.length - 1);
        j = MathHelper.clamp(j, 0, this.entitySections.length - 1);
        for (int k = i; k <= j; ++k) {
            for (Entity lv : this.entitySections[k].getAllOfType(arg)) {
                if (!lv.getBoundingBox().intersects(arg2) || predicate != null && !predicate.test(lv)) continue;
                list.add(lv);
            }
        }
    }

    public boolean isEmpty() {
        return false;
    }

    @Override
    public ChunkPos getPos() {
        return this.pos;
    }

    @Environment(value=EnvType.CLIENT)
    public void loadFromPacket(@Nullable BiomeArray arg2, PacketByteBuf arg22, CompoundTag arg3, int i) {
        boolean bl = arg2 != null;
        Predicate<BlockPos> predicate = bl ? arg -> true : arg -> (i & 1 << (arg.getY() >> 4)) != 0;
        Sets.newHashSet(this.blockEntities.keySet()).stream().filter(predicate).forEach(this.world::removeBlockEntity);
        for (int j = 0; j < this.sections.length; ++j) {
            ChunkSection lv = this.sections[j];
            if ((i & 1 << j) == 0) {
                if (!bl || lv == EMPTY_SECTION) continue;
                this.sections[j] = EMPTY_SECTION;
                continue;
            }
            if (lv == EMPTY_SECTION) {
                this.sections[j] = lv = new ChunkSection(j << 4);
            }
            lv.fromPacket(arg22);
        }
        if (arg2 != null) {
            this.biomeArray = arg2;
        }
        for (Heightmap.Type lv2 : Heightmap.Type.values()) {
            String string = lv2.getName();
            if (!arg3.contains(string, 12)) continue;
            this.setHeightmap(lv2, arg3.getLongArray(string));
        }
        for (BlockEntity lv3 : this.blockEntities.values()) {
            lv3.resetBlock();
        }
    }

    @Override
    public BiomeArray getBiomeArray() {
        return this.biomeArray;
    }

    public void setLoadedToWorld(boolean bl) {
        this.loadedToWorld = bl;
    }

    public World getWorld() {
        return this.world;
    }

    @Override
    public Collection<Map.Entry<Heightmap.Type, Heightmap>> getHeightmaps() {
        return Collections.unmodifiableSet(this.heightmaps.entrySet());
    }

    public Map<BlockPos, BlockEntity> getBlockEntities() {
        return this.blockEntities;
    }

    public TypeFilterableList<Entity>[] getEntitySectionArray() {
        return this.entitySections;
    }

    @Override
    public CompoundTag getBlockEntityTagAt(BlockPos arg) {
        return this.pendingBlockEntityTags.get(arg);
    }

    @Override
    public Stream<BlockPos> getLightSourcesStream() {
        return StreamSupport.stream(BlockPos.iterate(this.pos.getStartX(), 0, this.pos.getStartZ(), this.pos.getEndX(), 255, this.pos.getEndZ()).spliterator(), false).filter(arg -> this.getBlockState((BlockPos)arg).getLuminance() != 0);
    }

    @Override
    public TickScheduler<Block> getBlockTickScheduler() {
        return this.blockTickScheduler;
    }

    @Override
    public TickScheduler<Fluid> getFluidTickScheduler() {
        return this.fluidTickScheduler;
    }

    @Override
    public void setShouldSave(boolean bl) {
        this.shouldSave = bl;
    }

    @Override
    public boolean needsSaving() {
        return this.shouldSave || this.unsaved && this.world.getTime() != this.lastSaveTime;
    }

    public void setUnsaved(boolean bl) {
        this.unsaved = bl;
    }

    @Override
    public void setLastSaveTime(long l) {
        this.lastSaveTime = l;
    }

    @Override
    @Nullable
    public StructureStart getStructureStart(String string) {
        return this.structureStarts.get(string);
    }

    @Override
    public void setStructureStart(String string, StructureStart arg) {
        this.structureStarts.put(string, arg);
    }

    @Override
    public Map<String, StructureStart> getStructureStarts() {
        return this.structureStarts;
    }

    @Override
    public void setStructureStarts(Map<String, StructureStart> map) {
        this.structureStarts.clear();
        this.structureStarts.putAll(map);
    }

    @Override
    public LongSet getStructureReferences(String string2) {
        return this.structureReferences.computeIfAbsent(string2, string -> new LongOpenHashSet());
    }

    @Override
    public void addStructureReference(String string2, long l) {
        this.structureReferences.computeIfAbsent(string2, string -> new LongOpenHashSet()).add(l);
    }

    @Override
    public Map<String, LongSet> getStructureReferences() {
        return this.structureReferences;
    }

    @Override
    public void setStructureReferences(Map<String, LongSet> map) {
        this.structureReferences.clear();
        this.structureReferences.putAll(map);
    }

    @Override
    public long getInhabitedTime() {
        return this.inhabitedTime;
    }

    @Override
    public void setInhabitedTime(long l) {
        this.inhabitedTime = l;
    }

    public void runPostProcessing() {
        ChunkPos lv = this.getPos();
        for (int i = 0; i < this.postProcessingLists.length; ++i) {
            if (this.postProcessingLists[i] == null) continue;
            for (Short lv2 : this.postProcessingLists[i]) {
                BlockPos lv3 = ProtoChunk.joinBlockPos(lv2, i, lv);
                BlockState lv4 = this.getBlockState(lv3);
                BlockState lv5 = Block.postProcessState(lv4, this.world, lv3);
                this.world.setBlockState(lv3, lv5, 20);
            }
            this.postProcessingLists[i].clear();
        }
        this.disableTickSchedulers();
        for (BlockPos lv6 : Sets.newHashSet(this.pendingBlockEntityTags.keySet())) {
            this.getBlockEntity(lv6);
        }
        this.pendingBlockEntityTags.clear();
        this.upgradeData.upgrade(this);
    }

    @Nullable
    private BlockEntity loadBlockEntity(BlockPos arg, CompoundTag arg2) {
        BlockEntity lv5;
        BlockState lv = this.getBlockState(arg);
        if ("DUMMY".equals(arg2.getString("id"))) {
            Block lv2 = lv.getBlock();
            if (lv2 instanceof BlockEntityProvider) {
                BlockEntity lv3 = ((BlockEntityProvider)((Object)lv2)).createBlockEntity(this.world);
            } else {
                Object lv4 = null;
                LOGGER.warn("Tried to load a DUMMY block entity @ {} but found not block entity block {} at location", (Object)arg, (Object)lv);
            }
        } else {
            lv5 = BlockEntity.createFromTag(lv, arg2);
        }
        if (lv5 != null) {
            lv5.setLocation(this.world, arg);
            this.addBlockEntity(lv5);
        } else {
            LOGGER.warn("Tried to load a block entity for block {} but failed at location {}", (Object)lv, (Object)arg);
        }
        return lv5;
    }

    @Override
    public UpgradeData getUpgradeData() {
        return this.upgradeData;
    }

    @Override
    public ShortList[] getPostProcessingLists() {
        return this.postProcessingLists;
    }

    public void disableTickSchedulers() {
        if (this.blockTickScheduler instanceof ChunkTickScheduler) {
            ((ChunkTickScheduler)this.blockTickScheduler).tick(this.world.getBlockTickScheduler(), arg -> this.getBlockState((BlockPos)arg).getBlock());
            this.blockTickScheduler = DummyClientTickScheduler.get();
        } else if (this.blockTickScheduler instanceof SimpleTickScheduler) {
            ((SimpleTickScheduler)this.blockTickScheduler).scheduleTo(this.world.getBlockTickScheduler());
            this.blockTickScheduler = DummyClientTickScheduler.get();
        }
        if (this.fluidTickScheduler instanceof ChunkTickScheduler) {
            ((ChunkTickScheduler)this.fluidTickScheduler).tick(this.world.getFluidTickScheduler(), arg -> this.getFluidState((BlockPos)arg).getFluid());
            this.fluidTickScheduler = DummyClientTickScheduler.get();
        } else if (this.fluidTickScheduler instanceof SimpleTickScheduler) {
            ((SimpleTickScheduler)this.fluidTickScheduler).scheduleTo(this.world.getFluidTickScheduler());
            this.fluidTickScheduler = DummyClientTickScheduler.get();
        }
    }

    public void enableTickSchedulers(ServerWorld arg) {
        if (this.blockTickScheduler == DummyClientTickScheduler.get()) {
            this.blockTickScheduler = new SimpleTickScheduler<Block>(Registry.BLOCK::getId, ((ServerTickScheduler)arg.getBlockTickScheduler()).getScheduledTicksInChunk(this.pos, true, false), arg.getTime());
            this.setShouldSave(true);
        }
        if (this.fluidTickScheduler == DummyClientTickScheduler.get()) {
            this.fluidTickScheduler = new SimpleTickScheduler<Fluid>(Registry.FLUID::getId, ((ServerTickScheduler)arg.getFluidTickScheduler()).getScheduledTicksInChunk(this.pos, true, false), arg.getTime());
            this.setShouldSave(true);
        }
    }

    @Override
    public ChunkStatus getStatus() {
        return ChunkStatus.FULL;
    }

    public ChunkHolder.LevelType getLevelType() {
        if (this.levelTypeProvider == null) {
            return ChunkHolder.LevelType.BORDER;
        }
        return this.levelTypeProvider.get();
    }

    public void setLevelTypeProvider(Supplier<ChunkHolder.LevelType> supplier) {
        this.levelTypeProvider = supplier;
    }

    @Override
    public boolean isLightOn() {
        return this.lightOn;
    }

    @Override
    public void setLightOn(boolean bl) {
        this.lightOn = bl;
        this.setShouldSave(true);
    }

    public static enum CreationType {
        IMMEDIATE,
        QUEUED,
        CHECK;

    }
}

