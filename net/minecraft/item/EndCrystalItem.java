/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.item;

import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.dimension.TheEndDimension;

public class EndCrystalItem
extends Item {
    public EndCrystalItem(Item.Settings arg) {
        super(arg);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext arg) {
        double f;
        double e;
        BlockPos lv2;
        World lv = arg.getWorld();
        BlockState lv3 = lv.getBlockState(lv2 = arg.getBlockPos());
        if (!lv3.isOf(Blocks.OBSIDIAN) && !lv3.isOf(Blocks.BEDROCK)) {
            return ActionResult.FAIL;
        }
        BlockPos lv4 = lv2.up();
        if (!lv.isAir(lv4)) {
            return ActionResult.FAIL;
        }
        double d = lv4.getX();
        List<Entity> list = lv.getEntities(null, new Box(d, e = (double)lv4.getY(), f = (double)lv4.getZ(), d + 1.0, e + 2.0, f + 1.0));
        if (!list.isEmpty()) {
            return ActionResult.FAIL;
        }
        if (!lv.isClient) {
            EndCrystalEntity lv5 = new EndCrystalEntity(lv, d + 0.5, e, f + 0.5);
            lv5.setShowBottom(false);
            lv.spawnEntity(lv5);
            if (lv.getDimension() instanceof TheEndDimension) {
                EnderDragonFight lv6 = ((TheEndDimension)lv.getDimension()).getEnderDragonFight();
                lv6.respawnDragon();
            }
        }
        arg.getStack().decrement(1);
        return ActionResult.SUCCESS;
    }

    @Override
    public boolean hasEnchantmentGlint(ItemStack arg) {
        return true;
    }
}

