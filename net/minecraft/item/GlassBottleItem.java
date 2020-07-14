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
import net.minecraft.item.ItemUsage;
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
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        List<AreaEffectCloudEntity> list = world.getEntities(AreaEffectCloudEntity.class, user.getBoundingBox().expand(2.0), entity -> entity != null && entity.isAlive() && entity.getOwner() instanceof EnderDragonEntity);
        ItemStack lv = user.getStackInHand(hand);
        if (!list.isEmpty()) {
            AreaEffectCloudEntity lv2 = list.get(0);
            lv2.setRadius(lv2.getRadius() - 0.5f);
            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_BOTTLE_FILL_DRAGONBREATH, SoundCategory.NEUTRAL, 1.0f, 1.0f);
            return TypedActionResult.method_29237(this.fill(lv, user, new ItemStack(Items.DRAGON_BREATH)), world.isClient());
        }
        BlockHitResult lv3 = GlassBottleItem.rayTrace(world, user, RayTraceContext.FluidHandling.SOURCE_ONLY);
        if (((HitResult)lv3).getType() == HitResult.Type.MISS) {
            return TypedActionResult.pass(lv);
        }
        if (((HitResult)lv3).getType() == HitResult.Type.BLOCK) {
            BlockPos lv4 = lv3.getBlockPos();
            if (!world.canPlayerModifyAt(user, lv4)) {
                return TypedActionResult.pass(lv);
            }
            if (world.getFluidState(lv4).isIn(FluidTags.WATER)) {
                world.playSound(user, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0f, 1.0f);
                return TypedActionResult.method_29237(this.fill(lv, user, PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER)), world.isClient());
            }
        }
        return TypedActionResult.pass(lv);
    }

    protected ItemStack fill(ItemStack arg, PlayerEntity arg2, ItemStack arg3) {
        arg2.incrementStat(Stats.USED.getOrCreateStat(this));
        return ItemUsage.method_30012(arg, arg2, arg3);
    }
}

