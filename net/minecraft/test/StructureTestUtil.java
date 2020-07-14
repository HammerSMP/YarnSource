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

    public static Box getStructureBoundingBox(StructureBlockBlockEntity structureBlockEntity) {
        BlockPos lv = structureBlockEntity.getPos();
        BlockPos lv2 = lv.add(structureBlockEntity.getSize().add(-1, -1, -1));
        BlockPos lv3 = Structure.transformAround(lv2, BlockMirror.NONE, structureBlockEntity.getRotation(), lv);
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

    public static void createTestArea(String structure, BlockPos pos, BlockPos size, BlockRotation arg3, ServerWorld world) {
        BlockBox lv = StructureTestUtil.method_29409(pos, size, arg3);
        StructureTestUtil.clearArea(lv, pos.getY(), world);
        world.setBlockState(pos, Blocks.STRUCTURE_BLOCK.getDefaultState());
        StructureBlockBlockEntity lv2 = (StructureBlockBlockEntity)world.getBlockEntity(pos);
        lv2.setIgnoreEntities(false);
        lv2.setStructureName(new Identifier(structure));
        lv2.setSize(size);
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

    private static void forceLoadNearbyChunks(BlockPos pos, ServerWorld world) {
        ChunkPos lv = new ChunkPos(pos);
        for (int i = -1; i < 4; ++i) {
            for (int j = -1; j < 4; ++j) {
                int k = lv.x + i;
                int l = lv.z + j;
                world.setChunkForced(k, l, true);
            }
        }
    }

    public static void clearArea(BlockBox area, int i, ServerWorld world) {
        BlockBox lv = new BlockBox(area.minX - 2, area.minY - 3, area.minZ - 3, area.maxX + 3, area.maxY + 20, area.maxZ + 3);
        BlockPos.stream(lv).forEach(arg2 -> StructureTestUtil.method_22368(i, arg2, world));
        ((ServerTickScheduler)world.getBlockTickScheduler()).getScheduledTicks(lv, true, false);
        world.clearUpdatesInArea(lv);
        Box lv2 = new Box(lv.minX, lv.minY, lv.minZ, lv.maxX, lv.maxY, lv.maxZ);
        List<Entity> list = world.getEntities(Entity.class, lv2, arg -> !(arg instanceof PlayerEntity));
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

    public static Optional<BlockPos> findContainingStructureBlock(BlockPos pos, int radius, ServerWorld world) {
        return StructureTestUtil.findStructureBlocks(pos, radius, world).stream().filter(arg3 -> StructureTestUtil.isInStructureBounds(arg3, pos, world)).findFirst();
    }

    @Nullable
    public static BlockPos findNearestStructureBlock(BlockPos pos, int radius, ServerWorld world) {
        Comparator<BlockPos> comparator = Comparator.comparingInt(arg2 -> arg2.getManhattanDistance(pos));
        Collection<BlockPos> collection = StructureTestUtil.findStructureBlocks(pos, radius, world);
        Optional<BlockPos> optional = collection.stream().min(comparator);
        return optional.orElse(null);
    }

    public static Collection<BlockPos> findStructureBlocks(BlockPos pos, int radius, ServerWorld world) {
        ArrayList collection = Lists.newArrayList();
        Box lv = new Box(pos);
        lv = lv.expand(radius);
        for (int j = (int)lv.minX; j <= (int)lv.maxX; ++j) {
            for (int k = (int)lv.minY; k <= (int)lv.maxY; ++k) {
                for (int l = (int)lv.minZ; l <= (int)lv.maxZ; ++l) {
                    BlockPos lv2 = new BlockPos(j, k, l);
                    BlockState lv3 = world.getBlockState(lv2);
                    if (!lv3.isOf(Blocks.STRUCTURE_BLOCK)) continue;
                    collection.add(lv2);
                }
            }
        }
        return collection;
    }

    private static Structure createStructure(String structureId, ServerWorld world) {
        StructureManager lv = world.getStructureManager();
        Structure lv2 = lv.getStructure(new Identifier(structureId));
        if (lv2 != null) {
            return lv2;
        }
        String string2 = structureId + ".snbt";
        Path path = Paths.get(testStructuresDirectoryName, string2);
        CompoundTag lv3 = StructureTestUtil.loadSnbt(path);
        if (lv3 == null) {
            throw new RuntimeException("Could not find structure file " + path + ", and the structure is not available in the world structures either.");
        }
        return lv.createStructure(lv3);
    }

    private static StructureBlockBlockEntity placeStructure(String name, BlockPos pos, BlockRotation arg2, ServerWorld arg3, boolean bl) {
        arg3.setBlockState(pos, Blocks.STRUCTURE_BLOCK.getDefaultState());
        StructureBlockBlockEntity lv = (StructureBlockBlockEntity)arg3.getBlockEntity(pos);
        lv.setMode(StructureBlockMode.LOAD);
        lv.setRotation(arg2);
        lv.setIgnoreEntities(false);
        lv.setStructureName(new Identifier(name));
        lv.loadStructure(arg3, bl);
        if (lv.getSize() != BlockPos.ORIGIN) {
            return lv;
        }
        Structure lv2 = StructureTestUtil.createStructure(name, arg3);
        lv.place(arg3, bl, lv2);
        if (lv.getSize() == BlockPos.ORIGIN) {
            throw new RuntimeException("Failed to load structure " + name);
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

    private static void method_22368(int altitude, BlockPos pos, ServerWorld world) {
        BlockState lv = null;
        FlatChunkGeneratorConfig lv2 = FlatChunkGeneratorConfig.getDefaultConfig();
        if (lv2 instanceof FlatChunkGeneratorConfig) {
            BlockState[] lvs = lv2.getLayerBlocks();
            if (pos.getY() < altitude && pos.getY() <= lvs.length) {
                lv = lvs[pos.getY() - 1];
            }
        } else if (pos.getY() == altitude - 1) {
            lv = world.getBiome(pos).getSurfaceConfig().getTopMaterial();
        } else if (pos.getY() < altitude - 1) {
            lv = world.getBiome(pos).getSurfaceConfig().getUnderMaterial();
        }
        if (lv == null) {
            lv = Blocks.AIR.getDefaultState();
        }
        BlockStateArgument lv3 = new BlockStateArgument(lv, Collections.emptySet(), null);
        lv3.setBlockState(world, pos, 2);
        world.updateNeighbors(pos, lv.getBlock());
    }

    private static boolean isInStructureBounds(BlockPos structureBlockPos, BlockPos pos, ServerWorld world) {
        StructureBlockBlockEntity lv = (StructureBlockBlockEntity)world.getBlockEntity(structureBlockPos);
        Box lv2 = StructureTestUtil.getStructureBoundingBox(lv).expand(1.0);
        return lv2.contains(Vec3d.ofCenter(pos));
    }
}

