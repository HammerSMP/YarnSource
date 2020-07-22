/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.DropperBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointerImpl;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public class DropperBlock
extends DispenserBlock {
    private static final DispenserBehavior BEHAVIOR = new ItemDispenserBehavior();

    public DropperBlock(AbstractBlock.Settings arg) {
        super(arg);
    }

    @Override
    protected DispenserBehavior getBehaviorForItem(ItemStack stack) {
        return BEHAVIOR;
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new DropperBlockEntity();
    }

    @Override
    protected void dispense(ServerWorld arg, BlockPos pos) {
        ItemStack lv7;
        BlockPointerImpl lv = new BlockPointerImpl(arg, pos);
        DispenserBlockEntity lv2 = (DispenserBlockEntity)lv.getBlockEntity();
        int i = lv2.chooseNonEmptySlot();
        if (i < 0) {
            arg.syncWorldEvent(1001, pos, 0);
            return;
        }
        ItemStack lv3 = lv2.getStack(i);
        if (lv3.isEmpty()) {
            return;
        }
        Direction lv4 = arg.getBlockState(pos).get(FACING);
        Inventory lv5 = HopperBlockEntity.getInventoryAt(arg, pos.offset(lv4));
        if (lv5 == null) {
            ItemStack lv6 = BEHAVIOR.dispense(lv, lv3);
        } else {
            lv7 = HopperBlockEntity.transfer(lv2, lv5, lv3.copy().split(1), lv4.getOpposite());
            if (lv7.isEmpty()) {
                lv7 = lv3.copy();
                lv7.decrement(1);
            } else {
                lv7 = lv3.copy();
            }
        }
        lv2.setStack(i, lv7);
    }
}

