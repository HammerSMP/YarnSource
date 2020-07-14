/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class LeadItem
extends Item {
    public LeadItem(Item.Settings arg) {
        super(arg);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos lv2;
        World lv = context.getWorld();
        Block lv3 = lv.getBlockState(lv2 = context.getBlockPos()).getBlock();
        if (lv3.isIn(BlockTags.FENCES)) {
            PlayerEntity lv4 = context.getPlayer();
            if (!lv.isClient && lv4 != null) {
                LeadItem.attachHeldMobsToBlock(lv4, lv, lv2);
            }
            return ActionResult.success(lv.isClient);
        }
        return ActionResult.PASS;
    }

    public static ActionResult attachHeldMobsToBlock(PlayerEntity arg, World arg2, BlockPos arg3) {
        LeashKnotEntity lv = null;
        boolean bl = false;
        double d = 7.0;
        int i = arg3.getX();
        int j = arg3.getY();
        int k = arg3.getZ();
        List<MobEntity> list = arg2.getNonSpectatingEntities(MobEntity.class, new Box((double)i - 7.0, (double)j - 7.0, (double)k - 7.0, (double)i + 7.0, (double)j + 7.0, (double)k + 7.0));
        for (MobEntity lv2 : list) {
            if (lv2.getHoldingEntity() != arg) continue;
            if (lv == null) {
                lv = LeashKnotEntity.getOrCreate(arg2, arg3);
            }
            lv2.attachLeash(lv, true);
            bl = true;
        }
        return bl ? ActionResult.SUCCESS : ActionResult.PASS;
    }
}

