/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nullable
 */
package net.minecraft.structure;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public abstract class StructurePiece {
    protected static final BlockState AIR = Blocks.CAVE_AIR.getDefaultState();
    protected BlockBox boundingBox;
    @Nullable
    private Direction facing;
    private BlockMirror mirror;
    private BlockRotation rotation;
    protected int length;
    private final StructurePieceType type;
    private static final Set<Block> BLOCKS_NEEDING_POST_PROCESSING = ImmutableSet.builder().add((Object)Blocks.NETHER_BRICK_FENCE).add((Object)Blocks.TORCH).add((Object)Blocks.WALL_TORCH).add((Object)Blocks.OAK_FENCE).add((Object)Blocks.SPRUCE_FENCE).add((Object)Blocks.DARK_OAK_FENCE).add((Object)Blocks.ACACIA_FENCE).add((Object)Blocks.BIRCH_FENCE).add((Object)Blocks.JUNGLE_FENCE).add((Object)Blocks.LADDER).add((Object)Blocks.IRON_BARS).build();

    protected StructurePiece(StructurePieceType arg, int i) {
        this.type = arg;
        this.length = i;
    }

    public StructurePiece(StructurePieceType arg, CompoundTag arg2) {
        this(arg, arg2.getInt("GD"));
        int i;
        if (arg2.contains("BB")) {
            this.boundingBox = new BlockBox(arg2.getIntArray("BB"));
        }
        this.setOrientation((i = arg2.getInt("O")) == -1 ? null : Direction.fromHorizontal(i));
    }

    public final CompoundTag getTag() {
        CompoundTag lv = new CompoundTag();
        lv.putString("id", Registry.STRUCTURE_PIECE.getId(this.getType()).toString());
        lv.put("BB", this.boundingBox.toNbt());
        Direction lv2 = this.getFacing();
        lv.putInt("O", lv2 == null ? -1 : lv2.getHorizontal());
        lv.putInt("GD", this.length);
        this.toNbt(lv);
        return lv;
    }

    protected abstract void toNbt(CompoundTag var1);

    public void placeJigsaw(StructurePiece arg, List<StructurePiece> list, Random random) {
    }

    public abstract boolean generate(ServerWorldAccess var1, StructureAccessor var2, ChunkGenerator var3, Random var4, BlockBox var5, ChunkPos var6, BlockPos var7);

    public BlockBox getBoundingBox() {
        return this.boundingBox;
    }

    public int getLength() {
        return this.length;
    }

    public boolean intersectsChunk(ChunkPos arg, int i) {
        int j = arg.x << 4;
        int k = arg.z << 4;
        return this.boundingBox.intersectsXZ(j - i, k - i, j + 15 + i, k + 15 + i);
    }

    public static StructurePiece getOverlappingPiece(List<StructurePiece> list, BlockBox arg) {
        for (StructurePiece lv : list) {
            if (lv.getBoundingBox() == null || !lv.getBoundingBox().intersects(arg)) continue;
            return lv;
        }
        return null;
    }

    protected boolean method_14937(BlockView arg, BlockBox arg2) {
        int i = Math.max(this.boundingBox.minX - 1, arg2.minX);
        int j = Math.max(this.boundingBox.minY - 1, arg2.minY);
        int k = Math.max(this.boundingBox.minZ - 1, arg2.minZ);
        int l = Math.min(this.boundingBox.maxX + 1, arg2.maxX);
        int m = Math.min(this.boundingBox.maxY + 1, arg2.maxY);
        int n = Math.min(this.boundingBox.maxZ + 1, arg2.maxZ);
        BlockPos.Mutable lv = new BlockPos.Mutable();
        for (int o = i; o <= l; ++o) {
            for (int p = k; p <= n; ++p) {
                if (arg.getBlockState(lv.set(o, j, p)).getMaterial().isLiquid()) {
                    return true;
                }
                if (!arg.getBlockState(lv.set(o, m, p)).getMaterial().isLiquid()) continue;
                return true;
            }
        }
        for (int q = i; q <= l; ++q) {
            for (int r = j; r <= m; ++r) {
                if (arg.getBlockState(lv.set(q, r, k)).getMaterial().isLiquid()) {
                    return true;
                }
                if (!arg.getBlockState(lv.set(q, r, n)).getMaterial().isLiquid()) continue;
                return true;
            }
        }
        for (int s = k; s <= n; ++s) {
            for (int t = j; t <= m; ++t) {
                if (arg.getBlockState(lv.set(i, t, s)).getMaterial().isLiquid()) {
                    return true;
                }
                if (!arg.getBlockState(lv.set(l, t, s)).getMaterial().isLiquid()) continue;
                return true;
            }
        }
        return false;
    }

    protected int applyXTransform(int i, int j) {
        Direction lv = this.getFacing();
        if (lv == null) {
            return i;
        }
        switch (lv) {
            case NORTH: 
            case SOUTH: {
                return this.boundingBox.minX + i;
            }
            case WEST: {
                return this.boundingBox.maxX - j;
            }
            case EAST: {
                return this.boundingBox.minX + j;
            }
        }
        return i;
    }

    protected int applyYTransform(int i) {
        if (this.getFacing() == null) {
            return i;
        }
        return i + this.boundingBox.minY;
    }

    protected int applyZTransform(int i, int j) {
        Direction lv = this.getFacing();
        if (lv == null) {
            return j;
        }
        switch (lv) {
            case NORTH: {
                return this.boundingBox.maxZ - j;
            }
            case SOUTH: {
                return this.boundingBox.minZ + j;
            }
            case WEST: 
            case EAST: {
                return this.boundingBox.minZ + i;
            }
        }
        return j;
    }

    protected void addBlock(WorldAccess arg, BlockState arg2, int i, int j, int k, BlockBox arg3) {
        BlockPos lv = new BlockPos(this.applyXTransform(i, k), this.applyYTransform(j), this.applyZTransform(i, k));
        if (!arg3.contains(lv)) {
            return;
        }
        if (this.mirror != BlockMirror.NONE) {
            arg2 = arg2.mirror(this.mirror);
        }
        if (this.rotation != BlockRotation.NONE) {
            arg2 = arg2.rotate(this.rotation);
        }
        arg.setBlockState(lv, arg2, 2);
        FluidState lv2 = arg.getFluidState(lv);
        if (!lv2.isEmpty()) {
            arg.getFluidTickScheduler().schedule(lv, lv2.getFluid(), 0);
        }
        if (BLOCKS_NEEDING_POST_PROCESSING.contains(arg2.getBlock())) {
            arg.getChunk(lv).markBlockForPostProcessing(lv);
        }
    }

    protected BlockState getBlockAt(BlockView arg, int i, int j, int k, BlockBox arg2) {
        int n;
        int m;
        int l = this.applyXTransform(i, k);
        BlockPos lv = new BlockPos(l, m = this.applyYTransform(j), n = this.applyZTransform(i, k));
        if (!arg2.contains(lv)) {
            return Blocks.AIR.getDefaultState();
        }
        return arg.getBlockState(lv);
    }

    protected boolean isUnderSeaLevel(WorldView arg, int i, int j, int k, BlockBox arg2) {
        int n;
        int m;
        int l = this.applyXTransform(i, k);
        BlockPos lv = new BlockPos(l, m = this.applyYTransform(j + 1), n = this.applyZTransform(i, k));
        if (!arg2.contains(lv)) {
            return false;
        }
        return m < arg.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, l, n);
    }

    protected void fill(WorldAccess arg, BlockBox arg2, int i, int j, int k, int l, int m, int n) {
        for (int o = j; o <= m; ++o) {
            for (int p = i; p <= l; ++p) {
                for (int q = k; q <= n; ++q) {
                    this.addBlock(arg, Blocks.AIR.getDefaultState(), p, o, q, arg2);
                }
            }
        }
    }

    protected void fillWithOutline(WorldAccess arg, BlockBox arg2, int i, int j, int k, int l, int m, int n, BlockState arg3, BlockState arg4, boolean bl) {
        for (int o = j; o <= m; ++o) {
            for (int p = i; p <= l; ++p) {
                for (int q = k; q <= n; ++q) {
                    if (bl && this.getBlockAt(arg, p, o, q, arg2).isAir()) continue;
                    if (o == j || o == m || p == i || p == l || q == k || q == n) {
                        this.addBlock(arg, arg3, p, o, q, arg2);
                        continue;
                    }
                    this.addBlock(arg, arg4, p, o, q, arg2);
                }
            }
        }
    }

    protected void fillWithOutline(WorldAccess arg, BlockBox arg2, int i, int j, int k, int l, int m, int n, boolean bl, Random random, BlockRandomizer arg3) {
        for (int o = j; o <= m; ++o) {
            for (int p = i; p <= l; ++p) {
                for (int q = k; q <= n; ++q) {
                    if (bl && this.getBlockAt(arg, p, o, q, arg2).isAir()) continue;
                    arg3.setBlock(random, p, o, q, o == j || o == m || p == i || p == l || q == k || q == n);
                    this.addBlock(arg, arg3.getBlock(), p, o, q, arg2);
                }
            }
        }
    }

    protected void fillWithOutlineUnderSeaLevel(WorldAccess arg, BlockBox arg2, Random random, float f, int i, int j, int k, int l, int m, int n, BlockState arg3, BlockState arg4, boolean bl, boolean bl2) {
        for (int o = j; o <= m; ++o) {
            for (int p = i; p <= l; ++p) {
                for (int q = k; q <= n; ++q) {
                    if (random.nextFloat() > f || bl && this.getBlockAt(arg, p, o, q, arg2).isAir() || bl2 && !this.isUnderSeaLevel(arg, p, o, q, arg2)) continue;
                    if (o == j || o == m || p == i || p == l || q == k || q == n) {
                        this.addBlock(arg, arg3, p, o, q, arg2);
                        continue;
                    }
                    this.addBlock(arg, arg4, p, o, q, arg2);
                }
            }
        }
    }

    protected void addBlockWithRandomThreshold(WorldAccess arg, BlockBox arg2, Random random, float f, int i, int j, int k, BlockState arg3) {
        if (random.nextFloat() < f) {
            this.addBlock(arg, arg3, i, j, k, arg2);
        }
    }

    protected void method_14919(WorldAccess arg, BlockBox arg2, int i, int j, int k, int l, int m, int n, BlockState arg3, boolean bl) {
        float f = l - i + 1;
        float g = m - j + 1;
        float h = n - k + 1;
        float o = (float)i + f / 2.0f;
        float p = (float)k + h / 2.0f;
        for (int q = j; q <= m; ++q) {
            float r = (float)(q - j) / g;
            for (int s = i; s <= l; ++s) {
                float t = ((float)s - o) / (f * 0.5f);
                for (int u = k; u <= n; ++u) {
                    float w;
                    float v = ((float)u - p) / (h * 0.5f);
                    if (bl && this.getBlockAt(arg, s, q, u, arg2).isAir() || !((w = t * t + r * r + v * v) <= 1.05f)) continue;
                    this.addBlock(arg, arg3, s, q, u, arg2);
                }
            }
        }
    }

    protected void method_14936(WorldAccess arg, BlockState arg2, int i, int j, int k, BlockBox arg3) {
        int n;
        int m;
        int l = this.applyXTransform(i, k);
        if (!arg3.contains(new BlockPos(l, m = this.applyYTransform(j), n = this.applyZTransform(i, k)))) {
            return;
        }
        while ((arg.isAir(new BlockPos(l, m, n)) || arg.getBlockState(new BlockPos(l, m, n)).getMaterial().isLiquid()) && m > 1) {
            arg.setBlockState(new BlockPos(l, m, n), arg2, 2);
            --m;
        }
    }

    protected boolean addChest(WorldAccess arg, BlockBox arg2, Random random, int i, int j, int k, Identifier arg3) {
        BlockPos lv = new BlockPos(this.applyXTransform(i, k), this.applyYTransform(j), this.applyZTransform(i, k));
        return this.addChest(arg, arg2, random, lv, arg3, null);
    }

    public static BlockState method_14916(BlockView arg, BlockPos arg2, BlockState arg3) {
        Direction lv = null;
        for (Direction lv2 : Direction.Type.HORIZONTAL) {
            BlockPos lv3 = arg2.offset(lv2);
            BlockState lv4 = arg.getBlockState(lv3);
            if (lv4.isOf(Blocks.CHEST)) {
                return arg3;
            }
            if (!lv4.isOpaqueFullCube(arg, lv3)) continue;
            if (lv == null) {
                lv = lv2;
                continue;
            }
            lv = null;
            break;
        }
        if (lv != null) {
            return (BlockState)arg3.with(HorizontalFacingBlock.FACING, lv.getOpposite());
        }
        Direction lv5 = arg3.get(HorizontalFacingBlock.FACING);
        BlockPos lv6 = arg2.offset(lv5);
        if (arg.getBlockState(lv6).isOpaqueFullCube(arg, lv6)) {
            lv5 = lv5.getOpposite();
            lv6 = arg2.offset(lv5);
        }
        if (arg.getBlockState(lv6).isOpaqueFullCube(arg, lv6)) {
            lv5 = lv5.rotateYClockwise();
            lv6 = arg2.offset(lv5);
        }
        if (arg.getBlockState(lv6).isOpaqueFullCube(arg, lv6)) {
            lv5 = lv5.getOpposite();
            lv6 = arg2.offset(lv5);
        }
        return (BlockState)arg3.with(HorizontalFacingBlock.FACING, lv5);
    }

    protected boolean addChest(WorldAccess arg, BlockBox arg2, Random random, BlockPos arg3, Identifier arg4, @Nullable BlockState arg5) {
        if (!arg2.contains(arg3) || arg.getBlockState(arg3).isOf(Blocks.CHEST)) {
            return false;
        }
        if (arg5 == null) {
            arg5 = StructurePiece.method_14916(arg, arg3, Blocks.CHEST.getDefaultState());
        }
        arg.setBlockState(arg3, arg5, 2);
        BlockEntity lv = arg.getBlockEntity(arg3);
        if (lv instanceof ChestBlockEntity) {
            ((ChestBlockEntity)lv).setLootTable(arg4, random.nextLong());
        }
        return true;
    }

    protected boolean addDispenser(WorldAccess arg, BlockBox arg2, Random random, int i, int j, int k, Direction arg3, Identifier arg4) {
        BlockPos lv = new BlockPos(this.applyXTransform(i, k), this.applyYTransform(j), this.applyZTransform(i, k));
        if (arg2.contains(lv) && !arg.getBlockState(lv).isOf(Blocks.DISPENSER)) {
            this.addBlock(arg, (BlockState)Blocks.DISPENSER.getDefaultState().with(DispenserBlock.FACING, arg3), i, j, k, arg2);
            BlockEntity lv2 = arg.getBlockEntity(lv);
            if (lv2 instanceof DispenserBlockEntity) {
                ((DispenserBlockEntity)lv2).setLootTable(arg4, random.nextLong());
            }
            return true;
        }
        return false;
    }

    public void translate(int i, int j, int k) {
        this.boundingBox.offset(i, j, k);
    }

    @Nullable
    public Direction getFacing() {
        return this.facing;
    }

    public void setOrientation(@Nullable Direction arg) {
        this.facing = arg;
        if (arg == null) {
            this.rotation = BlockRotation.NONE;
            this.mirror = BlockMirror.NONE;
        } else {
            switch (arg) {
                case SOUTH: {
                    this.mirror = BlockMirror.LEFT_RIGHT;
                    this.rotation = BlockRotation.NONE;
                    break;
                }
                case WEST: {
                    this.mirror = BlockMirror.LEFT_RIGHT;
                    this.rotation = BlockRotation.CLOCKWISE_90;
                    break;
                }
                case EAST: {
                    this.mirror = BlockMirror.NONE;
                    this.rotation = BlockRotation.CLOCKWISE_90;
                    break;
                }
                default: {
                    this.mirror = BlockMirror.NONE;
                    this.rotation = BlockRotation.NONE;
                }
            }
        }
    }

    public BlockRotation getRotation() {
        return this.rotation;
    }

    public StructurePieceType getType() {
        return this.type;
    }

    public static abstract class BlockRandomizer {
        protected BlockState block = Blocks.AIR.getDefaultState();

        protected BlockRandomizer() {
        }

        public abstract void setBlock(Random var1, int var2, int var3, int var4, boolean var5);

        public BlockState getBlock() {
            return this.block;
        }
    }
}

