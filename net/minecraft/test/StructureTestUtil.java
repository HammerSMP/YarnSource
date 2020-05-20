/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  javax.annotation.Nullable
 *  org.apache.commons.io.IOUtils
 */
package net.minecraft.test;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.command.arguments.BlockStateArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.server.world.ServerTickScheduler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import org.apache.commons.io.IOUtils;

public class StructureTestUtil {
    public static String testStructuresDirectoryName = "gameteststructures";

    public static Box getStructureBoundingBox(StructureBlockBlockEntity arg) {
        BlockPos lv = arg.getPos().add(arg.getOffset());
        return new Box(lv, lv.add(arg.getSize()));
    }

    public static void placeStartButton(BlockPos arg, ServerWorld arg2) {
        arg2.setBlockState(arg, Blocks.COMMAND_BLOCK.getDefaultState());
        CommandBlockBlockEntity lv = (CommandBlockBlockEntity)arg2.getBlockEntity(arg);
        lv.getCommandExecutor().setCommand("test runthis");
        arg2.setBlockState(arg.add(0, 0, -1), Blocks.STONE_BUTTON.getDefaultState());
    }

    public static void createTestArea(String string, BlockPos arg, BlockPos arg2, int i, ServerWorld arg3) {
        BlockBox lv = StructureTestUtil.createArea(arg, arg2, i);
        StructureTestUtil.clearArea(lv, arg.getY(), arg3);
        arg3.setBlockState(arg, Blocks.STRUCTURE_BLOCK.getDefaultState());
        StructureBlockBlockEntity lv2 = (StructureBlockBlockEntity)arg3.getBlockEntity(arg);
        lv2.setIgnoreEntities(false);
        lv2.setStructureName(new Identifier(string));
        lv2.setSize(arg2);
        lv2.setMode(StructureBlockMode.SAVE);
        lv2.setShowBoundingBox(true);
    }

    public static StructureBlockBlockEntity method_22250(String string, BlockPos arg, int i, ServerWorld arg2, boolean bl) {
        BlockBox lv = StructureTestUtil.createArea(arg, StructureTestUtil.createStructure(string, arg2).getSize(), i);
        StructureTestUtil.forceLoadNearbyChunks(arg, arg2);
        StructureTestUtil.clearArea(lv, arg.getY(), arg2);
        StructureBlockBlockEntity lv2 = StructureTestUtil.placeStructure(string, arg, arg2, bl);
        ((ServerTickScheduler)arg2.getBlockTickScheduler()).getScheduledTicks(lv, true, false);
        arg2.clearUpdatesInArea(lv);
        return lv2;
    }

    private static void forceLoadNearbyChunks(BlockPos arg, ServerWorld arg2) {
        ChunkPos lv = new ChunkPos(arg);
        for (int i = -1; i < 4; ++i) {
            for (int j = -1; j < 4; ++j) {
                int k = lv.x + i;
                int l = lv.z + j;
                arg2.setChunkForced(k, l, true);
            }
        }
    }

    public static void clearArea(BlockBox arg3, int i, ServerWorld arg22) {
        BlockPos.stream(arg3).forEach(arg2 -> StructureTestUtil.method_22368(i, arg2, arg22));
        ((ServerTickScheduler)arg22.getBlockTickScheduler()).getScheduledTicks(arg3, true, false);
        arg22.clearUpdatesInArea(arg3);
        Box lv = new Box(arg3.minX, arg3.minY, arg3.minZ, arg3.maxX, arg3.maxY, arg3.maxZ);
        List<Entity> list = arg22.getEntities(Entity.class, lv, arg -> !(arg instanceof PlayerEntity));
        list.forEach(Entity::remove);
    }

    public static BlockBox createArea(BlockPos arg, BlockPos arg2, int i) {
        BlockPos lv = arg.add(-i, -3, -i);
        BlockPos lv2 = arg.add(arg2).add(i - 1, 30, i - 1);
        return BlockBox.create(lv.getX(), lv.getY(), lv.getZ(), lv2.getX(), lv2.getY(), lv2.getZ());
    }

    public static Optional<BlockPos> findContainingStructureBlock(BlockPos arg, int i, ServerWorld arg2) {
        return StructureTestUtil.findStructureBlocks(arg, i, arg2).stream().filter(arg3 -> StructureTestUtil.isInStructureBounds(arg3, arg, arg2)).findFirst();
    }

