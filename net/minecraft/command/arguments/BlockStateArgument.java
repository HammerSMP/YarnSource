/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.command.arguments;

import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;

public class BlockStateArgument
implements Predicate<CachedBlockPosition> {
    private final BlockState state;
    private final Set<Property<?>> properties;
    @Nullable
    private final CompoundTag data;

    public BlockStateArgument(BlockState arg, Set<Property<?>> set, @Nullable CompoundTag arg2) {
        this.state = arg;
        this.properties = set;
        this.data = arg2;
    }

    public BlockState getBlockState() {
        return this.state;
    }

    @Override
    public boolean test(CachedBlockPosition arg) {
        BlockState lv = arg.getBlockState();
        if (!lv.isOf(this.state.getBlock())) {
            return false;
        }
        for (Property<?> lv2 : this.properties) {
            if (lv.get(lv2) == this.state.get(lv2)) continue;
            return false;
        }
        if (this.data != null) {
            BlockEntity lv3 = arg.getBlockEntity();
            return lv3 != null && NbtHelper.matches(this.data, lv3.toTag(new CompoundTag()), true);
        }
        return true;
    }

    public boolean setBlockState(ServerWorld arg, BlockPos arg2, int i) {
        BlockEntity lv;
        if (!arg.setBlockState(arg2, this.state, i)) {
            return false;
        }
        if (this.data != null && (lv = arg.getBlockEntity(arg2)) != null) {
            CompoundTag lv2 = this.data.copy();
            lv2.putInt("x", arg2.getX());
            lv2.putInt("y", arg2.getY());
            lv2.putInt("z", arg2.getZ());
            lv.fromTag(this.state, lv2);
        }
        return true;
    }

    @Override
    public /* synthetic */ boolean test(Object object) {
        return this.test((CachedBlockPosition)object);
    }
}
