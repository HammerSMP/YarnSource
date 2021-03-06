/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.util.Pair
 *  javax.annotation.Nullable
 */
package net.minecraft.structure;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.class_5425;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Clearable;
import net.minecraft.util.collection.IdList;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.BitSetVoxelSet;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.world.EmptyBlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class Structure {
    private final List<PalettedBlockInfoList> blockInfoLists = Lists.newArrayList();
    private final List<StructureEntityInfo> entities = Lists.newArrayList();
    private BlockPos size = BlockPos.ORIGIN;
    private String author = "?";

    public BlockPos getSize() {
        return this.size;
    }

    public void setAuthor(String name) {
        this.author = name;
    }

    public String getAuthor() {
        return this.author;
    }

    public void saveFromWorld(World world, BlockPos start, BlockPos size, boolean includeEntities, @Nullable Block ignoredBlock) {
        if (size.getX() < 1 || size.getY() < 1 || size.getZ() < 1) {
            return;
        }
        BlockPos lv = start.add(size).add(-1, -1, -1);
        ArrayList list = Lists.newArrayList();
        ArrayList list2 = Lists.newArrayList();
        ArrayList list3 = Lists.newArrayList();
        BlockPos lv2 = new BlockPos(Math.min(start.getX(), lv.getX()), Math.min(start.getY(), lv.getY()), Math.min(start.getZ(), lv.getZ()));
        BlockPos lv3 = new BlockPos(Math.max(start.getX(), lv.getX()), Math.max(start.getY(), lv.getY()), Math.max(start.getZ(), lv.getZ()));
        this.size = size;
        for (BlockPos lv4 : BlockPos.iterate(lv2, lv3)) {
            StructureBlockInfo lv10;
            BlockPos lv5 = lv4.subtract(lv2);
            BlockState lv6 = world.getBlockState(lv4);
            if (ignoredBlock != null && ignoredBlock == lv6.getBlock()) continue;
            BlockEntity lv7 = world.getBlockEntity(lv4);
            if (lv7 != null) {
                CompoundTag lv8 = lv7.toTag(new CompoundTag());
                lv8.remove("x");
                lv8.remove("y");
                lv8.remove("z");
                StructureBlockInfo lv9 = new StructureBlockInfo(lv5, lv6, lv8.copy());
            } else {
                lv10 = new StructureBlockInfo(lv5, lv6, null);
            }
            Structure.method_28054(lv10, list, list2, list3);
        }
        List<StructureBlockInfo> list4 = Structure.method_28055(list, list2, list3);
        this.blockInfoLists.clear();
        this.blockInfoLists.add(new PalettedBlockInfoList(list4));
        if (includeEntities) {
            this.addEntitiesFromWorld(world, lv2, lv3.add(1, 1, 1));
        } else {
            this.entities.clear();
        }
    }

    private static void method_28054(StructureBlockInfo arg, List<StructureBlockInfo> list, List<StructureBlockInfo> list2, List<StructureBlockInfo> list3) {
        if (arg.tag != null) {
            list2.add(arg);
        } else if (!arg.state.getBlock().hasDynamicBounds() && arg.state.isFullCube(EmptyBlockView.INSTANCE, BlockPos.ORIGIN)) {
            list.add(arg);
        } else {
            list3.add(arg);
        }
    }

    private static List<StructureBlockInfo> method_28055(List<StructureBlockInfo> list, List<StructureBlockInfo> list2, List<StructureBlockInfo> list3) {
        Comparator<StructureBlockInfo> comparator = Comparator.comparingInt(arg -> arg.pos.getY()).thenComparingInt(arg -> arg.pos.getX()).thenComparingInt(arg -> arg.pos.getZ());
        list.sort(comparator);
        list3.sort(comparator);
        list2.sort(comparator);
        ArrayList list4 = Lists.newArrayList();
        list4.addAll(list);
        list4.addAll(list3);
        list4.addAll(list2);
        return list4;
    }

    private void addEntitiesFromWorld(World world, BlockPos firstCorner, BlockPos secondCorner) {
        List<Entity> list = world.getEntities(Entity.class, new Box(firstCorner, secondCorner), arg -> !(arg instanceof PlayerEntity));
        this.entities.clear();
        for (Entity lv : list) {
            BlockPos lv5;
            Vec3d lv2 = new Vec3d(lv.getX() - (double)firstCorner.getX(), lv.getY() - (double)firstCorner.getY(), lv.getZ() - (double)firstCorner.getZ());
            CompoundTag lv3 = new CompoundTag();
            lv.saveToTag(lv3);
            if (lv instanceof PaintingEntity) {
                BlockPos lv4 = ((PaintingEntity)lv).getDecorationBlockPos().subtract(firstCorner);
            } else {
                lv5 = new BlockPos(lv2);
            }
            this.entities.add(new StructureEntityInfo(lv2, lv5, lv3.copy()));
        }
    }

    public List<StructureBlockInfo> getInfosForBlock(BlockPos pos, StructurePlacementData placementData, Block block) {
        return this.getInfosForBlock(pos, placementData, block, true);
    }

    public List<StructureBlockInfo> getInfosForBlock(BlockPos pos, StructurePlacementData placementData, Block block, boolean transformed) {
        ArrayList list = Lists.newArrayList();
        BlockBox lv = placementData.getBoundingBox();
        if (this.blockInfoLists.isEmpty()) {
            return Collections.emptyList();
        }
        for (StructureBlockInfo lv2 : placementData.getRandomBlockInfos(this.blockInfoLists, pos).getAllOf(block)) {
            BlockPos lv3;
            BlockPos blockPos = lv3 = transformed ? Structure.transform(placementData, lv2.pos).add(pos) : lv2.pos;
            if (lv != null && !lv.contains(lv3)) continue;
            list.add(new StructureBlockInfo(lv3, lv2.state.rotate(placementData.getRotation()), lv2.tag));
        }
        return list;
    }

    public BlockPos transformBox(StructurePlacementData placementData1, BlockPos pos1, StructurePlacementData placementData2, BlockPos pos2) {
        BlockPos lv = Structure.transform(placementData1, pos1);
        BlockPos lv2 = Structure.transform(placementData2, pos2);
        return lv.subtract(lv2);
    }

    public static BlockPos transform(StructurePlacementData placementData, BlockPos pos) {
        return Structure.transformAround(pos, placementData.getMirror(), placementData.getRotation(), placementData.getPosition());
    }

    public void place(class_5425 arg, BlockPos pos, StructurePlacementData placementData, Random random) {
        placementData.calculateBoundingBox();
        this.placeAndNotifyListeners(arg, pos, placementData, random);
    }

    public void placeAndNotifyListeners(class_5425 arg, BlockPos pos, StructurePlacementData data, Random random) {
        this.place(arg, pos, pos, data, random, 2);
    }

    public boolean place(class_5425 arg, BlockPos pos, BlockPos arg3, StructurePlacementData placementData, Random random, int i) {
        if (this.blockInfoLists.isEmpty()) {
            return false;
        }
        List<StructureBlockInfo> list = placementData.getRandomBlockInfos(this.blockInfoLists, pos).getAll();
        if (list.isEmpty() && (placementData.shouldIgnoreEntities() || this.entities.isEmpty()) || this.size.getX() < 1 || this.size.getY() < 1 || this.size.getZ() < 1) {
            return false;
        }
        BlockBox lv = placementData.getBoundingBox();
        ArrayList list2 = Lists.newArrayListWithCapacity((int)(placementData.shouldPlaceFluids() ? list.size() : 0));
        ArrayList list3 = Lists.newArrayListWithCapacity((int)list.size());
        int j = Integer.MAX_VALUE;
        int k = Integer.MAX_VALUE;
        int l = Integer.MAX_VALUE;
        int m = Integer.MIN_VALUE;
        int n = Integer.MIN_VALUE;
        int o = Integer.MIN_VALUE;
        List<StructureBlockInfo> list4 = Structure.process(arg, pos, arg3, placementData, list);
        for (StructureBlockInfo lv2 : list4) {
            BlockEntity lv7;
            BlockPos lv3 = lv2.pos;
            if (lv != null && !lv.contains(lv3)) continue;
            FluidState lv4 = placementData.shouldPlaceFluids() ? arg.getFluidState(lv3) : null;
            BlockState lv5 = lv2.state.mirror(placementData.getMirror()).rotate(placementData.getRotation());
            if (lv2.tag != null) {
                BlockEntity lv6 = arg.getBlockEntity(lv3);
                Clearable.clear(lv6);
                arg.setBlockState(lv3, Blocks.BARRIER.getDefaultState(), 20);
            }
            if (!arg.setBlockState(lv3, lv5, i)) continue;
            j = Math.min(j, lv3.getX());
            k = Math.min(k, lv3.getY());
            l = Math.min(l, lv3.getZ());
            m = Math.max(m, lv3.getX());
            n = Math.max(n, lv3.getY());
            o = Math.max(o, lv3.getZ());
            list3.add(Pair.of((Object)lv3, (Object)lv2.tag));
            if (lv2.tag != null && (lv7 = arg.getBlockEntity(lv3)) != null) {
                lv2.tag.putInt("x", lv3.getX());
                lv2.tag.putInt("y", lv3.getY());
                lv2.tag.putInt("z", lv3.getZ());
                if (lv7 instanceof LootableContainerBlockEntity) {
                    lv2.tag.putLong("LootTableSeed", random.nextLong());
                }
                lv7.fromTag(lv2.state, lv2.tag);
                lv7.applyMirror(placementData.getMirror());
                lv7.applyRotation(placementData.getRotation());
            }
            if (lv4 == null || !(lv5.getBlock() instanceof FluidFillable)) continue;
            ((FluidFillable)((Object)lv5.getBlock())).tryFillWithFluid(arg, lv3, lv5, lv4);
            if (lv4.isStill()) continue;
            list2.add(lv3);
        }
        boolean bl = true;
        Direction[] lvs = new Direction[]{Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
        while (bl && !list2.isEmpty()) {
            bl = false;
            Iterator iterator = list2.iterator();
            while (iterator.hasNext()) {
                BlockState lv13;
                Block lv14;
                BlockPos lv8;
                BlockPos lv9 = lv8 = (BlockPos)iterator.next();
                FluidState lv10 = arg.getFluidState(lv9);
                for (int p = 0; p < lvs.length && !lv10.isStill(); ++p) {
                    BlockPos lv11 = lv9.offset(lvs[p]);
                    FluidState lv12 = arg.getFluidState(lv11);
                    if (!(lv12.getHeight(arg, lv11) > lv10.getHeight(arg, lv9)) && (!lv12.isStill() || lv10.isStill())) continue;
                    lv10 = lv12;
                    lv9 = lv11;
                }
                if (!lv10.isStill() || !((lv14 = (lv13 = arg.getBlockState(lv8)).getBlock()) instanceof FluidFillable)) continue;
                ((FluidFillable)((Object)lv14)).tryFillWithFluid(arg, lv8, lv13, lv10);
                bl = true;
                iterator.remove();
            }
        }
        if (j <= m) {
            if (!placementData.shouldUpdateNeighbors()) {
                BitSetVoxelSet lv15 = new BitSetVoxelSet(m - j + 1, n - k + 1, o - l + 1);
                int q = j;
                int r = k;
                int s = l;
                for (Pair pair : list3) {
                    BlockPos lv16 = (BlockPos)pair.getFirst();
                    ((VoxelSet)lv15).set(lv16.getX() - q, lv16.getY() - r, lv16.getZ() - s, true, true);
                }
                Structure.updateCorner(arg, i, lv15, q, r, s);
            }
            for (Pair pair2 : list3) {
                BlockEntity lv20;
                BlockPos lv17 = (BlockPos)pair2.getFirst();
                if (!placementData.shouldUpdateNeighbors()) {
                    BlockState lv19;
                    BlockState lv18 = arg.getBlockState(lv17);
                    if (lv18 != (lv19 = Block.postProcessState(lv18, arg, lv17))) {
                        arg.setBlockState(lv17, lv19, i & 0xFFFFFFFE | 0x10);
                    }
                    arg.updateNeighbors(lv17, lv19.getBlock());
                }
                if (pair2.getSecond() == null || (lv20 = arg.getBlockEntity(lv17)) == null) continue;
                lv20.markDirty();
            }
        }
        if (!placementData.shouldIgnoreEntities()) {
            this.spawnEntities(arg, pos, placementData.getMirror(), placementData.getRotation(), placementData.getPosition(), lv, placementData.method_27265());
        }
        return true;
    }

    public static void updateCorner(WorldAccess world, int flags, VoxelSet arg22, int startX, int startY, int startZ) {
        arg22.forEachDirection((arg2, m, n, o) -> {
            BlockState lv6;
            BlockState lv4;
            BlockState lv5;
            BlockPos lv = new BlockPos(startX + m, startY + n, startZ + o);
            BlockPos lv2 = lv.offset(arg2);
            BlockState lv3 = world.getBlockState(lv);
            if (lv3 != (lv5 = lv3.getStateForNeighborUpdate(arg2, lv4 = world.getBlockState(lv2), world, lv, lv2))) {
                world.setBlockState(lv, lv5, flags & 0xFFFFFFFE);
            }
            if (lv4 != (lv6 = lv4.getStateForNeighborUpdate(arg2.getOpposite(), lv5, world, lv2, lv))) {
                world.setBlockState(lv2, lv6, flags & 0xFFFFFFFE);
            }
        });
    }

    public static List<StructureBlockInfo> process(WorldAccess world, BlockPos pos, BlockPos arg3, StructurePlacementData arg4, List<StructureBlockInfo> list) {
        ArrayList list2 = Lists.newArrayList();
        for (StructureBlockInfo lv : list) {
            BlockPos lv2 = Structure.transform(arg4, lv.pos).add(pos);
            StructureBlockInfo lv3 = new StructureBlockInfo(lv2, lv.state, lv.tag != null ? lv.tag.copy() : null);
            Iterator<StructureProcessor> iterator = arg4.getProcessors().iterator();
            while (lv3 != null && iterator.hasNext()) {
                lv3 = iterator.next().process(world, pos, arg3, lv, lv3, arg4);
            }
            if (lv3 == null) continue;
            list2.add(lv3);
        }
        return list2;
    }

    private void spawnEntities(class_5425 arg, BlockPos pos, BlockMirror arg3, BlockRotation arg4, BlockPos pivot, @Nullable BlockBox area, boolean bl) {
        for (StructureEntityInfo lv : this.entities) {
            BlockPos lv2 = Structure.transformAround(lv.blockPos, arg3, arg4, pivot).add(pos);
            if (area != null && !area.contains(lv2)) continue;
            CompoundTag lv3 = lv.tag.copy();
            Vec3d lv4 = Structure.transformAround(lv.pos, arg3, arg4, pivot);
            Vec3d lv5 = lv4.add(pos.getX(), pos.getY(), pos.getZ());
            ListTag lv6 = new ListTag();
            lv6.add(DoubleTag.of(lv5.x));
            lv6.add(DoubleTag.of(lv5.y));
            lv6.add(DoubleTag.of(lv5.z));
            lv3.put("Pos", lv6);
            lv3.remove("UUID");
            Structure.getEntity(arg, lv3).ifPresent(arg6 -> {
                float f = arg6.applyMirror(arg3);
                arg6.refreshPositionAndAngles(arg3.x, arg3.y, arg3.z, f += arg6.yaw - arg6.applyRotation(arg4), arg6.pitch);
                if (bl && arg6 instanceof MobEntity) {
                    ((MobEntity)arg6).initialize(arg, arg.getLocalDifficulty(new BlockPos(lv5)), SpawnReason.STRUCTURE, null, lv3);
                }
                arg.spawnEntity((Entity)arg6);
            });
        }
    }

    private static Optional<Entity> getEntity(class_5425 arg, CompoundTag arg2) {
        try {
            return EntityType.getEntityFromTag(arg2, arg.getWorld());
        }
        catch (Exception exception) {
            return Optional.empty();
        }
    }

    public BlockPos getRotatedSize(BlockRotation arg) {
        switch (arg) {
            case COUNTERCLOCKWISE_90: 
            case CLOCKWISE_90: {
                return new BlockPos(this.size.getZ(), this.size.getY(), this.size.getX());
            }
        }
        return this.size;
    }

    public static BlockPos transformAround(BlockPos pos, BlockMirror arg2, BlockRotation arg3, BlockPos pivot) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        boolean bl = true;
        switch (arg2) {
            case LEFT_RIGHT: {
                k = -k;
                break;
            }
            case FRONT_BACK: {
                i = -i;
                break;
            }
            default: {
                bl = false;
            }
        }
        int l = pivot.getX();
        int m = pivot.getZ();
        switch (arg3) {
            case CLOCKWISE_180: {
                return new BlockPos(l + l - i, j, m + m - k);
            }
            case COUNTERCLOCKWISE_90: {
                return new BlockPos(l - m + k, j, l + m - i);
            }
            case CLOCKWISE_90: {
                return new BlockPos(l + m - k, j, m - l + i);
            }
        }
        return bl ? new BlockPos(i, j, k) : pos;
    }

    public static Vec3d transformAround(Vec3d point, BlockMirror arg2, BlockRotation arg3, BlockPos pivot) {
        double d = point.x;
        double e = point.y;
        double f = point.z;
        boolean bl = true;
        switch (arg2) {
            case LEFT_RIGHT: {
                f = 1.0 - f;
                break;
            }
            case FRONT_BACK: {
                d = 1.0 - d;
                break;
            }
            default: {
                bl = false;
            }
        }
        int i = pivot.getX();
        int j = pivot.getZ();
        switch (arg3) {
            case CLOCKWISE_180: {
                return new Vec3d((double)(i + i + 1) - d, e, (double)(j + j + 1) - f);
            }
            case COUNTERCLOCKWISE_90: {
                return new Vec3d((double)(i - j) + f, e, (double)(i + j + 1) - d);
            }
            case CLOCKWISE_90: {
                return new Vec3d((double)(i + j + 1) - f, e, (double)(j - i) + d);
            }
        }
        return bl ? new Vec3d(d, e, f) : point;
    }

    public BlockPos offsetByTransformedSize(BlockPos arg, BlockMirror arg2, BlockRotation arg3) {
        return Structure.applyTransformedOffset(arg, arg2, arg3, this.getSize().getX(), this.getSize().getZ());
    }

    public static BlockPos applyTransformedOffset(BlockPos arg, BlockMirror arg2, BlockRotation arg3, int offsetX, int offsetZ) {
        int k = arg2 == BlockMirror.FRONT_BACK ? --offsetX : 0;
        int l = arg2 == BlockMirror.LEFT_RIGHT ? --offsetZ : 0;
        BlockPos lv = arg;
        switch (arg3) {
            case NONE: {
                lv = arg.add(k, 0, l);
                break;
            }
            case CLOCKWISE_90: {
                lv = arg.add(offsetZ - l, 0, k);
                break;
            }
            case CLOCKWISE_180: {
                lv = arg.add(offsetX - k, 0, offsetZ - l);
                break;
            }
            case COUNTERCLOCKWISE_90: {
                lv = arg.add(l, 0, offsetX - k);
            }
        }
        return lv;
    }

    public BlockBox calculateBoundingBox(StructurePlacementData arg, BlockPos pos) {
        return this.method_27267(pos, arg.getRotation(), arg.getPosition(), arg.getMirror());
    }

    public BlockBox method_27267(BlockPos arg, BlockRotation arg2, BlockPos arg3, BlockMirror arg4) {
        BlockPos lv = this.getRotatedSize(arg2);
        int i = arg3.getX();
        int j = arg3.getZ();
        int k = lv.getX() - 1;
        int l = lv.getY() - 1;
        int m = lv.getZ() - 1;
        BlockBox lv2 = new BlockBox(0, 0, 0, 0, 0, 0);
        switch (arg2) {
            case NONE: {
                lv2 = new BlockBox(0, 0, 0, k, l, m);
                break;
            }
            case CLOCKWISE_180: {
                lv2 = new BlockBox(i + i - k, 0, j + j - m, i + i, l, j + j);
                break;
            }
            case COUNTERCLOCKWISE_90: {
                lv2 = new BlockBox(i - j, 0, i + j - m, i - j + k, l, i + j);
                break;
            }
            case CLOCKWISE_90: {
                lv2 = new BlockBox(i + j - k, 0, j - i, i + j, l, j - i + m);
            }
        }
        switch (arg4) {
            case NONE: {
                break;
            }
            case FRONT_BACK: {
                this.mirrorBoundingBox(arg2, k, m, lv2, Direction.WEST, Direction.EAST);
                break;
            }
            case LEFT_RIGHT: {
                this.mirrorBoundingBox(arg2, m, k, lv2, Direction.NORTH, Direction.SOUTH);
            }
        }
        lv2.offset(arg.getX(), arg.getY(), arg.getZ());
        return lv2;
    }

    private void mirrorBoundingBox(BlockRotation rotation, int offsetX, int offsetZ, BlockBox boundingBox, Direction arg3, Direction arg4) {
        BlockPos lv = BlockPos.ORIGIN;
        lv = rotation == BlockRotation.CLOCKWISE_90 || rotation == BlockRotation.COUNTERCLOCKWISE_90 ? lv.offset(rotation.rotate(arg3), offsetZ) : (rotation == BlockRotation.CLOCKWISE_180 ? lv.offset(arg4, offsetX) : lv.offset(arg3, offsetX));
        boundingBox.offset(lv.getX(), 0, lv.getZ());
    }

    public CompoundTag toTag(CompoundTag tag) {
        if (this.blockInfoLists.isEmpty()) {
            tag.put("blocks", new ListTag());
            tag.put("palette", new ListTag());
        } else {
            ArrayList list = Lists.newArrayList();
            Palette lv = new Palette();
            list.add(lv);
            for (int i = 1; i < this.blockInfoLists.size(); ++i) {
                list.add(new Palette());
            }
            ListTag lv2 = new ListTag();
            List<StructureBlockInfo> list2 = this.blockInfoLists.get(0).getAll();
            for (int j = 0; j < list2.size(); ++j) {
                StructureBlockInfo lv3 = list2.get(j);
                CompoundTag lv4 = new CompoundTag();
                lv4.put("pos", this.createIntListTag(lv3.pos.getX(), lv3.pos.getY(), lv3.pos.getZ()));
                int k = lv.getId(lv3.state);
                lv4.putInt("state", k);
                if (lv3.tag != null) {
                    lv4.put("nbt", lv3.tag);
                }
                lv2.add(lv4);
                for (int l = 1; l < this.blockInfoLists.size(); ++l) {
                    Palette lv5 = (Palette)list.get(l);
                    lv5.set(this.blockInfoLists.get((int)l).getAll().get((int)j).state, k);
                }
            }
            tag.put("blocks", lv2);
            if (list.size() == 1) {
                ListTag lv6 = new ListTag();
                for (BlockState lv7 : lv) {
                    lv6.add(NbtHelper.fromBlockState(lv7));
                }
                tag.put("palette", lv6);
            } else {
                ListTag lv8 = new ListTag();
                for (Palette lv9 : list) {
                    ListTag lv10 = new ListTag();
                    for (BlockState lv11 : lv9) {
                        lv10.add(NbtHelper.fromBlockState(lv11));
                    }
                    lv8.add(lv10);
                }
                tag.put("palettes", lv8);
            }
        }
        ListTag lv12 = new ListTag();
        for (StructureEntityInfo lv13 : this.entities) {
            CompoundTag lv14 = new CompoundTag();
            lv14.put("pos", this.createDoubleListTag(lv13.pos.x, lv13.pos.y, lv13.pos.z));
            lv14.put("blockPos", this.createIntListTag(lv13.blockPos.getX(), lv13.blockPos.getY(), lv13.blockPos.getZ()));
            if (lv13.tag != null) {
                lv14.put("nbt", lv13.tag);
            }
            lv12.add(lv14);
        }
        tag.put("entities", lv12);
        tag.put("size", this.createIntListTag(this.size.getX(), this.size.getY(), this.size.getZ()));
        tag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
        return tag;
    }

    public void fromTag(CompoundTag tag) {
        this.blockInfoLists.clear();
        this.entities.clear();
        ListTag lv = tag.getList("size", 3);
        this.size = new BlockPos(lv.getInt(0), lv.getInt(1), lv.getInt(2));
        ListTag lv2 = tag.getList("blocks", 10);
        if (tag.contains("palettes", 9)) {
            ListTag lv3 = tag.getList("palettes", 9);
            for (int i = 0; i < lv3.size(); ++i) {
                this.loadPalettedBlockInfo(lv3.getList(i), lv2);
            }
        } else {
            this.loadPalettedBlockInfo(tag.getList("palette", 10), lv2);
        }
        ListTag lv4 = tag.getList("entities", 10);
        for (int j = 0; j < lv4.size(); ++j) {
            CompoundTag lv5 = lv4.getCompound(j);
            ListTag lv6 = lv5.getList("pos", 6);
            Vec3d lv7 = new Vec3d(lv6.getDouble(0), lv6.getDouble(1), lv6.getDouble(2));
            ListTag lv8 = lv5.getList("blockPos", 3);
            BlockPos lv9 = new BlockPos(lv8.getInt(0), lv8.getInt(1), lv8.getInt(2));
            if (!lv5.contains("nbt")) continue;
            CompoundTag lv10 = lv5.getCompound("nbt");
            this.entities.add(new StructureEntityInfo(lv7, lv9, lv10));
        }
    }

    private void loadPalettedBlockInfo(ListTag paletteTag, ListTag blocksTag) {
        Palette lv = new Palette();
        for (int i = 0; i < paletteTag.size(); ++i) {
            lv.set(NbtHelper.toBlockState(paletteTag.getCompound(i)), i);
        }
        ArrayList list = Lists.newArrayList();
        ArrayList list2 = Lists.newArrayList();
        ArrayList list3 = Lists.newArrayList();
        for (int j = 0; j < blocksTag.size(); ++j) {
            CompoundTag lv7;
            CompoundTag lv2 = blocksTag.getCompound(j);
            ListTag lv3 = lv2.getList("pos", 3);
            BlockPos lv4 = new BlockPos(lv3.getInt(0), lv3.getInt(1), lv3.getInt(2));
            BlockState lv5 = lv.getState(lv2.getInt("state"));
            if (lv2.contains("nbt")) {
                CompoundTag lv6 = lv2.getCompound("nbt");
            } else {
                lv7 = null;
            }
            StructureBlockInfo lv8 = new StructureBlockInfo(lv4, lv5, lv7);
            Structure.method_28054(lv8, list, list2, list3);
        }
        List<StructureBlockInfo> list4 = Structure.method_28055(list, list2, list3);
        this.blockInfoLists.add(new PalettedBlockInfoList(list4));
    }

    private ListTag createIntListTag(int ... is) {
        ListTag lv = new ListTag();
        for (int i : is) {
            lv.add(IntTag.of(i));
        }
        return lv;
    }

    private ListTag createDoubleListTag(double ... ds) {
        ListTag lv = new ListTag();
        for (double d : ds) {
            lv.add(DoubleTag.of(d));
        }
        return lv;
    }

    public static final class PalettedBlockInfoList {
        private final List<StructureBlockInfo> infos;
        private final Map<Block, List<StructureBlockInfo>> blockToInfos = Maps.newHashMap();

        private PalettedBlockInfoList(List<StructureBlockInfo> infos) {
            this.infos = infos;
        }

        public List<StructureBlockInfo> getAll() {
            return this.infos;
        }

        public List<StructureBlockInfo> getAllOf(Block block) {
            return this.blockToInfos.computeIfAbsent(block, arg -> this.infos.stream().filter(arg2 -> arg2.state.isOf((Block)arg)).collect(Collectors.toList()));
        }
    }

    public static class StructureEntityInfo {
        public final Vec3d pos;
        public final BlockPos blockPos;
        public final CompoundTag tag;

        public StructureEntityInfo(Vec3d pos, BlockPos blockPos, CompoundTag tag) {
            this.pos = pos;
            this.blockPos = blockPos;
            this.tag = tag;
        }
    }

    public static class StructureBlockInfo {
        public final BlockPos pos;
        public final BlockState state;
        public final CompoundTag tag;

        public StructureBlockInfo(BlockPos pos, BlockState state, @Nullable CompoundTag tag) {
            this.pos = pos;
            this.state = state;
            this.tag = tag;
        }

        public String toString() {
            return String.format("<StructureBlockInfo | %s | %s | %s>", this.pos, this.state, this.tag);
        }
    }

    static class Palette
    implements Iterable<BlockState> {
        public static final BlockState AIR = Blocks.AIR.getDefaultState();
        private final IdList<BlockState> ids = new IdList(16);
        private int currentIndex;

        private Palette() {
        }

        public int getId(BlockState state) {
            int i = this.ids.getRawId(state);
            if (i == -1) {
                i = this.currentIndex++;
                this.ids.set(state, i);
            }
            return i;
        }

        @Nullable
        public BlockState getState(int id) {
            BlockState lv = this.ids.get(id);
            return lv == null ? AIR : lv;
        }

        @Override
        public Iterator<BlockState> iterator() {
            return this.ids.iterator();
        }

        public void set(BlockState state, int id) {
            this.ids.set(state, id);
        }
    }
}