    @Nullable
    public static BlockPos findNearestStructureBlock(BlockPos arg, int i, ServerWorld arg22) {
        Comparator<BlockPos> comparator = Comparator.comparingInt(arg2 -> arg2.getManhattanDistance(arg));
        Collection<BlockPos> collection = StructureTestUtil.findStructureBlocks(arg, i, arg22);
        Optional<BlockPos> optional = collection.stream().min(comparator);
        return optional.orElse(null);
    }

    public static Collection<BlockPos> findStructureBlocks(BlockPos arg, int i, ServerWorld arg2) {
        ArrayList collection = Lists.newArrayList();
        Box lv = new Box(arg);
        lv = lv.expand(i);
        for (int j = (int)lv.minX; j <= (int)lv.maxX; ++j) {
            for (int k = (int)lv.minY; k <= (int)lv.maxY; ++k) {
                for (int l = (int)lv.minZ; l <= (int)lv.maxZ; ++l) {
                    BlockPos lv2 = new BlockPos(j, k, l);
                    BlockState lv3 = arg2.getBlockState(lv2);
                    if (!lv3.isOf(Blocks.STRUCTURE_BLOCK)) continue;
                    collection.add(lv2);
                }
            }
        }
        return collection;
    }

    private static Structure createStructure(String string, ServerWorld arg) {
        StructureManager lv = arg.getStructureManager();
        Structure lv2 = lv.getStructure(new Identifier(string));
        if (lv2 != null) {
            return lv2;
        }
        String string2 = string + ".snbt";
        Path path = Paths.get(testStructuresDirectoryName, string2);
        CompoundTag lv3 = StructureTestUtil.loadSnbt(path);
        if (lv3 == null) {
            throw new RuntimeException("Could not find structure file " + path + ", and the structure is not available in the world structures either.");
        }
        return lv.createStructure(lv3);
    }

    private static StructureBlockBlockEntity placeStructure(String string, BlockPos arg, ServerWorld arg2, boolean bl) {
        arg2.setBlockState(arg, Blocks.STRUCTURE_BLOCK.getDefaultState());
        StructureBlockBlockEntity lv = (StructureBlockBlockEntity)arg2.getBlockEntity(arg);
        lv.setMode(StructureBlockMode.LOAD);
        lv.setIgnoreEntities(false);
        lv.setStructureName(new Identifier(string));
        lv.loadStructure(bl);
        if (lv.getSize() != BlockPos.ORIGIN) {
            return lv;
        }
        Structure lv2 = StructureTestUtil.createStructure(string, arg2);
        lv.place(bl, lv2);
        if (lv.getSize() == BlockPos.ORIGIN) {
            throw new RuntimeException("Failed to load structure " + string);
        }
        return lv;
    }

    @Nullable
    private static CompoundTag loadSnbt(Path path) {
        try {
            BufferedReader bufferedReader = Files.newBufferedReader(path);
            String string = IOUtils.toString((Reader)bufferedReader);
            return StringNbtReader.parse(string);
        }
        catch (IOException iOException) {
            return null;
        }
        catch (CommandSyntaxException commandSyntaxException) {
            throw new RuntimeException("Error while trying to load structure " + path, commandSyntaxException);
        }
    }

    private static void method_22368(int i, BlockPos arg, ServerWorld arg2) {
        BlockState lv3;
        FlatChunkGeneratorConfig lv = FlatChunkGeneratorConfig.getDefaultConfig();
        BlockState[] lvs = lv.getLayerBlocks();
        if (arg.getY() < i) {
            BlockState lv2 = lvs[arg.getY() - 1];
        } else {
            lv3 = Blocks.AIR.getDefaultState();
        }
        BlockStateArgument lv4 = new BlockStateArgument(lv3, Collections.emptySet(), null);
        lv4.setBlockState(arg2, arg, 2);
        arg2.updateNeighbors(arg, lv3.getBlock());
    }

    private static boolean isInStructureBounds(BlockPos arg, BlockPos arg2, ServerWorld arg3) {
        StructureBlockBlockEntity lv = (StructureBlockBlockEntity)arg3.getBlockEntity(arg);
        Box lv2 = StructureTestUtil.getStructureBoundingBox(lv);
        return lv2.contains(Vec3d.of(arg2));
    }
}

