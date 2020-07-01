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
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
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

    public static BlockRotation method_29408(int i) {
        switch (i) {
            case 0: {
                return BlockRotation.NONE;
            }
            case 1: {
                return BlockRotation.CLOCKWISE_90;
            }
            case 2: {
                return BlockRotation.CLOCKWISE_180;
            }
            case 3: {
                return BlockRotation.COUNTERCLOCKWISE_90;
            }
        }
        throw new IllegalArgumentException("rotationSteps must be a value from 0-3. Got value " + i);
    }

    public static Box getStructureBoundingBox(StructureBlockBlockEntity arg) {
        BlockPos lv = arg.getPos();
        BlockPos lv2 = lv.add(arg.getSize().add(-1, -1, -1));
        BlockPos lv3 = Structure.transformAround(lv2, BlockMirror.NONE, arg.getRotation(), lv);
        return new Box(lv, lv3);
    }

    public static BlockBox method_29410(StructureBlockBlockEntity arg) {
        BlockPos lv = arg.getPos();
        BlockPos lv2 = lv.add(arg.getSize().add(-1, -1, -1));
        BlockPos lv3 = Structure.transformAround(lv2, BlockMirror.NONE, arg.getRotation(), lv);
        return new BlockBox(lv, lv3);
    }

    public static void placeStartButton(BlockPos arg, BlockPos arg2, BlockRotation arg3, ServerWorld arg4) {
        BlockPos lv = Structure.transformAround(arg.add(arg2), BlockMirror.NONE, arg3, arg);
        arg4.setBlockState(lv, Blocks.COMMAND_BLOCK.getDefaultState());
        CommandBlockBlockEntity lv2 = (CommandBlockBlockEntity)arg4.getBlockEntity(lv);
        lv2.getCommandExecutor().setCommand("test runthis");
        BlockPos lv3 = Structure.transformAround(lv.add(0, 0, -1), BlockMirror.NONE, arg3, lv);
        arg4.setBlockState(lv3, Blocks.STONE_BUTTON.getDefaultState().rotate(arg3));
    }

    public static void createTestArea(String string, BlockPos arg, BlockPos arg2, BlockRotation arg3, ServerWorld arg4) {
        BlockBox lv = StructureTestUtil.method_29409(arg, arg2, arg3);
        StructureTestUtil.clearArea(lv, arg.getY(), arg4);
        arg4.setBlockState(arg, Blocks.STRUCTURE_BLOCK.getDefaultState());
        StructureBlockBlockEntity lv2 = (StructureBlockBlockEntity)arg4.getBlockEntity(arg);
        lv2.setIgnoreEntities(false);
        lv2.setStructureName(new Identifier(string));
        lv2.setSize(arg2);
        lv2.setMode(StructureBlockMode.SAVE);
        lv2.setShowBoundingBox(true);
    }

    /*
     * WARNING - void declaration
     */
    public static StructureBlockBlockEntity method_22250(String string, BlockPos arg, BlockRotation arg2, int i, ServerWorld arg3, boolean bl) {
        void lv7;
        BlockPos lv = StructureTestUtil.createStructure(string, arg3).getSize();
        BlockBox lv2 = StructureTestUtil.method_29409(arg, lv, arg2);
        if (arg2 == BlockRotation.NONE) {
            BlockPos lv3 = arg;
        } else if (arg2 == BlockRotation.CLOCKWISE_90) {
            BlockPos lv4 = arg.add(lv.getZ() - 1, 0, 0);
        } else if (arg2 == BlockRotation.CLOCKWISE_180) {
            BlockPos lv5 = arg.add(lv.getX() - 1, 0, lv.getZ() - 1);
        } else if (arg2 == BlockRotation.COUNTERCLOCKWISE_90) {
            BlockPos lv6 = arg.add(0, 0, lv.getX() - 1);
        } else {
            throw new IllegalArgumentException("Invalid rotation: " + (Object)((Object)arg2));
        }
        StructureTestUtil.forceLoadNearbyChunks(arg, arg3);
        StructureTestUtil.clearArea(lv2, arg.getY(), arg3);
        StructureBlockBlockEntity lv8 = StructureTestUtil.placeStructure(string, (BlockPos)lv7, arg2, arg3, bl);
        ((ServerTickScheduler)arg3.getBlockTickScheduler()).getScheduledTicks(lv2, true, false);
        arg3.clearUpdatesInArea(lv2);
        return lv8;
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
        BlockBox lv = new BlockBox(arg3.minX - 2, arg3.minY - 3, arg3.minZ - 3, arg3.maxX + 3, arg3.maxY + 20, arg3.maxZ + 3);
        BlockPos.stream(lv).forEach(arg2 -> StructureTestUtil.method_22368(i, arg2, arg22));
        ((ServerTickScheduler)arg22.getBlockTickScheduler()).getScheduledTicks(lv, true, false);
        arg22.clearUpdatesInArea(lv);
        Box lv2 = new Box(lv.minX, lv.minY, lv.minZ, lv.maxX, lv.maxY, lv.maxZ);
        List<Entity> list = arg22.getEntities(Entity.class, lv2, arg -> !(arg instanceof PlayerEntity));
        list.forEach(Entity::remove);
    }

    public static BlockBox method_29409(BlockPos arg, BlockPos arg2, BlockRotation arg3) {
        BlockPos lv = arg.add(arg2).add(-1, -1, -1);
        BlockPos lv2 = Structure.transformAround(lv, BlockMirror.NONE, arg3, arg);
        BlockBox lv3 = BlockBox.create(arg.getX(), arg.getY(), arg.getZ(), lv2.getX(), lv2.getY(), lv2.getZ());
        int i = Math.min(lv3.minX, lv3.maxX);
        int j = Math.min(lv3.minZ, lv3.maxZ);
        BlockPos lv4 = new BlockPos(arg.getX() - i, 0, arg.getZ() - j);
        lv3.method_29299(lv4);
        return lv3;
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

    private static StructureBlockBlockEntity placeStructure(String string, BlockPos arg, BlockRotation arg2, ServerWorld arg3, boolean bl) {
        arg3.setBlockState(arg, Blocks.STRUCTURE_BLOCK.getDefaultState());
        StructureBlockBlockEntity lv = (StructureBlockBlockEntity)arg3.getBlockEntity(arg);
        lv.setMode(StructureBlockMode.LOAD);
        lv.setRotation(arg2);
        lv.setIgnoreEntities(false);
        lv.setStructureName(new Identifier(string));
        lv.loadStructure(arg3, bl);
        if (lv.getSize() != BlockPos.ORIGIN) {
            return lv;
        }
        Structure lv2 = StructureTestUtil.createStructure(string, arg3);
        lv.place(arg3, bl, lv2);
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
        BlockState lv = null;
        FlatChunkGeneratorConfig lv2 = FlatChunkGeneratorConfig.getDefaultConfig();
        if (lv2 instanceof FlatChunkGeneratorConfig) {
            BlockState[] lvs = lv2.getLayerBlocks();
            if (arg.getY() < i && arg.getY() <= lvs.length) {
                lv = lvs[arg.getY() - 1];
            }
        } else if (arg.getY() == i - 1) {
            lv = arg2.getBiome(arg).getSurfaceConfig().getTopMaterial();
        } else if (arg.getY() < i - 1) {
            lv = arg2.getBiome(arg).getSurfaceConfig().getUnderMaterial();
        }
        if (lv == null) {
            lv = Blocks.AIR.getDefaultState();
        }
        BlockStateArgument lv3 = new BlockStateArgument(lv, Collections.emptySet(), null);
        lv3.setBlockState(arg2, arg, 2);
        arg2.updateNeighbors(arg, lv.getBlock());
    }

    private static boolean isInStructureBounds(BlockPos arg, BlockPos arg2, ServerWorld arg3) {
        StructureBlockBlockEntity lv = (StructureBlockBlockEntity)arg3.getBlockEntity(arg);
        Box lv2 = StructureTestUtil.getStructureBoundingBox(lv).expand(1.0);
        return lv2.contains(Vec3d.ofCenter(arg2));
    }
}

