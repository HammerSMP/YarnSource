/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.structure;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.TripwireBlock;
import net.minecraft.block.TripwireHookBlock;
import net.minecraft.block.VineBlock;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePieceWithDimensions;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class JungleTempleGenerator
extends StructurePieceWithDimensions {
    private boolean placedMainChest;
    private boolean placedHiddenChest;
    private boolean placedTrap1;
    private boolean placedTrap2;
    private static final CobblestoneRandomizer COBBLESTONE_RANDOMIZER = new CobblestoneRandomizer();

    public JungleTempleGenerator(Random random, int x, int z) {
        super(StructurePieceType.JUNGLE_TEMPLE, random, x, 64, z, 12, 10, 15);
    }

    public JungleTempleGenerator(StructureManager manager, CompoundTag tag) {
        super(StructurePieceType.JUNGLE_TEMPLE, tag);
        this.placedMainChest = tag.getBoolean("placedMainChest");
        this.placedHiddenChest = tag.getBoolean("placedHiddenChest");
        this.placedTrap1 = tag.getBoolean("placedTrap1");
        this.placedTrap2 = tag.getBoolean("placedTrap2");
    }

    @Override
    protected void toNbt(CompoundTag tag) {
        super.toNbt(tag);
        tag.putBoolean("placedMainChest", this.placedMainChest);
        tag.putBoolean("placedHiddenChest", this.placedHiddenChest);
        tag.putBoolean("placedTrap1", this.placedTrap1);
        tag.putBoolean("placedTrap2", this.placedTrap2);
    }

    @Override
    public boolean generate(ServerWorldAccess arg, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox boundingBox, ChunkPos arg5, BlockPos arg6) {
        if (!this.method_14839(arg, boundingBox, 0)) {
            return false;
        }
        this.fillWithOutline(arg, boundingBox, 0, -4, 0, this.width - 1, 0, this.depth - 1, false, random, COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(arg, boundingBox, 2, 1, 2, 9, 2, 2, false, random, COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(arg, boundingBox, 2, 1, 12, 9, 2, 12, false, random, COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(arg, boundingBox, 2, 1, 3, 2, 2, 11, false, random, COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(arg, boundingBox, 9, 1, 3, 9, 2, 11, false, random, COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(arg, boundingBox, 1, 3, 1, 10, 6, 1, false, random, COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(arg, boundingBox, 1, 3, 13, 10, 6, 13, false, random, COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(arg, boundingBox, 1, 3, 2, 1, 6, 12, false, random, COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(arg, boundingBox, 10, 3, 2, 10, 6, 12, false, random, COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(arg, boundingBox, 2, 3, 2, 9, 3, 12, false, random, COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(arg, boundingBox, 2, 6, 2, 9, 6, 12, false, random, COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(arg, boundingBox, 3, 7, 3, 8, 7, 11, false, random, COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(arg, boundingBox, 4, 8, 4, 7, 8, 10, false, random, COBBLESTONE_RANDOMIZER);
        this.fill(arg, boundingBox, 3, 1, 3, 8, 2, 11);
        this.fill(arg, boundingBox, 4, 3, 6, 7, 3, 9);
        this.fill(arg, boundingBox, 2, 4, 2, 9, 5, 12);
        this.fill(arg, boundingBox, 4, 6, 5, 7, 6, 9);
        this.fill(arg, boundingBox, 5, 7, 6, 6, 7, 8);
        this.fill(arg, boundingBox, 5, 1, 2, 6, 2, 2);
        this.fill(arg, boundingBox, 5, 2, 12, 6, 2, 12);
        this.fill(arg, boundingBox, 5, 5, 1, 6, 5, 1);
        this.fill(arg, boundingBox, 5, 5, 13, 6, 5, 13);
        this.addBlock(arg, Blocks.AIR.getDefaultState(), 1, 5, 5, boundingBox);
        this.addBlock(arg, Blocks.AIR.getDefaultState(), 10, 5, 5, boundingBox);
        this.addBlock(arg, Blocks.AIR.getDefaultState(), 1, 5, 9, boundingBox);
        this.addBlock(arg, Blocks.AIR.getDefaultState(), 10, 5, 9, boundingBox);
        for (int i = 0; i <= 14; i += 14) {
            this.fillWithOutline(arg, boundingBox, 2, 4, i, 2, 5, i, false, random, COBBLESTONE_RANDOMIZER);
            this.fillWithOutline(arg, boundingBox, 4, 4, i, 4, 5, i, false, random, COBBLESTONE_RANDOMIZER);
            this.fillWithOutline(arg, boundingBox, 7, 4, i, 7, 5, i, false, random, COBBLESTONE_RANDOMIZER);
            this.fillWithOutline(arg, boundingBox, 9, 4, i, 9, 5, i, false, random, COBBLESTONE_RANDOMIZER);
        }
        this.fillWithOutline(arg, boundingBox, 5, 6, 0, 6, 6, 0, false, random, COBBLESTONE_RANDOMIZER);
        for (int j = 0; j <= 11; j += 11) {
            for (int k = 2; k <= 12; k += 2) {
                this.fillWithOutline(arg, boundingBox, j, 4, k, j, 5, k, false, random, COBBLESTONE_RANDOMIZER);
            }
            this.fillWithOutline(arg, boundingBox, j, 6, 5, j, 6, 5, false, random, COBBLESTONE_RANDOMIZER);
            this.fillWithOutline(arg, boundingBox, j, 6, 9, j, 6, 9, false, random, COBBLESTONE_RANDOMIZER);
        }
        this.fillWithOutline(arg, boundingBox, 2, 7, 2, 2, 9, 2, false, random, COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(arg, boundingBox, 9, 7, 2, 9, 9, 2, false, random, COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(arg, boundingBox, 2, 7, 12, 2, 9, 12, false, random, COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(arg, boundingBox, 9, 7, 12, 9, 9, 12, false, random, COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(arg, boundingBox, 4, 9, 4, 4, 9, 4, false, random, COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(arg, boundingBox, 7, 9, 4, 7, 9, 4, false, random, COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(arg, boundingBox, 4, 9, 10, 4, 9, 10, false, random, COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(arg, boundingBox, 7, 9, 10, 7, 9, 10, false, random, COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(arg, boundingBox, 5, 9, 7, 6, 9, 7, false, random, COBBLESTONE_RANDOMIZER);
        BlockState lv = (BlockState)Blocks.COBBLESTONE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.EAST);
        BlockState lv2 = (BlockState)Blocks.COBBLESTONE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.WEST);
        BlockState lv3 = (BlockState)Blocks.COBBLESTONE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.SOUTH);
        BlockState lv4 = (BlockState)Blocks.COBBLESTONE_STAIRS.getDefaultState().with(StairsBlock.FACING, Direction.NORTH);
        this.addBlock(arg, lv4, 5, 9, 6, boundingBox);
        this.addBlock(arg, lv4, 6, 9, 6, boundingBox);
        this.addBlock(arg, lv3, 5, 9, 8, boundingBox);
        this.addBlock(arg, lv3, 6, 9, 8, boundingBox);
        this.addBlock(arg, lv4, 4, 0, 0, boundingBox);
        this.addBlock(arg, lv4, 5, 0, 0, boundingBox);
        this.addBlock(arg, lv4, 6, 0, 0, boundingBox);
        this.addBlock(arg, lv4, 7, 0, 0, boundingBox);
        this.addBlock(arg, lv4, 4, 1, 8, boundingBox);
        this.addBlock(arg, lv4, 4, 2, 9, boundingBox);
        this.addBlock(arg, lv4, 4, 3, 10, boundingBox);
        this.addBlock(arg, lv4, 7, 1, 8, boundingBox);
        this.addBlock(arg, lv4, 7, 2, 9, boundingBox);
        this.addBlock(arg, lv4, 7, 3, 10, boundingBox);
        this.fillWithOutline(arg, boundingBox, 4, 1, 9, 4, 1, 9, false, random, COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(arg, boundingBox, 7, 1, 9, 7, 1, 9, false, random, COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(arg, boundingBox, 4, 1, 10, 7, 2, 10, false, random, COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(arg, boundingBox, 5, 4, 5, 6, 4, 5, false, random, COBBLESTONE_RANDOMIZER);
        this.addBlock(arg, lv, 4, 4, 5, boundingBox);
        this.addBlock(arg, lv2, 7, 4, 5, boundingBox);
        for (int l = 0; l < 4; ++l) {
            this.addBlock(arg, lv3, 5, 0 - l, 6 + l, boundingBox);
            this.addBlock(arg, lv3, 6, 0 - l, 6 + l, boundingBox);
            this.fill(arg, boundingBox, 5, 0 - l, 7 + l, 6, 0 - l, 9 + l);
        }
        this.fill(arg, boundingBox, 1, -3, 12, 10, -1, 13);
        this.fill(arg, boundingBox, 1, -3, 1, 3, -1, 13);
        this.fill(arg, boundingBox, 1, -3, 1, 9, -1, 5);
        for (int m = 1; m <= 13; m += 2) {
            this.fillWithOutline(arg, boundingBox, 1, -3, m, 1, -2, m, false, random, COBBLESTONE_RANDOMIZER);
        }
        for (int n = 2; n <= 12; n += 2) {
            this.fillWithOutline(arg, boundingBox, 1, -1, n, 3, -1, n, false, random, COBBLESTONE_RANDOMIZER);
        }
        this.fillWithOutline(arg, boundingBox, 2, -2, 1, 5, -2, 1, false, random, COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(arg, boundingBox, 7, -2, 1, 9, -2, 1, false, random, COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(arg, boundingBox, 6, -3, 1, 6, -3, 1, false, random, COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(arg, boundingBox, 6, -1, 1, 6, -1, 1, false, random, COBBLESTONE_RANDOMIZER);
        this.addBlock(arg, (BlockState)((BlockState)Blocks.TRIPWIRE_HOOK.getDefaultState().with(TripwireHookBlock.FACING, Direction.EAST)).with(TripwireHookBlock.ATTACHED, true), 1, -3, 8, boundingBox);
        this.addBlock(arg, (BlockState)((BlockState)Blocks.TRIPWIRE_HOOK.getDefaultState().with(TripwireHookBlock.FACING, Direction.WEST)).with(TripwireHookBlock.ATTACHED, true), 4, -3, 8, boundingBox);
        this.addBlock(arg, (BlockState)((BlockState)((BlockState)Blocks.TRIPWIRE.getDefaultState().with(TripwireBlock.EAST, true)).with(TripwireBlock.WEST, true)).with(TripwireBlock.ATTACHED, true), 2, -3, 8, boundingBox);
        this.addBlock(arg, (BlockState)((BlockState)((BlockState)Blocks.TRIPWIRE.getDefaultState().with(TripwireBlock.EAST, true)).with(TripwireBlock.WEST, true)).with(TripwireBlock.ATTACHED, true), 3, -3, 8, boundingBox);
        BlockState lv5 = (BlockState)((BlockState)Blocks.REDSTONE_WIRE.getDefaultState().with(RedstoneWireBlock.WIRE_CONNECTION_NORTH, WireConnection.SIDE)).with(RedstoneWireBlock.WIRE_CONNECTION_SOUTH, WireConnection.SIDE);
        this.addBlock(arg, lv5, 5, -3, 7, boundingBox);
        this.addBlock(arg, lv5, 5, -3, 6, boundingBox);
        this.addBlock(arg, lv5, 5, -3, 5, boundingBox);
        this.addBlock(arg, lv5, 5, -3, 4, boundingBox);
        this.addBlock(arg, lv5, 5, -3, 3, boundingBox);
        this.addBlock(arg, lv5, 5, -3, 2, boundingBox);
        this.addBlock(arg, (BlockState)((BlockState)Blocks.REDSTONE_WIRE.getDefaultState().with(RedstoneWireBlock.WIRE_CONNECTION_NORTH, WireConnection.SIDE)).with(RedstoneWireBlock.WIRE_CONNECTION_WEST, WireConnection.SIDE), 5, -3, 1, boundingBox);
        this.addBlock(arg, (BlockState)((BlockState)Blocks.REDSTONE_WIRE.getDefaultState().with(RedstoneWireBlock.WIRE_CONNECTION_EAST, WireConnection.SIDE)).with(RedstoneWireBlock.WIRE_CONNECTION_WEST, WireConnection.SIDE), 4, -3, 1, boundingBox);
        this.addBlock(arg, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 3, -3, 1, boundingBox);
        if (!this.placedTrap1) {
            this.placedTrap1 = this.addDispenser(arg, boundingBox, random, 3, -2, 1, Direction.NORTH, LootTables.JUNGLE_TEMPLE_DISPENSER_CHEST);
        }
        this.addBlock(arg, (BlockState)Blocks.VINE.getDefaultState().with(VineBlock.SOUTH, true), 3, -2, 2, boundingBox);
        this.addBlock(arg, (BlockState)((BlockState)Blocks.TRIPWIRE_HOOK.getDefaultState().with(TripwireHookBlock.FACING, Direction.NORTH)).with(TripwireHookBlock.ATTACHED, true), 7, -3, 1, boundingBox);
        this.addBlock(arg, (BlockState)((BlockState)Blocks.TRIPWIRE_HOOK.getDefaultState().with(TripwireHookBlock.FACING, Direction.SOUTH)).with(TripwireHookBlock.ATTACHED, true), 7, -3, 5, boundingBox);
        this.addBlock(arg, (BlockState)((BlockState)((BlockState)Blocks.TRIPWIRE.getDefaultState().with(TripwireBlock.NORTH, true)).with(TripwireBlock.SOUTH, true)).with(TripwireBlock.ATTACHED, true), 7, -3, 2, boundingBox);
        this.addBlock(arg, (BlockState)((BlockState)((BlockState)Blocks.TRIPWIRE.getDefaultState().with(TripwireBlock.NORTH, true)).with(TripwireBlock.SOUTH, true)).with(TripwireBlock.ATTACHED, true), 7, -3, 3, boundingBox);
        this.addBlock(arg, (BlockState)((BlockState)((BlockState)Blocks.TRIPWIRE.getDefaultState().with(TripwireBlock.NORTH, true)).with(TripwireBlock.SOUTH, true)).with(TripwireBlock.ATTACHED, true), 7, -3, 4, boundingBox);
        this.addBlock(arg, (BlockState)((BlockState)Blocks.REDSTONE_WIRE.getDefaultState().with(RedstoneWireBlock.WIRE_CONNECTION_EAST, WireConnection.SIDE)).with(RedstoneWireBlock.WIRE_CONNECTION_WEST, WireConnection.SIDE), 8, -3, 6, boundingBox);
        this.addBlock(arg, (BlockState)((BlockState)Blocks.REDSTONE_WIRE.getDefaultState().with(RedstoneWireBlock.WIRE_CONNECTION_WEST, WireConnection.SIDE)).with(RedstoneWireBlock.WIRE_CONNECTION_SOUTH, WireConnection.SIDE), 9, -3, 6, boundingBox);
        this.addBlock(arg, (BlockState)((BlockState)Blocks.REDSTONE_WIRE.getDefaultState().with(RedstoneWireBlock.WIRE_CONNECTION_NORTH, WireConnection.SIDE)).with(RedstoneWireBlock.WIRE_CONNECTION_SOUTH, WireConnection.UP), 9, -3, 5, boundingBox);
        this.addBlock(arg, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 9, -3, 4, boundingBox);
        this.addBlock(arg, lv5, 9, -2, 4, boundingBox);
        if (!this.placedTrap2) {
            this.placedTrap2 = this.addDispenser(arg, boundingBox, random, 9, -2, 3, Direction.WEST, LootTables.JUNGLE_TEMPLE_DISPENSER_CHEST);
        }
        this.addBlock(arg, (BlockState)Blocks.VINE.getDefaultState().with(VineBlock.EAST, true), 8, -1, 3, boundingBox);
        this.addBlock(arg, (BlockState)Blocks.VINE.getDefaultState().with(VineBlock.EAST, true), 8, -2, 3, boundingBox);
        if (!this.placedMainChest) {
            this.placedMainChest = this.addChest(arg, boundingBox, random, 8, -3, 3, LootTables.JUNGLE_TEMPLE_CHEST);
        }
        this.addBlock(arg, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 9, -3, 2, boundingBox);
        this.addBlock(arg, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 8, -3, 1, boundingBox);
        this.addBlock(arg, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 4, -3, 5, boundingBox);
        this.addBlock(arg, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 5, -2, 5, boundingBox);
        this.addBlock(arg, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 5, -1, 5, boundingBox);
        this.addBlock(arg, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 6, -3, 5, boundingBox);
        this.addBlock(arg, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 7, -2, 5, boundingBox);
        this.addBlock(arg, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 7, -1, 5, boundingBox);
        this.addBlock(arg, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 8, -3, 5, boundingBox);
        this.fillWithOutline(arg, boundingBox, 9, -1, 1, 9, -1, 5, false, random, COBBLESTONE_RANDOMIZER);
        this.fill(arg, boundingBox, 8, -3, 8, 10, -1, 10);
        this.addBlock(arg, Blocks.CHISELED_STONE_BRICKS.getDefaultState(), 8, -2, 11, boundingBox);
        this.addBlock(arg, Blocks.CHISELED_STONE_BRICKS.getDefaultState(), 9, -2, 11, boundingBox);
        this.addBlock(arg, Blocks.CHISELED_STONE_BRICKS.getDefaultState(), 10, -2, 11, boundingBox);
        BlockState lv6 = (BlockState)((BlockState)Blocks.LEVER.getDefaultState().with(LeverBlock.FACING, Direction.NORTH)).with(LeverBlock.FACE, WallMountLocation.WALL);
        this.addBlock(arg, lv6, 8, -2, 12, boundingBox);
        this.addBlock(arg, lv6, 9, -2, 12, boundingBox);
        this.addBlock(arg, lv6, 10, -2, 12, boundingBox);
        this.fillWithOutline(arg, boundingBox, 8, -3, 8, 8, -3, 10, false, random, COBBLESTONE_RANDOMIZER);
        this.fillWithOutline(arg, boundingBox, 10, -3, 8, 10, -3, 10, false, random, COBBLESTONE_RANDOMIZER);
        this.addBlock(arg, Blocks.MOSSY_COBBLESTONE.getDefaultState(), 10, -2, 9, boundingBox);
        this.addBlock(arg, lv5, 8, -2, 9, boundingBox);
        this.addBlock(arg, lv5, 8, -2, 10, boundingBox);
        this.addBlock(arg, (BlockState)((BlockState)((BlockState)((BlockState)Blocks.REDSTONE_WIRE.getDefaultState().with(RedstoneWireBlock.WIRE_CONNECTION_NORTH, WireConnection.SIDE)).with(RedstoneWireBlock.WIRE_CONNECTION_SOUTH, WireConnection.SIDE)).with(RedstoneWireBlock.WIRE_CONNECTION_EAST, WireConnection.SIDE)).with(RedstoneWireBlock.WIRE_CONNECTION_WEST, WireConnection.SIDE), 10, -1, 9, boundingBox);
        this.addBlock(arg, (BlockState)Blocks.STICKY_PISTON.getDefaultState().with(PistonBlock.FACING, Direction.UP), 9, -2, 8, boundingBox);
        this.addBlock(arg, (BlockState)Blocks.STICKY_PISTON.getDefaultState().with(PistonBlock.FACING, Direction.WEST), 10, -2, 8, boundingBox);
        this.addBlock(arg, (BlockState)Blocks.STICKY_PISTON.getDefaultState().with(PistonBlock.FACING, Direction.WEST), 10, -1, 8, boundingBox);
        this.addBlock(arg, (BlockState)Blocks.REPEATER.getDefaultState().with(RepeaterBlock.FACING, Direction.NORTH), 10, -2, 10, boundingBox);
        if (!this.placedHiddenChest) {
            this.placedHiddenChest = this.addChest(arg, boundingBox, random, 9, -3, 10, LootTables.JUNGLE_TEMPLE_CHEST);
        }
        return true;
    }

    static class CobblestoneRandomizer
    extends StructurePiece.BlockRandomizer {
        private CobblestoneRandomizer() {
        }

        @Override
        public void setBlock(Random random, int x, int y, int z, boolean placeBlock) {
            this.block = random.nextFloat() < 0.4f ? Blocks.COBBLESTONE.getDefaultState() : Blocks.MOSSY_COBBLESTONE.getDefaultState();
        }
    }
}

