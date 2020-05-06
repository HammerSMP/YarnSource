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
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
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
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class Structure {
    private final List<PalettedBlockInfoList> blockInfoLists = Lists.newArrayList();
    private final List<StructureEntityInfo> entities = Lists.newArrayList();
    private BlockPos size = BlockPos.ORIGIN;
    private String author = "?";

    public BlockPos getSize() {
        return this.size;
    }

    public void setAuthor(String string) {
        this.author = string;
    }

    public String getAuthor() {
        return this.author;
    }

    public void saveFromWorld(World arg, BlockPos arg2, BlockPos arg3, boolean bl, @Nullable Block arg4) {
        if (arg3.getX() < 1 || arg3.getY() < 1 || arg3.getZ() < 1) {
            return;
        }
        BlockPos lv = arg2.add(arg3).add(-1, -1, -1);
        ArrayList list = Lists.newArrayList();
        ArrayList list2 = Lists.newArrayList();
        ArrayList list3 = Lists.newArrayList();
        BlockPos lv2 = new BlockPos(Math.min(arg2.getX(), lv.getX()), Math.min(arg2.getY(), lv.getY()), Math.min(arg2.getZ(), lv.getZ()));
        BlockPos lv3 = new BlockPos(Math.max(arg2.getX(), lv.getX()), Math.max(arg2.getY(), lv.getY()), Math.max(arg2.getZ(), lv.getZ()));
        this.size = arg3;
        for (BlockPos lv4 : BlockPos.iterate(lv2, lv3)) {
            BlockPos lv5 = lv4.subtract(lv2);
            BlockState lv6 = arg.getBlockState(lv4);
            if (arg4 != null && arg4 == lv6.getBlock()) continue;
            BlockEntity lv7 = arg.getBlockEntity(lv4);
            if (lv7 != null) {
                CompoundTag lv8 = lv7.toTag(new CompoundTag());
                lv8.remove("x");
                lv8.remove("y");
                lv8.remove("z");
                list2.add(new StructureBlockInfo(lv5, lv6, lv8));
                continue;
            }
            if (lv6.isOpaqueFullCube(arg, lv4) || lv6.isFullCube(arg, lv4)) {
                list.add(new StructureBlockInfo(lv5, lv6, null));
                continue;
            }
            list3.add(new StructureBlockInfo(lv5, lv6, null));
        }
        ArrayList list4 = Lists.newArrayList();
        list4.addAll(list);
        list4.addAll(list2);
        list4.addAll(list3);
        this.blockInfoLists.clear();
        this.blockInfoLists.add(new PalettedBlockInfoList(list4));
        if (bl) {
            this.addEntitiesFromWorld(arg, lv2, lv3.add(1, 1, 1));
        } else {
            this.entities.clear();
        }
    }

    private void addEntitiesFromWorld(World arg2, BlockPos arg22, BlockPos arg3) {
        List<Entity> list = arg2.getEntities(Entity.class, new Box(arg22, arg3), arg -> !(arg instanceof PlayerEntity));
        this.entities.clear();
        for (Entity lv : list) {
            BlockPos lv5;
            Vec3d lv2 = new Vec3d(lv.getX() - (double)arg22.getX(), lv.getY() - (double)arg22.getY(), lv.getZ() - (double)arg22.getZ());
            CompoundTag lv3 = new CompoundTag();
            lv.saveToTag(lv3);
            if (lv instanceof PaintingEntity) {
                BlockPos lv4 = ((PaintingEntity)lv).getDecorationBlockPos().subtract(arg22);
            } else {
                lv5 = new BlockPos(lv2);
            }
            this.entities.add(new StructureEntityInfo(lv2, lv5, lv3));
        }
    }

    public List<StructureBlockInfo> getInfosForBlock(BlockPos arg, StructurePlacementData arg2, Block arg3) {
        return this.getInfosForBlock(arg, arg2, arg3, true);
    }

    public List<StructureBlockInfo> getInfosForBlock(BlockPos arg, StructurePlacementData arg2, Block arg3, boolean bl) {
        ArrayList list = Lists.newArrayList();
        BlockBox lv = arg2.getBoundingBox();
        if (this.blockInfoLists.isEmpty()) {
            return Collections.emptyList();
        }
        for (StructureBlockInfo lv2 : arg2.getRandomBlockInfos(this.blockInfoLists, arg).getAllOf(arg3)) {
            BlockPos lv3;
            BlockPos blockPos = lv3 = bl ? Structure.transform(arg2, lv2.pos).add(arg) : lv2.pos;
            if (lv != null && !lv.contains(lv3)) continue;
            list.add(new StructureBlockInfo(lv3, lv2.state.rotate(arg2.getRotation()), lv2.tag));
        }
        return list;
    }

    public BlockPos transformBox(StructurePlacementData arg, BlockPos arg2, StructurePlacementData arg3, BlockPos arg4) {
        BlockPos lv = Structure.transform(arg, arg2);
        BlockPos lv2 = Structure.transform(arg3, arg4);
        return lv.subtract(lv2);
    }

    public static BlockPos transform(StructurePlacementData arg, BlockPos arg2) {
        return Structure.transformAround(arg2, arg.getMirror(), arg.getRotation(), arg.getPosition());
    }

    public void place(IWorld arg, BlockPos arg2, StructurePlacementData arg3) {
        arg3.calculateBoundingBox();
        this.placeAndNotifyListeners(arg, arg2, arg3);
    }

    public void placeAndNotifyListeners(IWorld arg, BlockPos arg2, StructurePlacementData arg3) {
        this.place(arg, arg2, arg2, arg3, 2);
    }

    public boolean place(IWorld arg, BlockPos arg2, BlockPos arg3, StructurePlacementData arg4, int i) {
        if (this.blockInfoLists.isEmpty()) {
            return false;
        }
        List<StructureBlockInfo> list = arg4.getRandomBlockInfos(this.blockInfoLists, arg2).getAll();
        if (list.isEmpty() && (arg4.shouldIgnoreEntities() || this.entities.isEmpty()) || this.size.getX() < 1 || this.size.getY() < 1 || this.size.getZ() < 1) {
            return false;
        }
        BlockBox lv = arg4.getBoundingBox();
        ArrayList list2 = Lists.newArrayListWithCapacity((int)(arg4.shouldPlaceFluids() ? list.size() : 0));
        ArrayList list3 = Lists.newArrayListWithCapacity((int)list.size());
        int j = Integer.MAX_VALUE;
        int k = Integer.MAX_VALUE;
        int l = Integer.MAX_VALUE;
        int m = Integer.MIN_VALUE;
        int n = Integer.MIN_VALUE;
        int o = Integer.MIN_VALUE;
        List<StructureBlockInfo> list4 = Structure.process(arg, arg2, arg3, arg4, list);
        for (StructureBlockInfo lv2 : list4) {
            BlockEntity lv7;
            BlockPos lv3 = lv2.pos;
            if (lv != null && !lv.contains(lv3)) continue;
            FluidState lv4 = arg4.shouldPlaceFluids() ? arg.getFluidState(lv3) : null;
            BlockState lv5 = lv2.state.mirror(arg4.getMirror()).rotate(arg4.getRotation());
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
                lv7.fromTag(lv2.state, lv2.tag);
                lv7.applyMirror(arg4.getMirror());
                lv7.applyRotation(arg4.getRotation());
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
            if (!arg4.shouldUpdateNeighbors()) {
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
                if (!arg4.shouldUpdateNeighbors()) {
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
        if (!arg4.shouldIgnoreEntities()) {
            this.spawnEntities(arg, arg2, arg4.getMirror(), arg4.getRotation(), arg4.getPosition(), lv, arg4.method_27265());
        }
        return true;
    }

    public static void updateCorner(IWorld arg, int i, VoxelSet arg22, int j, int k, int l) {
        arg22.forEachDirection((arg2, m, n, o) -> {
            BlockState lv6;
            BlockState lv4;
            BlockState lv5;
            BlockPos lv = new BlockPos(j + m, k + n, l + o);
            BlockPos lv2 = lv.offset(arg2);
            BlockState lv3 = arg.getBlockState(lv);
            if (lv3 != (lv5 = lv3.getStateForNeighborUpdate(arg2, lv4 = arg.getBlockState(lv2), arg, lv, lv2))) {
                arg.setBlockState(lv, lv5, i & 0xFFFFFFFE);
            }
            if (lv4 != (lv6 = lv4.getStateForNeighborUpdate(arg2.getOpposite(), lv5, arg, lv2, lv))) {
                arg.setBlockState(lv2, lv6, i & 0xFFFFFFFE);
            }
        });
    }

    public static List<StructureBlockInfo> process(IWorld arg, BlockPos arg2, BlockPos arg3, StructurePlacementData arg4, List<StructureBlockInfo> list) {
        ArrayList list2 = Lists.newArrayList();
        for (StructureBlockInfo lv : list) {
            BlockPos lv2 = Structure.transform(arg4, lv.pos).add(arg2);
            StructureBlockInfo lv3 = new StructureBlockInfo(lv2, lv.state, lv.tag);
            Iterator<StructureProcessor> iterator = arg4.getProcessors().iterator();
            while (lv3 != null && iterator.hasNext()) {
                lv3 = iterator.next().process(arg, arg2, arg3, lv, lv3, arg4);
            }
            if (lv3 == null) continue;
            list2.add(lv3);
        }
        return list2;
    }

    private void spawnEntities(IWorld arg, BlockPos arg2, BlockMirror arg3, BlockRotation arg4, BlockPos arg5, @Nullable BlockBox arg62, boolean bl) {
        for (StructureEntityInfo lv : this.entities) {
            BlockPos lv2 = Structure.transformAround(lv.blockPos, arg3, arg4, arg5).add(arg2);
            if (arg62 != null && !arg62.contains(lv2)) continue;
            CompoundTag lv3 = lv.tag;
            Vec3d lv4 = Structure.transformAround(lv.pos, arg3, arg4, arg5);
            Vec3d lv5 = lv4.add(arg2.getX(), arg2.getY(), arg2.getZ());
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
                    ((MobEntity)arg6).initialize(arg, arg.getLocalDifficulty(new BlockPos(lv5)), SpawnType.STRUCTURE, null, lv3);
                }
                arg.spawnEntity((Entity)arg6);
            });
        }
    }

    private static Optional<Entity> getEntity(IWorld arg, CompoundTag arg2) {
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

    public static BlockPos transformAround(BlockPos arg, BlockMirror arg2, BlockRotation arg3, BlockPos arg4) {
        int i = arg.getX();
        int j = arg.getY();
        int k = arg.getZ();
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
        int l = arg4.getX();
        int m = arg4.getZ();
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
        return bl ? new BlockPos(i, j, k) : arg;
    }

    private static Vec3d transformAround(Vec3d arg, BlockMirror arg2, BlockRotation arg3, BlockPos arg4) {
        double d = arg.x;
        double e = arg.y;
        double f = arg.z;
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
        int i = arg4.getX();
        int j = arg4.getZ();
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
        return bl ? new Vec3d(d, e, f) : arg;
    }

    public BlockPos offsetByTransformedSize(BlockPos arg, BlockMirror arg2, BlockRotation arg3) {
        return Structure.applyTransformedOffset(arg, arg2, arg3, this.getSize().getX(), this.getSize().getZ());
    }

    public static BlockPos applyTransformedOffset(BlockPos arg, BlockMirror arg2, BlockRotation arg3, int i, int j) {
        int k = arg2 == BlockMirror.FRONT_BACK ? --i : 0;
        int l = arg2 == BlockMirror.LEFT_RIGHT ? --j : 0;
        BlockPos lv = arg;
        switch (arg3) {
            case NONE: {
                lv = arg.add(k, 0, l);
                break;
            }
            case CLOCKWISE_90: {
                lv = arg.add(j - l, 0, k);
                break;
            }
            case CLOCKWISE_180: {
                lv = arg.add(i - k, 0, j - l);
                break;
            }
            case COUNTERCLOCKWISE_90: {
                lv = arg.add(l, 0, i - k);
            }
        }
        return lv;
    }

    public BlockBox calculateBoundingBox(StructurePlacementData arg, BlockPos arg2) {
        return this.method_27267(arg2, arg.getRotation(), arg.getPosition(), arg.getMirror());
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

    private void mirrorBoundingBox(BlockRotation arg, int i, int j, BlockBox arg2, Direction arg3, Direction arg4) {
        BlockPos lv = BlockPos.ORIGIN;
        lv = arg == BlockRotation.CLOCKWISE_90 || arg == BlockRotation.COUNTERCLOCKWISE_90 ? lv.offset(arg.rotate(arg3), j) : (arg == BlockRotation.CLOCKWISE_180 ? lv.offset(arg4, i) : lv.offset(arg3, i));
        arg2.offset(lv.getX(), 0, lv.getZ());
    }

    public CompoundTag toTag(CompoundTag arg) {
        if (this.blockInfoLists.isEmpty()) {
            arg.put("blocks", new ListTag());
            arg.put("palette", new ListTag());
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
            arg.put("blocks", lv2);
            if (list.size() == 1) {
                ListTag lv6 = new ListTag();
                for (BlockState lv7 : lv) {
                    lv6.add(NbtHelper.fromBlockState(lv7));
                }
                arg.put("palette", lv6);
            } else {
                ListTag lv8 = new ListTag();
                for (Palette lv9 : list) {
                    ListTag lv10 = new ListTag();
                    for (BlockState lv11 : lv9) {
                        lv10.add(NbtHelper.fromBlockState(lv11));
                    }
                    lv8.add(lv10);
                }
                arg.put("palettes", lv8);
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
        arg.put("entities", lv12);
        arg.put("size", this.createIntListTag(this.size.getX(), this.size.getY(), this.size.getZ()));
        arg.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
        return arg;
    }

    public void fromTag(CompoundTag arg) {
        this.blockInfoLists.clear();
        this.entities.clear();
        ListTag lv = arg.getList("size", 3);
        this.size = new BlockPos(lv.getInt(0), lv.getInt(1), lv.getInt(2));
        ListTag lv2 = arg.getList("blocks", 10);
        if (arg.contains("palettes", 9)) {
            ListTag lv3 = arg.getList("palettes", 9);
            for (int i = 0; i < lv3.size(); ++i) {
                this.loadPalettedBlockInfo(lv3.getList(i), lv2);
            }
        } else {
            this.loadPalettedBlockInfo(arg.getList("palette", 10), lv2);
        }
        ListTag lv4 = arg.getList("entities", 10);
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

    private void loadPalettedBlockInfo(ListTag arg2, ListTag arg22) {
        Palette lv = new Palette();
        ArrayList list = Lists.newArrayList();
        for (int i = 0; i < arg2.size(); ++i) {
            lv.set(NbtHelper.toBlockState(arg2.getCompound(i)), i);
        }
        for (int j = 0; j < arg22.size(); ++j) {
            CompoundTag lv7;
            CompoundTag lv2 = arg22.getCompound(j);
            ListTag lv3 = lv2.getList("pos", 3);
            BlockPos lv4 = new BlockPos(lv3.getInt(0), lv3.getInt(1), lv3.getInt(2));
            BlockState lv5 = lv.getState(lv2.getInt("state"));
            if (lv2.contains("nbt")) {
                CompoundTag lv6 = lv2.getCompound("nbt");
            } else {
                lv7 = null;
            }
            list.add(new StructureBlockInfo(lv4, lv5, lv7));
        }
        list.sort(Comparator.comparingInt(arg -> arg.pos.getY()));
        this.blockInfoLists.add(new PalettedBlockInfoList(list));
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

        private PalettedBlockInfoList(List<StructureBlockInfo> list) {
            this.infos = list;
        }

        public List<StructureBlockInfo> getAll() {
            return this.infos;
        }

        public List<StructureBlockInfo> getAllOf(Block arg2) {
            return this.blockToInfos.computeIfAbsent(arg2, arg -> this.infos.stream().filter(arg2 -> arg2.state.isOf((Block)arg)).collect(Collectors.toList()));
        }
    }

    public static class StructureEntityInfo {
        public final Vec3d pos;
        public final BlockPos blockPos;
        public final CompoundTag tag;

        public StructureEntityInfo(Vec3d arg, BlockPos arg2, CompoundTag arg3) {
            this.pos = arg;
            this.blockPos = arg2;
            this.tag = arg3;
        }
    }

    public static class StructureBlockInfo {
        public final BlockPos pos;
        public final BlockState state;
        public final CompoundTag tag;

        public StructureBlockInfo(BlockPos arg, BlockState arg2, @Nullable CompoundTag arg3) {
            this.pos = arg;
            this.state = arg2;
            this.tag = arg3;
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

        public int getId(BlockState arg) {
            int i = this.ids.getId(arg);
            if (i == -1) {
                i = this.currentIndex++;
                this.ids.set(arg, i);
            }
            return i;
        }

        @Nullable
        public BlockState getState(int i) {
            BlockState lv = this.ids.get(i);
            return lv == null ? AIR : lv;
        }

        @Override
        public Iterator<BlockState> iterator() {
            return this.ids.iterator();
        }

        public void set(BlockState arg, int i) {
            this.ids.set(arg, i);
        }
    }
}

