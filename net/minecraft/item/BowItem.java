/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.item;

import java.util.function.Predicate;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.Vanishable;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class BowItem
extends RangedWeaponItem
implements Vanishable {
    public BowItem(Item.Settings arg) {
        super(arg);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        boolean bl2;
        int j;
        float f;
        if (!(user instanceof PlayerEntity)) {
            return;
        }
        PlayerEntity lv = (PlayerEntity)user;
        boolean bl = lv.abilities.creativeMode || EnchantmentHelper.getLevel(Enchantments.INFINITY, stack) > 0;
        ItemStack lv2 = lv.getArrowType(stack);
        if (lv2.isEmpty() && !bl) {
            return;
        }
        if (lv2.isEmpty()) {
            lv2 = new ItemStack(Items.ARROW);
        }
        if ((double)(f = BowItem.getPullProgress(j = this.getMaxUseTime(stack) - remainingUseTicks)) < 0.1) {
            return;
        }
        boolean bl3 = bl2 = bl && lv2.getItem() == Items.ARROW;
        if (!world.isClient) {
            int l;
            int k;
            ArrowItem lv3 = (ArrowItem)(lv2.getItem() instanceof ArrowItem ? lv2.getItem() : Items.ARROW);
            PersistentProjectileEntity lv4 = lv3.createArrow(world, lv2, lv);
            lv4.setProperties(lv, lv.pitch, lv.yaw, 0.0f, f * 3.0f, 1.0f);
            if (f == 1.0f) {
                lv4.setCritical(true);
            }
            if ((k = EnchantmentHelper.getLevel(Enchantments.POWER, stack)) > 0) {
                lv4.setDamage(lv4.getDamage() + (double)k * 0.5 + 0.5);
            }
            if ((l = EnchantmentHelper.getLevel(Enchantments.PUNCH, stack)) > 0) {
                lv4.setPunch(l);
            }
            if (EnchantmentHelper.getLevel(Enchantments.FLAME, stack) > 0) {
                lv4.setOnFireFor(100);
            }
            stack.damage(1, lv, p -> p.sendToolBreakStatus(lv.getActiveHand()));
            if (bl2 || lv.abilities.creativeMode && (lv2.getItem() == Items.SPECTRAL_ARROW || lv2.getItem() == Items.TIPPED_ARROW)) {
                lv4.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
            }
            world.spawnEntity(lv4);
        }
        world.playSound(null, lv.getX(), lv.getY(), lv.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0f, 1.0f / (RANDOM.nextFloat() * 0.4f + 1.2f) + f * 0.5f);
        if (!bl2 && !lv.abilities.creativeMode) {
            lv2.decrement(1);
            if (lv2.isEmpty()) {
                lv.inventory.removeOne(lv2);
            }
        }
        lv.incrementStat(Stats.USED.getOrCreateStat(this));
    }

    public static float getPullProgress(int useTicks) {
        float f = (float)useTicks / 20.0f;
        if ((f = (f * f + f * 2.0f) / 3.0f) > 1.0f) {
            f = 1.0f;
        }
        return f;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        boolean bl;
        ItemStack lv = user.getStackInHand(hand);
        boolean bl2 = bl = !user.getArrowType(lv).isEmpty();
        if (user.abilities.creativeMode || bl) {
            user.setCurrentHand(hand);
            return TypedActionResult.consume(lv);
        }
        return TypedActionResult.fail(lv);
    }

    @Override
    public Predicate<ItemStack> getProjectiles() {
        return BOW_PROJECTILES;
    }

    @Override
    public int getRange() {
        return 15;
    }
}

