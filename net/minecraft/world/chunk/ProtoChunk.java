/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.shorts.ShortList
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkTickScheduler;
import net.minecraft.world.Heightmap;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.GenerationStep;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProtoChunk
implements Chunk {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ChunkPos pos;
    private volatile boolean shouldSave;
    @Nullable
    private BiomeArray biomes;
    @Nullable
    private volatile LightingProvider lightingProvider;
    private final Map<Heightmap.Type, Heightmap> heightmaps = Maps.newEnumMap(Heightmap.Type.class);
    private volatile ChunkStatus status = ChunkStatus.EMPTY;
    private final Map<BlockPos, BlockEntity> blockEntities = Maps.newHashMap();
    private final Map<BlockPos, CompoundTag> blockEntityTags = Maps.newHashMap();
    private final ChunkSection[] sections = new ChunkSection[16];
    private final List<CompoundTag> entities = Lists.newArrayList();
    private final List<BlockPos> lightSources = Lists.newArrayList();
    private final ShortList[] postProcessingLists = new ShortList[16];
    private final Map<String, StructureStart> structureStarts = Maps.newHashMap();
    private final Map<String, LongSet> structureReferences = Maps.newHashMap();
    private final UpgradeData upgradeData;
    private final ChunkTickScheduler<Block> blockTickScheduler;
    private final ChunkTickScheduler<Fluid> fluidTickScheduler;
    private long inhabitedTime;
    private final Map<GenerationStep.Carver, BitSet> carvingMasks = Maps.newHashMap();
    private volatile boolean lightOn;

    public ProtoChunk(ChunkPos arg2, UpgradeData arg22) {
        this(arg2, arg22, null, new ChunkTickScheduler<Block>(arg -> arg == null || arg.getDefaultState().isAir(), arg2), new ChunkTickScheduler<Fluid>(arg -> arg == null || arg == Fluids.EMPTY, arg2));
    }

    public ProtoChunk(ChunkPos arg, UpgradeData arg2, @Nullable ChunkSection[] args, ChunkTickScheduler<Block> arg3, ChunkTickScheduler<Fluid> arg4) {
        this.pos = arg;
        this.upgradeData = arg2;
        this.blockTickScheduler = arg3;
        this.fluidTickScheduler = arg4;
        if (args != null) {
            if (this.sections.length == args.length) {
                System.arraycopy(args, 0, this.sections, 0, this.sections.length);
            } else {
                LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", (Object)args.length, (Object)this.sections.length);
            }
        }
    }

    @Override
    public BlockState getBlockState(BlockPos arg) {
        int i = arg.getY();
        if (World.isHeightInvalid(i)) {
            return Blocks.VOID_AIR.getDefaultState();
        }
        ChunkSection lv = this.getSectionArray()[i >> 4];
        if (ChunkSection.isEmpty(lv)) {
            return Blocks.AIR.getDefaultState();
        }
        return lv.getBlockState(arg.getX() & 0xF, i & 0xF, arg.getZ() & 0xF);
    }

    @Override
    public FluidState getFluidState(BlockPos arg) {
        int i = arg.getY();
        if (World.isHeightInvalid(i)) {
            return Fluids.EMPTY.getDefaultState();
        }
        ChunkSection lv = this.getSectionArray()[i >> 4];
        if (ChunkSection.isEmpty(lv)) {
            return Fluids.EMPTY.getDefaultState();
        }
        return lv.getFluidState(arg.getX() & 0xF, i & 0xF, arg.getZ() & 0xF);
    }

    @Override
    public Stream<BlockPos> getLightSourcesStream() {
        return this.lightSources.stream();
    }

    public ShortList[] getLightSourcesBySection() {
        ShortList[] shortLists = new ShortList[16];
        for (BlockPos lv : this.lightSources) {
            Chunk.getList(shortLists, lv.getY() >> 4).add(ProtoChunk.getPackedSectionRelative(lv));
        }
        return shortLists;
    }

    public void addLightSource(short s, int i) {
        this.addLightSource(ProtoChunk.joinBlockPos(s, i, this.pos));
    }

    public void addLightSource(BlockPos arg) {
        this.lightSources.add(arg.toImmutable());
    }

    @Override
    @Nullable
    public BlockState setBlockState(BlockPos arg, BlockState arg2, boolean bl) {
        int i = arg.getX();
        int j = arg.getY();
        int k = arg.getZ();
        if (j < 0 || j >= 256) {
            return Blocks.VOID_AIR.getDefaultState();
        }
        if (this.sections[j >> 4] == WorldChunk.EMPTY_SECTION && arg2.isOf(Blocks.AIR)) {
            return arg2;
        }
        if (arg2.getLuminance() > 0) {
            this.lightSources.add(new BlockPos((i & 0xF) + this.getPos().getStartX(), j, (k & 0xF) + this.getPos().getStartZ()));
        }
        ChunkSection lv = this.getSection(j >> 4);
        BlockState lv2 = lv.setBlockState(i & 0xF, j & 0xF, k & 0xF, arg2);
        if (this.status.isAtLeast(ChunkStatus.FEATURES) && arg2 != lv2 && (arg2.getOpacity(this, arg) != lv2.getOpacity(this, arg) || arg2.getLuminance() != lv2.getLuminance() || arg2.hasSidedTransparency() || lv2.hasSidedTransparency())) {
            LightingProvider lv3 = this.getLightingProvider();
            lv3.checkBlock(arg);
        }
        EnumSet<Heightmap.Type> enumSet = this.getStatus().getHeightmapTypes();
        EnumSet<Heightmap.Type> enumSet2 = null;
        for (Heightmap.Type lv4 : enumSet) {
            Heightmap lv5 = this.heightmaps.get((Object)lv4);
            if (lv5 != null) continue;
            if (enumSet2 == null) {
                enumSet2 = EnumSet.noneOf(Heightmap.Type.class);
            }
            enumSet2.add(lv4);
        }
        if (enumSet2 != null) {
            Heightmap.populateHeightmaps(this, enumSet2);
        }
        for (Heightmap.Type lv6 : enumSet) {
            this.heightmaps.get((Object)lv6).trackUpdate(i & 0xF, j, k & 0xF, arg2);
        }
        return lv2;
    }

    public ChunkSection getSection(int i) {
        if (this.sections[i] == WorldChunk.EMPTY_SECTION) {
            this.sections[i] = new ChunkSection(i << 4);
        }
        return this.sections[i];
    }

    @Override
    public void setBlockEntity(BlockPos arg, BlockEntity arg2) {
        arg2.setPos(arg);
        this.blockEntities.put(arg, arg2);
    }

    @Override
    public Set<BlockPos> getBlockEntityPositions() {
        HashSet set = Sets.newHashSet(this.blockEntityTags.keySet());
        set.addAll(this.blockEntities.keySet());
        return set;
    }

    @Override
    @Nullable
    public BlockEntity getBlockEntity(BlockPos arg) {
        return this.blockEntities.get(arg);
    }

    public Map<BlockPos, BlockEntity> getBlockEntities() {
        return this.blockEntities;
    }

    public void addEntity(CompoundTag arg) {
        this.entities.add(arg);
    }

    @Override
    public void addEntity(Entity arg) {
        if (arg.hasVehicle()) {
            return;
        }
        CompoundTag lv = new CompoundTag();
        arg.saveToTag(lv);
        this.addEntity(lv);
    }

    public List<CompoundTag> getEntities() {
        return this.entities;
    }

    public void setBiomes(BiomeArray arg) {
        this.biomes = arg;
    }

    @Override
    @Nullable
    public BiomeArray getBiomeArray() {
        return this.biomes;
    }

    @Override
    public void setShouldSave(boolean bl) {
        this.shouldSave = bl;
    }

    @Override
    public boolean needsSaving() {
        return this.shouldSave;
    }

    @Override
    public ChunkStatus getStatus() {
        return this.status;
    }

    public void setStatus(ChunkStatus arg) {
        this.status = arg;
        this.setShouldSave(true);
    }

    @Override
    public ChunkSection[] getSectionArray() {
        return this.sections;
    }

    @Nullable
    public LightingProvider getLightingProvider() {
        return this.lightingProvider;
    }

    @Override
    public Collection<Map.Entry<Heightmap.Type, Heightmap>> getHeightmaps() {
        return Collections.unmodifiableSet(this.heightmaps.entrySet());
    }

    @Override
    public void setHeightmap(Heightmap.Type arg, long[] ls) {
        this.getHeightmap(arg).setTo(ls);
    }

    @Override
    public Heightmap getHeightmap(Heightmap.Type arg2) {
        return this.heightmaps.computeIfAbsent(arg2, arg -> new Heightmap(this, (Heightmap.Type)((Object)arg)));
    }

    @Override
    public int sampleHeightmap(Heightmap.Type arg, int i, int j) {
        Heightmap lv = this.heightmaps.get((Object)arg);
        if (lv == null) {
            Heightmap.populateHeightmaps(this, EnumSet.of(arg));
            lv = this.heightmaps.get((Object)arg);
        }
        return lv.get(i & 0xF, j & 0xF) - 1;
    }

    @Override
    public ChunkPos getPos() {
        return this.pos;
    }

    @Override
    public void setLastSaveTime(long l) {
    }

    @Override
    @Nullable
    public StructureStart getStructureStart(String string) {
        return this.structureStarts.get(string);
    }

    @Override
    public void setStructureStart(String string, StructureStart arg) {
        this.structureStarts.put(string, arg);
        this.shouldSave = true;
    }

    @Override
    public Map<String, StructureStart> getStructureStarts() {
        return Collections.unmodifiableMap(this.structureStarts);
    }

    @Override
    public void setStructureStarts(Map<String, StructureStart> map) {
        this.structureStarts.clear();
        this.structureStarts.putAll(map);
        this.shouldSave = true;
    }

    @Override
    public LongSet getStructureReferences(String string2) {
        return this.structureReferences.computeIfAbsent(string2, string -> new LongOpenHashSet());
    }

    @Override
    public void addStructureReference(String string2, long l) {
        this.structureReferences.computeIfAbsent(string2, string -> new LongOpenHashSet()).add(l);
        this.shouldSave = true;
    }

    @Override
    public Map<String, LongSet> getStructureReferences() {
        return Collections.unmodifiableMap(this.structureReferences);
    }

    @Override
    public void setStructureReferences(Map<String, LongSet> map) {
        this.structureReferences.clear();
        this.structureReferences.putAll(map);
        this.shouldSave = true;
    }

    public static short getPackedSectionRelative(BlockPos arg) {
        int i = arg.getX();
        int j = arg.getY();
        int k = arg.getZ();
        int l = i & 0xF;
        int m = j & 0xF;
        int n = k & 0xF;
        return (short)(l | m << 4 | n << 8);
    }

    public static BlockPos joinBlockPos(short s, int i, ChunkPos arg) {
        int j = (s & 0xF) + (arg.x << 4);
        int k = (s >>> 4 & 0xF) + (i << 4);
        int l = (s >>> 8 & 0xF) + (arg.z << 4);
        return new BlockPos(j, k, l);
    }

    @Override
    public void markBlockForPostProcessing(BlockPos arg) {
        if (!World.isHeightInvalid(arg)) {
            Chunk.getList(this.postProcessingLists, arg.getY() >> 4).add(ProtoChunk.getPackedSectionRelative(arg));
        }
    }

    @Override
    public ShortList[] getPostProcessingLists() {
        return this.postProcessingLists;
    }

    @Override
    public void markBlockForPostProcessing(short s, int i) {
        Chunk.getList(this.postProcessingLists, i).add(s);
    }

    public ChunkTickScheduler<Block> getBlockTickScheduler() {
        return this.blockTickScheduler;
    }

    public ChunkTickScheduler<Fluid> getFluidTickScheduler() {
        return this.fluidTickScheduler;
    }

    @Override
    public UpgradeData getUpgradeData() {
        return this.upgradeData;
    }

    @Override
    public void setInhabitedTime(long l) {
        this.inhabitedTime = l;
    }

    @Override
    public long getInhabitedTime() {
        return this.inhabitedTime;
    }

    @Override
    public void addPendingBlockEntityTag(CompoundTag arg) {
        this.blockEntityTags.put(new BlockPos(arg.getInt("x"), arg.getInt("y"), arg.getInt("z")), arg);
    }

    public Map<BlockPos, CompoundTag> getBlockEntityTags() {
        return Collections.unmodifiableMap(this.blockEntityTags);
    }

    @Override
    public CompoundTag getBlockEntityTagAt(BlockPos arg) {
        return this.blockEntityTags.get(arg);
    }

    @Override
    @Nullable
    public CompoundTag method_20598(BlockPos arg) {
        BlockEntity lv = this.getBlockEntity(arg);
        if (lv != null) {
            return lv.toTag(new CompoundTag());
        }
        return this.blockEntityTags.get(arg);
    }

    @Override
    public void removeBlockEntity(BlockPos arg) {
        this.blockEntities.remove(arg);
        this.blockEntityTags.remove(arg);
    }

    @Override
    public BitSet getCarvingMask(GenerationStep.Carver arg2) {
        return this.carvingMasks.computeIfAbsent(arg2, arg -> new BitSet(65536));
    }

    public void setCarvingMask(GenerationStep.Carver arg, BitSet bitSet) {
        this.carvingMasks.put(arg, bitSet);
    }

    public void setLightingProvider(LightingProvider arg) {
        this.lightingProvider = arg;
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

    public /* synthetic */ TickScheduler getFluidTickScheduler() {
        return this.getFluidTickScheduler();
    }

    public /* synthetic */ TickScheduler getBlockTickScheduler() {
        return this.getBlockTickScheduler();
    }
}

