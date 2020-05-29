/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.item;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class MinecartItem
extends Item {
    private static final DispenserBehavior DISPENSER_BEHAVIOR = new ItemDispenserBehavior(){
        private final ItemDispenserBehavior defaultBehavior = new ItemDispenserBehavior();

        /*
         * WARNING - void declaration
         */
        @Override
        public ItemStack dispenseSilently(BlockPointer arg, ItemStack arg2) {
            void k;
            RailShape lv5;
            Direction lv = arg.getBlockState().get(DispenserBlock.FACING);
            World lv2 = arg.getWorld();
            double d = arg.getX() + (double)lv.getOffsetX() * 1.125;
            double e = Math.floor(arg.getY()) + (double)lv.getOffsetY();
            double f = arg.getZ() + (double)lv.getOffsetZ() * 1.125;
            BlockPos lv3 = arg.getBlockPos().offset(lv);
            BlockState lv4 = lv2.getBlockState(lv3);
            RailShape railShape = lv5 = lv4.getBlock() instanceof AbstractRailBlock ? lv4.get(((AbstractRailBlock)lv4.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
            if (lv4.isIn(BlockTags.RAILS)) {
                if (lv5.isAscending()) {
                    double g = 0.6;
                } else {
                    double h = 0.1;
                }
            } else if (lv4.isAir() && lv2.getBlockState(lv3.down()).isIn(BlockTags.RAILS)) {
                RailShape lv7;
                BlockState lv6 = lv2.getBlockState(lv3.down());
                RailShape railShape2 = lv7 = lv6.getBlock() instanceof AbstractRailBlock ? lv6.get(((AbstractRailBlock)lv6.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
                if (lv == Direction.DOWN || !lv7.isAscending()) {
                    double i = -0.9;
                } else {
                    double j = -0.4;
                }
            } else {
                return this.defaultBehavior.dispense(arg, arg2);
            }
            AbstractMinecartEntity lv8 = AbstractMinecartEntity.create(lv2, d, e + k, f, ((MinecartItem)arg2.getItem()).type);
            if (arg2.hasCustomName()) {
                lv8.setCustomName(arg2.getName());
            }
            lv2.spawnEntity(lv8);
            arg2.decrement(1);
            return arg2;
        }

        @Override
        protected void playSound(BlockPointer arg) {
            arg.getWorld().syncWorldEvent(1000, arg.getBlockPos(), 0);
        }
    };
    private final AbstractMinecartEntity.Type type;

    public MinecartItem(AbstractMinecartEntity.Type arg, Item.Settings arg2) {
        super(arg2);
        this.type = arg;
        DispenserBlock.registerBehavior(this, DISPENSER_BEHAVIOR);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext arg) {
        BlockPos lv2;
        World lv = arg.getWorld();
        BlockState lv3 = lv.getBlockState(lv2 = arg.getBlockPos());
        if (!lv3.isIn(BlockTags.RAILS)) {
            return ActionResult.FAIL;
        }
        ItemStack lv4 = arg.getStack();
        if (!lv.isClient) {
            RailShape lv5 = lv3.getBlock() instanceof AbstractRailBlock ? lv3.get(((AbstractRailBlock)lv3.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
            double d = 0.0;
            if (lv5.isAscending()) {
                d = 0.5;
            }
            AbstractMinecartEntity lv6 = AbstractMinecartEntity.create(lv, (double)lv2.getX() + 0.5, (double)lv2.getY() + 0.0625 + d, (double)lv2.getZ() + 0.5, this.type);
            if (lv4.hasCustomName()) {
                lv6.setCustomName(lv4.getName());
            }
            lv.spawnEntity(lv6);
        }
        lv4.decrement(1);
        return ActionResult.method_29236(lv.isClient);
    }
}

