/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.world.chunk;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.chunk.IdListPalette;
import net.minecraft.world.chunk.Palette;
import net.minecraft.world.chunk.PalettedContainer;
import net.minecraft.world.chunk.WorldChunk;

public class ChunkSection {
    private static final Palette<BlockState> palette = new IdListPalette<BlockState>(Block.STATE_IDS, Blocks.AIR.getDefaultState());
    private final int yOffset;
    private short nonEmptyBlockCount;
    private short randomTickableBlockCount;
    private short nonEmptyFluidCount;
    private final PalettedContainer<BlockState> container;

    public ChunkSection(int i) {
        this(i, 0, 0, 0);
    }

    public ChunkSection(int i, short s, short t, short u) {
        this.yOffset = i;
        this.nonEmptyBlockCount = s;
        this.randomTickableBlockCount = t;
        this.nonEmptyFluidCount = u;
        this.container = new PalettedContainer<BlockState>(palette, Block.STATE_IDS, NbtHelper::toBlockState, NbtHelper::fromBlockState, Blocks.AIR.getDefaultState());
    }

    public BlockState getBlockState(int i, int j, int k) {
        return this.container.get(i, j, k);
    }

    public FluidState getFluidState(int i, int j, int k) {
        return this.container.get(i, j, k).getFluidState();
    }

    public void lock() {
        this.container.lock();
    }

    public void unlock() {
        this.container.unlock();
    }

    public BlockState setBlockState(int i, int j, int k, BlockState arg) {
        return this.setBlockState(i, j, k, arg, true);
    }

    public BlockState setBlockState(int i, int j, int k, BlockState arg, boolean bl) {
        BlockState lv2;
        if (bl) {
            BlockState lv = this.container.setSync(i, j, k, arg);
        } else {
            lv2 = this.container.set(i, j, k, arg);
        }
        FluidState lv3 = lv2.getFluidState();
        FluidState lv4 = arg.getFluidState();
        if (!lv2.isAir()) {
            this.nonEmptyBlockCount = (short)(this.nonEmptyBlockCount - 1);
            if (lv2.hasRandomTicks()) {
                this.randomTickableBlockCount = (short)(this.randomTickableBlockCount - 1);
            }
        }
        if (!lv3.isEmpty()) {
            this.nonEmptyFluidCount = (short)(this.nonEmptyFluidCount - 1);
        }
        if (!arg.isAir()) {
            this.nonEmptyBlockCount = (short)(this.nonEmptyBlockCount + 1);
            if (arg.hasRandomTicks()) {
                this.randomTickableBlockCount = (short)(this.randomTickableBlockCount + 1);
            }
        }
        if (!lv4.isEmpty()) {
            this.nonEmptyFluidCount = (short)(this.nonEmptyFluidCount + 1);
        }
        return lv2;
    }

    public boolean isEmpty() {
        return this.nonEmptyBlockCount == 0;
    }

    public static boolean isEmpty(@Nullable ChunkSection arg) {
        return arg == WorldChunk.EMPTY_SECTION || arg.isEmpty();
    }

    public boolean hasRandomTicks() {
        return this.hasRandomBlockTicks() || this.hasRandomFluidTicks();
    }

    public boolean hasRandomBlockTicks() {
        return this.randomTickableBlockCount > 0;
    }

    public boolean hasRandomFluidTicks() {
        return this.nonEmptyFluidCount > 0;
    }

    public int getYOffset() {
        return this.yOffset;
    }

    public void calculateCounts() {
        this.nonEmptyBlockCount = 0;
        this.randomTickableBlockCount = 0;
        this.nonEmptyFluidCount = 0;
        this.container.count((arg, i) -> {
            FluidState lv = arg.getFluidState();
            if (!arg.isAir()) {
                this.nonEmptyBlockCount = (short)(this.nonEmptyBlockCount + i);
                if (arg.hasRandomTicks()) {
                    this.randomTickableBlockCount = (short)(this.randomTickableBlockCount + i);
                }
            }
            if (!lv.isEmpty()) {
                this.nonEmptyBlockCount = (short)(this.nonEmptyBlockCount + i);
                if (lv.hasRandomTicks()) {
                    this.nonEmptyFluidCount = (short)(this.nonEmptyFluidCount + i);
                }
            }
        });
    }

    public PalettedContainer<BlockState> getContainer() {
        return this.container;
    }

    @Environment(value=EnvType.CLIENT)
    public void fromPacket(PacketByteBuf arg) {
        this.nonEmptyBlockCount = arg.readShort();
        this.container.fromPacket(arg);
    }

    public void toPacket(PacketByteBuf arg) {
        arg.writeShort(this.nonEmptyBlockCount);
        this.container.toPacket(arg);
    }

    public int getPacketSize() {
        return 2 + this.container.getPacketSize();
    }

    public boolean method_19523(BlockState arg) {
        return this.container.method_19526(arg);
    }
}

