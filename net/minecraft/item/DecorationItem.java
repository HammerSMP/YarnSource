/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class DecorationItem
extends Item {
    private final EntityType<? extends AbstractDecorationEntity> entityType;

    public DecorationItem(EntityType<? extends AbstractDecorationEntity> arg, Item.Settings arg2) {
        super(arg2);
        this.entityType = arg;
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public ActionResult useOnBlock(ItemUsageContext arg) {
        void lv9;
        BlockPos lv = arg.getBlockPos();
        Direction lv2 = arg.getSide();
        BlockPos lv3 = lv.offset(lv2);
        PlayerEntity lv4 = arg.getPlayer();
        ItemStack lv5 = arg.getStack();
        if (lv4 != null && !this.canPlaceOn(lv4, lv2, lv5, lv3)) {
            return ActionResult.FAIL;
        }
        World lv6 = arg.getWorld();
        if (this.entityType == EntityType.PAINTING) {
            PaintingEntity lv7 = new PaintingEntity(lv6, lv3, lv2);
        } else if (this.entityType == EntityType.ITEM_FRAME) {
            ItemFrameEntity lv8 = new ItemFrameEntity(lv6, lv3, lv2);
        } else {
            return ActionResult.method_29236(lv6.isClient);
        }
        CompoundTag lv10 = lv5.getTag();
        if (lv10 != null) {
            EntityType.loadFromEntityTag(lv6, lv4, (Entity)lv9, lv10);
        }
        if (lv9.canStayAttached()) {
            if (!lv6.isClient) {
                lv9.onPlace();
                lv6.spawnEntity((Entity)lv9);
            }
            lv5.decrement(1);
            return ActionResult.method_29236(lv6.isClient);
        }
        return ActionResult.CONSUME;
    }

    protected boolean canPlaceOn(PlayerEntity arg, Direction arg2, ItemStack arg3, BlockPos arg4) {
        return !arg2.getAxis().isVertical() && arg.canPlaceOn(arg4, arg2, arg3);
    }
}

