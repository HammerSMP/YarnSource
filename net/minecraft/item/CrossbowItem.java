/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.Vanishable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CrossbowItem
extends RangedWeaponItem
implements Vanishable {
    private boolean charged = false;
    private boolean loaded = false;

    public CrossbowItem(Item.Settings arg) {
        super(arg);
    }

    @Override
    public Predicate<ItemStack> getHeldProjectiles() {
        return CROSSBOW_HELD_PROJECTILES;
    }

    @Override
    public Predicate<ItemStack> getProjectiles() {
        return BOW_PROJECTILES;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack lv = user.getStackInHand(hand);
        if (CrossbowItem.isCharged(lv)) {
            CrossbowItem.shootAll(world, user, hand, lv, CrossbowItem.getSpeed(lv), 1.0f);
            CrossbowItem.setCharged(lv, false);
            return TypedActionResult.consume(lv);
        }
        if (!user.getArrowType(lv).isEmpty()) {
            if (!CrossbowItem.isCharged(lv)) {
                this.charged = false;
                this.loaded = false;
                user.setCurrentHand(hand);
            }
            return TypedActionResult.consume(lv);
        }
        return TypedActionResult.fail(lv);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        int j = this.getMaxUseTime(stack) - remainingUseTicks;
        float f = CrossbowItem.getPullProgress(j, stack);
        if (f >= 1.0f && !CrossbowItem.isCharged(stack) && CrossbowItem.loadProjectiles(user, stack)) {
            CrossbowItem.setCharged(stack, true);
            SoundCategory lv = user instanceof PlayerEntity ? SoundCategory.PLAYERS : SoundCategory.HOSTILE;
            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_CROSSBOW_LOADING_END, lv, 1.0f, 1.0f / (RANDOM.nextFloat() * 0.5f + 1.0f) + 0.2f);
        }
    }

    private static boolean loadProjectiles(LivingEntity shooter, ItemStack projectile) {
        int i = EnchantmentHelper.getLevel(Enchantments.MULTISHOT, projectile);
        int j = i == 0 ? 1 : 3;
        boolean bl = shooter instanceof PlayerEntity && ((PlayerEntity)shooter).abilities.creativeMode;
        ItemStack lv = shooter.getArrowType(projectile);
        ItemStack lv2 = lv.copy();
        for (int k = 0; k < j; ++k) {
            if (k > 0) {
                lv = lv2.copy();
            }
            if (lv.isEmpty() && bl) {
                lv = new ItemStack(Items.ARROW);
                lv2 = lv.copy();
            }
            if (CrossbowItem.loadProjectile(shooter, projectile, lv, k > 0, bl)) continue;
            return false;
        }
        return true;
    }

    private static boolean loadProjectile(LivingEntity shooter, ItemStack crossbow, ItemStack projectile, boolean simulated, boolean creative) {
        ItemStack lv2;
        boolean bl3;
        if (projectile.isEmpty()) {
            return false;
        }
        boolean bl = bl3 = creative && projectile.getItem() instanceof ArrowItem;
        if (!(bl3 || creative || simulated)) {
            ItemStack lv = projectile.split(1);
            if (projectile.isEmpty() && shooter instanceof PlayerEntity) {
                ((PlayerEntity)shooter).inventory.removeOne(projectile);
            }
        } else {
            lv2 = projectile.copy();
        }
        CrossbowItem.putProjectile(crossbow, lv2);
        return true;
    }

    public static boolean isCharged(ItemStack stack) {
        CompoundTag lv = stack.getTag();
        return lv != null && lv.getBoolean("Charged");
    }

    public static void setCharged(ItemStack stack, boolean charged) {
        CompoundTag lv = stack.getOrCreateTag();
        lv.putBoolean("Charged", charged);
    }

    private static void putProjectile(ItemStack crossbow, ItemStack projectile) {
        ListTag lv3;
        CompoundTag lv = crossbow.getOrCreateTag();
        if (lv.contains("ChargedProjectiles", 9)) {
            ListTag lv2 = lv.getList("ChargedProjectiles", 10);
        } else {
            lv3 = new ListTag();
        }
        CompoundTag lv4 = new CompoundTag();
        projectile.toTag(lv4);
        lv3.add(lv4);
        lv.put("ChargedProjectiles", lv3);
    }

    private static List<ItemStack> getProjectiles(ItemStack crossbow) {
        ListTag lv2;
        ArrayList list = Lists.newArrayList();
        CompoundTag lv = crossbow.getTag();
        if (lv != null && lv.contains("ChargedProjectiles", 9) && (lv2 = lv.getList("ChargedProjectiles", 10)) != null) {
            for (int i = 0; i < lv2.size(); ++i) {
                CompoundTag lv3 = lv2.getCompound(i);
                list.add(ItemStack.fromTag(lv3));
            }
        }
        return list;
    }

    private static void clearProjectiles(ItemStack crossbow) {
        CompoundTag lv = crossbow.getTag();
        if (lv != null) {
            ListTag lv2 = lv.getList("ChargedProjectiles", 9);
            lv2.clear();
            lv.put("ChargedProjectiles", lv2);
        }
    }

    public static boolean hasProjectile(ItemStack crossbow, Item projectile) {
        return CrossbowItem.getProjectiles(crossbow).stream().anyMatch(s -> s.getItem() == projectile);
    }

    private static void shoot(World world, LivingEntity shooter, Hand hand, ItemStack crossbow, ItemStack projectile, float soundPitch, boolean creative, float speed, float divergence, float simulated) {
        PersistentProjectileEntity lv2;
        boolean bl2;
        if (world.isClient) {
            return;
        }
        boolean bl = bl2 = projectile.getItem() == Items.FIREWORK_ROCKET;
        if (bl2) {
            FireworkRocketEntity lv = new FireworkRocketEntity(world, projectile, shooter, shooter.getX(), shooter.getEyeY() - (double)0.15f, shooter.getZ(), true);
        } else {
            lv2 = CrossbowItem.createArrow(world, shooter, crossbow, projectile);
            if (creative || simulated != 0.0f) {
                lv2.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
            }
        }
        if (shooter instanceof CrossbowUser) {
            CrossbowUser lv3 = (CrossbowUser)((Object)shooter);
            lv3.shoot(lv3.getTarget(), crossbow, lv2, simulated);
        } else {
            Vec3d lv4 = shooter.getOppositeRotationVector(1.0f);
            Quaternion lv5 = new Quaternion(new Vector3f(lv4), simulated, true);
            Vec3d lv6 = shooter.getRotationVec(1.0f);
            Vector3f lv7 = new Vector3f(lv6);
            lv7.rotate(lv5);
            ((ProjectileEntity)lv2).setVelocity(lv7.getX(), lv7.getY(), lv7.getZ(), speed, divergence);
        }
        crossbow.damage(bl2 ? 3 : 1, shooter, e -> e.sendToolBreakStatus(hand));
        world.spawnEntity(lv2);
        world.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.ITEM_CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1.0f, soundPitch);
    }

    private static PersistentProjectileEntity createArrow(World world, LivingEntity entity, ItemStack crossbow, ItemStack arrow) {
        ArrowItem lv = (ArrowItem)(arrow.getItem() instanceof ArrowItem ? arrow.getItem() : Items.ARROW);
        PersistentProjectileEntity lv2 = lv.createArrow(world, arrow, entity);
        if (entity instanceof PlayerEntity) {
            lv2.setCritical(true);
        }
        lv2.setSound(SoundEvents.ITEM_CROSSBOW_HIT);
        lv2.setShotFromCrossbow(true);
        int i = EnchantmentHelper.getLevel(Enchantments.PIERCING, crossbow);
        if (i > 0) {
            lv2.setPierceLevel((byte)i);
        }
        return lv2;
    }

    public static void shootAll(World world, LivingEntity entity, Hand hand, ItemStack stack, float speed, float divergence) {
        List<ItemStack> list = CrossbowItem.getProjectiles(stack);
        float[] fs = CrossbowItem.getSoundPitches(entity.getRandom());
        for (int i = 0; i < list.size(); ++i) {
            boolean bl;
            ItemStack lv = list.get(i);
            boolean bl2 = bl = entity instanceof PlayerEntity && ((PlayerEntity)entity).abilities.creativeMode;
            if (lv.isEmpty()) continue;
            if (i == 0) {
                CrossbowItem.shoot(world, entity, hand, stack, lv, fs[i], bl, speed, divergence, 0.0f);
                continue;
            }
            if (i == 1) {
                CrossbowItem.shoot(world, entity, hand, stack, lv, fs[i], bl, speed, divergence, -10.0f);
                continue;
            }
            if (i != 2) continue;
            CrossbowItem.shoot(world, entity, hand, stack, lv, fs[i], bl, speed, divergence, 10.0f);
        }
        CrossbowItem.postShoot(world, entity, stack);
    }

    private static float[] getSoundPitches(Random random) {
        boolean bl = random.nextBoolean();
        return new float[]{1.0f, CrossbowItem.getSoundPitch(bl), CrossbowItem.getSoundPitch(!bl)};
    }

    private static float getSoundPitch(boolean flag) {
        float f = flag ? 0.63f : 0.43f;
        return 1.0f / (RANDOM.nextFloat() * 0.5f + 1.8f) + f;
    }

    private static void postShoot(World world, LivingEntity entity, ItemStack stack) {
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity lv = (ServerPlayerEntity)entity;
            if (!world.isClient) {
                Criteria.SHOT_CROSSBOW.trigger(lv, stack);
            }
            lv.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
        }
        CrossbowItem.clearProjectiles(stack);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!world.isClient) {
            int j = EnchantmentHelper.getLevel(Enchantments.QUICK_CHARGE, stack);
            SoundEvent lv = this.getQuickChargeSound(j);
            SoundEvent lv2 = j == 0 ? SoundEvents.ITEM_CROSSBOW_LOADING_MIDDLE : null;
            float f = (float)(stack.getMaxUseTime() - remainingUseTicks) / (float)CrossbowItem.getPullTime(stack);
            if (f < 0.2f) {
                this.charged = false;
                this.loaded = false;
            }
            if (f >= 0.2f && !this.charged) {
                this.charged = true;
                world.playSound(null, user.getX(), user.getY(), user.getZ(), lv, SoundCategory.PLAYERS, 0.5f, 1.0f);
            }
            if (f >= 0.5f && lv2 != null && !this.loaded) {
                this.loaded = true;
                world.playSound(null, user.getX(), user.getY(), user.getZ(), lv2, SoundCategory.PLAYERS, 0.5f, 1.0f);
            }
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return CrossbowItem.getPullTime(stack) + 3;
    }

    public static int getPullTime(ItemStack stack) {
        int i = EnchantmentHelper.getLevel(Enchantments.QUICK_CHARGE, stack);
        return i == 0 ? 25 : 25 - 5 * i;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.CROSSBOW;
    }

    private SoundEvent getQuickChargeSound(int stage) {
        switch (stage) {
            case 1: {
                return SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_1;
            }
            case 2: {
                return SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_2;
            }
            case 3: {
                return SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_3;
            }
        }
        return SoundEvents.ITEM_CROSSBOW_LOADING_START;
    }

    private static float getPullProgress(int useTicks, ItemStack stack) {
        float f = (float)useTicks / (float)CrossbowItem.getPullTime(stack);
        if (f > 1.0f) {
            f = 1.0f;
        }
        return f;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        List<ItemStack> list2 = CrossbowItem.getProjectiles(stack);
        if (!CrossbowItem.isCharged(stack) || list2.isEmpty()) {
            return;
        }
        ItemStack lv = list2.get(0);
        tooltip.add(new TranslatableText("item.minecraft.crossbow.projectile").append(" ").append(lv.toHoverableText()));
        if (context.isAdvanced() && lv.getItem() == Items.FIREWORK_ROCKET) {
            ArrayList list3 = Lists.newArrayList();
            Items.FIREWORK_ROCKET.appendTooltip(lv, world, list3, context);
            if (!list3.isEmpty()) {
                for (int i = 0; i < list3.size(); ++i) {
                    list3.set(i, new LiteralText("  ").append((Text)list3.get(i)).formatted(Formatting.GRAY));
                }
                tooltip.addAll(list3);
            }
        }
    }

    private static float getSpeed(ItemStack stack) {
        if (stack.getItem() == Items.CROSSBOW && CrossbowItem.hasProjectile(stack, Items.FIREWORK_ROCKET)) {
            return 1.6f;
        }
        return 3.15f;
    }

    @Override
    public int getRange() {
        return 8;
    }
}

