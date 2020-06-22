/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.item;

import java.util.List;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public class GlassBottleItem
extends Item {
    public GlassBottleItem(Item.Settings arg) {
        super(arg);
    }

    @Override
    public TypedActionResult<ItemStack> use(World arg2, PlayerEntity arg22, Hand arg3) {
        List<AreaEffectCloudEntity> list = arg2.getEntities(AreaEffectCloudEntity.class, arg22.getBoundingBox().expand(2.0), arg -> arg != null && arg.isAlive() && arg.getOwner() instanceof EnderDragonEntity);
        ItemStack lv = arg22.getStackInHand(arg3);
        if (!list.isEmpty()) {
            AreaEffectCloudEntity lv2 = list.get(0);
            lv2.setRadius(lv2.getRadius() - 0.5f);
            arg2.playSound(null, arg22.getX(), arg22.getY(), arg22.getZ(), SoundEvents.ITEM_BOTTLE_FILL_DRAGONBREATH, SoundCategory.NEUTRAL, 1.0f, 1.0f);
            return TypedActionResult.method_29237(this.fill(lv, arg22, new ItemStack(Items.DRAGON_BREATH)), arg2.isClient());
        }
        BlockHitResult lv3 = GlassBottleItem.rayTrace(arg2, arg22, RayTraceContext.FluidHandling.SOURCE_ONLY);
        if (((HitResult)lv3).getType() == HitResult.Type.MISS) {
            return TypedActionResult.pass(lv);
        }
        if (((HitResult)lv3).getType() == HitResult.Type.BLOCK) {
            BlockPos lv4 = lv3.getBlockPos();
            if (!arg2.canPlayerModifyAt(arg22, lv4)) {
                return TypedActionResult.pass(lv);
            }
            if (arg2.getFluidState(lv4).isIn(FluidTags.WATER)) {
                arg2.playSound(arg22, arg22.getX(), arg22.getY(), arg22.getZ(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0f, 1.0f);
                return TypedActionResult.method_29237(this.fill(lv, arg22, PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER)), arg2.isClient());
            }
        }
        return TypedActionResult.pass(lv);
    }

    protected ItemStack fill(ItemStack arg, PlayerEntity arg2, ItemStack arg3) {
        arg.decrement(1);
        arg2.incrementStat(Stats.USED.getOrCreateStat(this));
        if (arg.isEmpty()) {
            return arg3;
        }
        if (!arg2.inventory.insertStack(arg3)) {
            arg2.dropItem(arg3, false);
        }
        return arg;
    }
}

