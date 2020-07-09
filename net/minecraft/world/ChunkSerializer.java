/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.shorts.ShortList
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerTickScheduler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.SimpleTickScheduler;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkTickScheduler;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LightType;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.ReadOnlyChunk;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.poi.PointOfInterestStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkSerializer {
    private static final Logger LOGGER = LogManager.getLogger();

    public static ProtoChunk deserialize(ServerWorld arg3, StructureManager arg22, PointOfInterestStorage arg32, ChunkPos arg4, CompoundTag arg5) {
        ProtoChunk lv21;
        ChunkGenerator lv = arg3.getChunkManager().getChunkGenerator();
        BiomeSource lv2 = lv.getBiomeSource();
        CompoundTag lv3 = arg5.getCompound("Level");
        ChunkPos lv4 = new ChunkPos(lv3.getInt("xPos"), lv3.getInt("zPos"));
        if (!Objects.equals(arg4, lv4)) {
            LOGGER.error("Chunk file at {} is in the wrong location; relocating. (Expected {}, got {})", (Object)arg4, (Object)arg4, (Object)lv4);
        }
        BiomeArray lv5 = new BiomeArray(arg3.method_30349().method_30530(Registry.BIOME_KEY), arg4, lv2, lv3.contains("Biomes", 11) ? lv3.getIntArray("Biomes") : null);
        UpgradeData lv6 = lv3.contains("UpgradeData", 10) ? new UpgradeData(lv3.getCompound("UpgradeData")) : UpgradeData.NO_UPGRADE_DATA;
        ChunkTickScheduler<Block> lv7 = new ChunkTickScheduler<Block>(arg -> arg == null || arg.getDefaultState().isAir(), arg4, lv3.getList("ToBeTicked", 9));
        ChunkTickScheduler<Fluid> lv8 = new ChunkTickScheduler<Fluid>(arg -> arg == null || arg == Fluids.EMPTY, arg4, lv3.getList("LiquidsToBeTicked", 9));
        boolean bl = lv3.getBoolean("isLightOn");
        ListTag lv9 = lv3.getList("Sections", 10);
        int i = 16;
        ChunkSection[] lvs = new ChunkSection[16];
        boolean bl2 = arg3.getDimension().hasSkyLight();
        ServerChunkManager lv10 = arg3.getChunkManager();
        LightingProvider lv11 = ((ChunkManager)lv10).getLightingProvider();
        if (bl) {
            lv11.setRetainData(arg4, true);
        }
        for (int j = 0; j < lv9.size(); ++j) {
            CompoundTag lv12 = lv9.getCompound(j);
            byte k = lv12.getByte("Y");
            if (lv12.contains("Palette", 9) && lv12.contains("BlockStates", 12)) {
                ChunkSection lv13 = new ChunkSection(k << 4);
                lv13.getContainer().read(lv12.getList("Palette", 10), lv12.getLongArray("BlockStates"));
                lv13.calculateCounts();
                if (!lv13.isEmpty()) {
                    lvs[k] = lv13;
                }
                arg32.initForPalette(arg4, lv13);
            }
            if (!bl) continue;
            if (lv12.contains("BlockLight", 7)) {
                lv11.queueData(LightType.BLOCK, ChunkSectionPos.from(arg4, k), new ChunkNibbleArray(lv12.getByteArray("BlockLight")), true);
            }
            if (!bl2 || !lv12.contains("SkyLight", 7)) continue;
            lv11.queueData(LightType.SKY, ChunkSectionPos.from(arg4, k), new ChunkNibbleArray(lv12.getByteArray("SkyLight")), true);
        }
        long l = lv3.getLong("InhabitedTime");
        ChunkStatus.ChunkType lv14 = ChunkSerializer.getChunkType(arg5);
        if (lv14 == ChunkStatus.ChunkType.LEVELCHUNK) {
            ChunkTickScheduler<Fluid> lv18;
            ChunkTickScheduler<Block> lv16;
            if (lv3.contains("TileTicks", 9)) {
                SimpleTickScheduler<Block> lv15 = SimpleTickScheduler.fromNbt(lv3.getList("TileTicks", 10), Registry.BLOCK::getId, Registry.BLOCK::get);
            } else {
                lv16 = lv7;
            }
            if (lv3.contains("LiquidTicks", 9)) {
                SimpleTickScheduler<Fluid> lv17 = SimpleTickScheduler.fromNbt(lv3.getList("LiquidTicks", 10), Registry.FLUID::getId, Registry.FLUID::get);
            } else {
                lv18 = lv8;
            }
            WorldChunk lv19 = new WorldChunk(arg3.getWorld(), arg4, lv5, lv6, lv16, lv18, l, lvs, arg2 -> ChunkSerializer.writeEntities(lv3, arg2));
        } else {
            ProtoChunk lv20 = new ProtoChunk(arg4, lv6, lvs, lv7, lv8);
            lv20.setBiomes(lv5);
            lv21 = lv20;
            lv21.setInhabitedTime(l);
            lv20.setStatus(ChunkStatus.get(lv3.getString("Status")));
            if (lv21.getStatus().isAtLeast(ChunkStatus.FEATURES)) {
                lv20.setLightingProvider(lv11);
            }
            if (!bl && lv21.getStatus().isAtLeast(ChunkStatus.LIGHT)) {
                for (BlockPos blockPos : BlockPos.iterate(arg4.getStartX(), 0, arg4.getStartZ(), arg4.getEndX(), 255, arg4.getEndZ())) {
                    if (lv21.getBlockState(blockPos).getLuminance() == 0) continue;
                    lv20.addLightSource(blockPos);
                }
            }
        }
        lv21.setLightOn(bl);
        CompoundTag lv23 = lv3.getCompound("Heightmaps");
        EnumSet<Heightmap.Type> enumSet = EnumSet.noneOf(Heightmap.Type.class);
        for (Heightmap.Type lv24 : lv21.getStatus().getHeightmapTypes()) {
            String string = lv24.getName();
            if (lv23.contains(string, 12)) {
                lv21.setHeightmap(lv24, lv23.getLongArray(string));
                continue;
            }
            enumSet.add(lv24);
        }
        Heightmap.populateHeightmaps(lv21, enumSet);
        CompoundTag compoundTag = lv3.getCompound("Structures");
        lv21.setStructureStarts(ChunkSerializer.readStructureStarts(arg22, compoundTag, arg3.getSeed()));
        lv21.setStructureReferences(ChunkSerializer.readStructureReferences(arg4, compoundTag));
        if (lv3.getBoolean("shouldSave")) {
            lv21.setShouldSave(true);
        }
        ListTag lv26 = lv3.getList("PostProcessing", 9);
        for (int m = 0; m < lv26.size(); ++m) {
            ListTag lv27 = lv26.getList(m);
            for (int n = 0; n < lv27.size(); ++n) {
                lv21.markBlockForPostProcessing(lv27.getShort(n), m);
            }
        }
        if (lv14 == ChunkStatus.ChunkType.LEVELCHUNK) {
            return new ReadOnlyChunk((WorldChunk)((Object)lv21));
        }
        ProtoChunk lv28 = lv21;
        ListTag lv29 = lv3.getList("Entities", 10);
        for (int o = 0; o < lv29.size(); ++o) {
            lv28.addEntity(lv29.getCompound(o));
        }
        ListTag lv30 = lv3.getList("TileEntities", 10);
        for (int p = 0; p < lv30.size(); ++p) {
            CompoundTag lv31 = lv30.getCompound(p);
            lv21.addPendingBlockEntityTag(lv31);
        }
        ListTag lv32 = lv3.getList("Lights", 9);
        for (int q = 0; q < lv32.size(); ++q) {
            ListTag lv33 = lv32.getList(q);
            for (int r = 0; r < lv33.size(); ++r) {
                lv28.addLightSource(lv33.getShort(r), q);
            }
        }
        CompoundTag lv34 = lv3.getCompound("CarvingMasks");
        for (String string2 : lv34.getKeys()) {
            GenerationStep.Carver lv35 = GenerationStep.Carver.valueOf(string2);
            lv28.setCarvingMask(lv35, BitSet.valueOf(lv34.getByteArray(string2)));
        }
        return lv28;
    }

    public static CompoundTag serialize(ServerWorld arg2, Chunk arg22) {
        BiomeArray lv11;
        ChunkPos lv = arg22.getPos();
        CompoundTag lv2 = new CompoundTag();
        CompoundTag lv3 = new CompoundTag();
        lv2.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
        lv2.put("Level", lv3);
        lv3.putInt("xPos", lv.x);
        lv3.putInt("zPos", lv.z);
        lv3.putLong("LastUpdate", arg2.getTime());
        lv3.putLong("InhabitedTime", arg22.getInhabitedTime());
        lv3.putString("Status", arg22.getStatus().getId());
        UpgradeData lv4 = arg22.getUpgradeData();
        if (!lv4.isDone()) {
            lv3.put("UpgradeData", lv4.toTag());
        }
        ChunkSection[] lvs = arg22.getSectionArray();
        ListTag lv5 = new ListTag();
        ServerLightingProvider lv6 = arg2.getChunkManager().getLightingProvider();
        boolean bl = arg22.isLightOn();
        for (int i = -1; i < 17; ++i) {
            int j = i;
            ChunkSection lv7 = Arrays.stream(lvs).filter(arg -> arg != null && arg.getYOffset() >> 4 == j).findFirst().orElse(WorldChunk.EMPTY_SECTION);
            ChunkNibbleArray lv8 = lv6.get(LightType.BLOCK).getLightArray(ChunkSectionPos.from(lv, j));
            ChunkNibbleArray lv9 = lv6.get(LightType.SKY).getLightArray(ChunkSectionPos.from(lv, j));
            if (lv7 == WorldChunk.EMPTY_SECTION && lv8 == null && lv9 == null) continue;
            GenerationStep.Carver[] lv10 = new CompoundTag();
            lv10.putByte("Y", (byte)(j & 0xFF));
            if (lv7 != WorldChunk.EMPTY_SECTION) {
                lv7.getContainer().write((CompoundTag)lv10, "Palette", "BlockStates");
            }
            if (lv8 != null && !lv8.isUninitialized()) {
                lv10.putByteArray("BlockLight", lv8.asByteArray());
            }
            if (lv9 != null && !lv9.isUninitialized()) {
                lv10.putByteArray("SkyLight", lv9.asByteArray());
            }
            lv5.add(lv10);
        }
        lv3.put("Sections", lv5);
        if (bl) {
            lv3.putBoolean("isLightOn", true);
        }
        if ((lv11 = arg22.getBiomeArray()) != null) {
            lv3.putIntArray("Biomes", lv11.toIntArray());
        }
        ListTag lv12 = new ListTag();
        for (BlockPos lv13 : arg22.getBlockEntityPositions()) {
            CompoundTag lv14 = arg22.getPackedBlockEntityTag(lv13);
            if (lv14 == null) continue;
            lv12.add(lv14);
        }
        lv3.put("TileEntities", lv12);
        ListTag lv15 = new ListTag();
        if (arg22.getStatus().getChunkType() == ChunkStatus.ChunkType.LEVELCHUNK) {
            WorldChunk lv16 = (WorldChunk)arg22;
            lv16.setUnsaved(false);
            for (int k = 0; k < lv16.getEntitySectionArray().length; ++k) {
                for (Entity lv17 : lv16.getEntitySectionArray()[k]) {
                    CompoundTag lv18;
                    if (!lv17.saveToTag(lv18 = new CompoundTag())) continue;
                    lv16.setUnsaved(true);
                    lv15.add(lv18);
                }
            }
        } else {
            ProtoChunk lv19 = (ProtoChunk)arg22;
            lv15.addAll(lv19.getEntities());
            lv3.put("Lights", ChunkSerializer.toNbt(lv19.getLightSourcesBySection()));
            CompoundTag lv20 = new CompoundTag();
            for (GenerationStep.Carver lv21 : GenerationStep.Carver.values()) {
                BitSet bitSet = lv19.getCarvingMask(lv21);
                if (bitSet == null) continue;
                lv20.putByteArray(lv21.toString(), bitSet.toByteArray());
            }
            lv3.put("CarvingMasks", lv20);
        }
        lv3.put("Entities", lv15);
        TickScheduler<Block> lv22 = arg22.getBlockTickScheduler();
        if (lv22 instanceof ChunkTickScheduler) {
            lv3.put("ToBeTicked", ((ChunkTickScheduler)lv22).toNbt());
        } else if (lv22 instanceof SimpleTickScheduler) {
            lv3.put("TileTicks", ((SimpleTickScheduler)lv22).toNbt());
        } else {
            lv3.put("TileTicks", ((ServerTickScheduler)arg2.getBlockTickScheduler()).toTag(lv));
        }
        TickScheduler<Fluid> lv23 = arg22.getFluidTickScheduler();
        if (lv23 instanceof ChunkTickScheduler) {
            lv3.put("LiquidsToBeTicked", ((ChunkTickScheduler)lv23).toNbt());
        } else if (lv23 instanceof SimpleTickScheduler) {
            lv3.put("LiquidTicks", ((SimpleTickScheduler)lv23).toNbt());
        } else {
            lv3.put("LiquidTicks", ((ServerTickScheduler)arg2.getFluidTickScheduler()).toTag(lv));
        }
        lv3.put("PostProcessing", ChunkSerializer.toNbt(arg22.getPostProcessingLists()));
        CompoundTag lv24 = new CompoundTag();
        for (Map.Entry<Heightmap.Type, Heightmap> entry : arg22.getHeightmaps()) {
            if (!arg22.getStatus().getHeightmapTypes().contains(entry.getKey())) continue;
            lv24.put(entry.getKey().getName(), new LongArrayTag(entry.getValue().asLongArray()));
        }
        lv3.put("Heightmaps", lv24);
        lv3.put("Structures", ChunkSerializer.writeStructures(lv, arg22.getStructureStarts(), arg22.getStructureReferences()));
        return lv2;
    }

    public static ChunkStatus.ChunkType getChunkType(@Nullable CompoundTag arg) {
        ChunkStatus lv;
        if (arg != null && (lv = ChunkStatus.get(arg.getCompound("Level").getString("Status"))) != null) {
            return lv.getChunkType();
        }
        return ChunkStatus.ChunkType.PROTOCHUNK;
    }

    private static void writeEntities(CompoundTag arg, WorldChunk arg22) {
        ListTag lv = arg.getList("Entities", 10);
        World lv2 = arg22.getWorld();
        for (int i = 0; i < lv.size(); ++i) {
            CompoundTag lv3 = lv.getCompound(i);
            EntityType.loadEntityWithPassengers(lv3, lv2, arg2 -> {
                arg22.addEntity((Entity)arg2);
                return arg2;
            });
            arg22.setUnsaved(true);
        }
        ListTag lv4 = arg.getList("TileEntities", 10);
        for (int j = 0; j < lv4.size(); ++j) {
            CompoundTag lv5 = lv4.getCompound(j);
            boolean bl = lv5.getBoolean("keepPacked");
            if (bl) {
                arg22.addPendingBlockEntityTag(lv5);
                continue;
            }
            BlockPos lv6 = new BlockPos(lv5.getInt("x"), lv5.getInt("y"), lv5.getInt("z"));
            BlockEntity lv7 = BlockEntity.createFromTag(arg22.getBlockState(lv6), lv5);
            if (lv7 == null) continue;
            arg22.addBlockEntity(lv7);
        }
    }

    private static CompoundTag writeStructures(ChunkPos arg, Map<StructureFeature<?>, StructureStart<?>> map, Map<StructureFeature<?>, LongSet> map2) {
        CompoundTag lv = new CompoundTag();
        CompoundTag lv2 = new CompoundTag();
        for (Map.Entry<StructureFeature<?>, StructureStart<?>> entry : map.entrySet()) {
            lv2.put(entry.getKey().getName(), entry.getValue().toTag(arg.x, arg.z));
        }
        lv.put("Starts", lv2);
        CompoundTag lv3 = new CompoundTag();
        for (Map.Entry<StructureFeature<?>, LongSet> entry2 : map2.entrySet()) {
            lv3.put(entry2.getKey().getName(), new LongArrayTag(entry2.getValue()));
        }
        lv.put("References", lv3);
        return lv;
    }

    private static Map<StructureFeature<?>, StructureStart<?>> readStructureStarts(StructureManager arg, CompoundTag arg2, long l) {
        HashMap map = Maps.newHashMap();
        CompoundTag lv = arg2.getCompound("Starts");
        for (String string : lv.getKeys()) {
            String string2 = string.toLowerCase(Locale.ROOT);
            StructureFeature lv2 = (StructureFeature)StructureFeature.STRUCTURES.get((Object)string2);
            if (lv2 == null) {
                LOGGER.error("Unknown structure start: {}", (Object)string2);
                continue;
            }
            StructureStart<?> lv3 = StructureFeature.method_28660(arg, lv.getCompound(string), l);
            if (lv3 == null) continue;
            map.put(lv2, lv3);
        }
        return map;
    }

    private static Map<StructureFeature<?>, LongSet> readStructureReferences(ChunkPos arg, CompoundTag arg2) {
        HashMap map = Maps.newHashMap();
        CompoundTag lv = arg2.getCompound("References");
        for (String string : lv.getKeys()) {
            map.put(StructureFeature.STRUCTURES.get((Object)string.toLowerCase(Locale.ROOT)), new LongOpenHashSet(Arrays.stream(lv.getLongArray(string)).filter(l -> {
                ChunkPos lv = new ChunkPos(l);
                if (lv.method_24022(arg) > 8) {
                    LOGGER.warn("Found invalid structure reference [ {} @ {} ] for chunk {}.", (Object)string, (Object)lv, (Object)arg);
                    return false;
                }
                return true;
            }).toArray()));
        }
        return map;
    }

    public static ListTag toNbt(ShortList[] shortLists) {
        ListTag lv = new ListTag();
        for (ShortList shortList : shortLists) {
            ListTag lv2 = new ListTag();
            if (shortList != null) {
                for (Short short_ : shortList) {
                    lv2.add(ShortTag.of(short_));
                }
            }
            lv.add(lv2);
        }
        return lv;
    }
}

