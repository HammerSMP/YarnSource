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
    public TypedActionResult<ItemStack> use(World arg, PlayerEntity arg2, Hand arg3) {
        ItemStack lv = arg2.getStackInHand(arg3);
        if (CrossbowItem.isCharged(lv)) {
            CrossbowItem.shootAll(arg, arg2, arg3, lv, CrossbowItem.getSpeed(lv), 1.0f);
            CrossbowItem.setCharged(lv, false);
            return TypedActionResult.consume(lv);
        }
        if (!arg2.getArrowType(lv).isEmpty()) {
            if (!CrossbowItem.isCharged(lv)) {
                this.charged = false;
                this.loaded = false;
                arg2.setCurrentHand(arg3);
            }
            return TypedActionResult.consume(lv);
        }
        return TypedActionResult.fail(lv);
    }

    @Override
    public void onStoppedUsing(ItemStack arg, World arg2, LivingEntity arg3, int i) {
        int j = this.getMaxUseTime(arg) - i;
        float f = CrossbowItem.getPullProgress(j, arg);
        if (f >= 1.0f && !CrossbowItem.isCharged(arg) && CrossbowItem.loadProjectiles(arg3, arg)) {
            CrossbowItem.setCharged(arg, true);
            SoundCategory lv = arg3 instanceof PlayerEntity ? SoundCategory.PLAYERS : SoundCategory.HOSTILE;
            arg2.playSound(null, arg3.getX(), arg3.getY(), arg3.getZ(), SoundEvents.ITEM_CROSSBOW_LOADING_END, lv, 1.0f, 1.0f / (RANDOM.nextFloat() * 0.5f + 1.0f) + 0.2f);
        }
    }

    private static boolean loadProjectiles(LivingEntity arg, ItemStack arg2) {
        int i = EnchantmentHelper.getLevel(Enchantments.MULTISHOT, arg2);
        int j = i == 0 ? 1 : 3;
        boolean bl = arg instanceof PlayerEntity && ((PlayerEntity)arg).abilities.creativeMode;
        ItemStack lv = arg.getArrowType(arg2);
        ItemStack lv2 = lv.copy();
        for (int k = 0; k < j; ++k) {
            if (k > 0) {
                lv = lv2.copy();
            }
            if (lv.isEmpty() && bl) {
                lv = new ItemStack(Items.ARROW);
                lv2 = lv.copy();
            }
            if (CrossbowItem.loadProjectile(arg, arg2, lv, k > 0, bl)) continue;
            return false;
        }
        return true;
    }

    private static boolean loadProjectile(LivingEntity arg, ItemStack arg2, ItemStack arg3, boolean bl, boolean bl2) {
        ItemStack lv2;
        boolean bl3;
        if (arg3.isEmpty()) {
            return false;
        }
        boolean bl4 = bl3 = bl2 && arg3.getItem() instanceof ArrowItem;
        if (!(bl3 || bl2 || bl)) {
            ItemStack lv = arg3.split(1);
            if (arg3.isEmpty() && arg instanceof PlayerEntity) {
                ((PlayerEntity)arg).inventory.removeOne(arg3);
            }
        } else {
            lv2 = arg3.copy();
        }
        CrossbowItem.putProjectile(arg2, lv2);
        return true;
    }

    public static boolean isCharged(ItemStack arg) {
        CompoundTag lv = arg.getTag();
        return lv != null && lv.getBoolean("Charged");
    }

    public static void setCharged(ItemStack arg, boolean bl) {
        CompoundTag lv = arg.getOrCreateTag();
        lv.putBoolean("Charged", bl);
    }

    private static void putProjectile(ItemStack arg, ItemStack arg2) {
        ListTag lv3;
        CompoundTag lv = arg.getOrCreateTag();
        if (lv.contains("ChargedProjectiles", 9)) {
            ListTag lv2 = lv.getList("ChargedProjectiles", 10);
        } else {
            lv3 = new ListTag();
        }
        CompoundTag lv4 = new CompoundTag();
        arg2.toTag(lv4);
        lv3.add(lv4);
        lv.put("ChargedProjectiles", lv3);
    }

    private static List<ItemStack> getProjectiles(ItemStack arg) {
        ListTag lv2;
        ArrayList list = Lists.newArrayList();
        CompoundTag lv = arg.getTag();
        if (lv != null && lv.contains("ChargedProjectiles", 9) && (lv2 = lv.getList("ChargedProjectiles", 10)) != null) {
            for (int i = 0; i < lv2.size(); ++i) {
                CompoundTag lv3 = lv2.getCompound(i);
                list.add(ItemStack.fromTag(lv3));
            }
        }
        return list;
    }

    private static void clearProjectiles(ItemStack arg) {
        CompoundTag lv = arg.getTag();
        if (lv != null) {
            ListTag lv2 = lv.getList("ChargedProjectiles", 9);
            lv2.clear();
            lv.put("ChargedProjectiles", lv2);
        }
    }

    public static boolean hasProjectile(ItemStack arg, Item arg22) {
        return CrossbowItem.getProjectiles(arg).stream().anyMatch(arg2 -> arg2.getItem() == arg22);
    }

    private static void shoot(World arg, LivingEntity arg22, Hand arg3, ItemStack arg4, ItemStack arg5, float f, boolean bl, float g, float h, float i) {
        PersistentProjectileEntity lv2;
        boolean bl2;
        if (arg.isClient) {
            return;
        }
        boolean bl3 = bl2 = arg5.getItem() == Items.FIREWORK_ROCKET;
        if (bl2) {
            FireworkRocketEntity lv = new FireworkRocketEntity(arg, arg5, arg22, arg22.getX(), arg22.getEyeY() - (double)0.15f, arg22.getZ(), true);
        } else {
            lv2 = CrossbowItem.createArrow(arg, arg22, arg4, arg5);
            if (bl || i != 0.0f) {
                lv2.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
            }
        }
        if (arg22 instanceof CrossbowUser) {
            CrossbowUser lv3 = (CrossbowUser)((Object)arg22);
            lv3.shoot(lv3.getTarget(), arg4, lv2, i);
        } else {
            Vec3d lv4 = arg22.getOppositeRotationVector(1.0f);
            Quaternion lv5 = new Quaternion(new Vector3f(lv4), i, true);
            Vec3d lv6 = arg22.getRotationVec(1.0f);
            Vector3f lv7 = new Vector3f(lv6);
            lv7.rotate(lv5);
            ((ProjectileEntity)lv2).setVelocity(lv7.getX(), lv7.getY(), lv7.getZ(), g, h);
        }
        arg4.damage(bl2 ? 3 : 1, arg22, arg2 -> arg2.sendToolBreakStatus(arg3));
        arg.spawnEntity(lv2);
        arg.playSound(null, arg22.getX(), arg22.getY(), arg22.getZ(), SoundEvents.ITEM_CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1.0f, f);
    }

    private static PersistentProjectileEntity createArrow(World arg, LivingEntity arg2, ItemStack arg3, ItemStack arg4) {
        ArrowItem lv = (ArrowItem)(arg4.getItem() instanceof ArrowItem ? arg4.getItem() : Items.ARROW);
        PersistentProjectileEntity lv2 = lv.createArrow(arg, arg4, arg2);
        if (arg2 instanceof PlayerEntity) {
            lv2.setCritical(true);
        }
        lv2.setSound(SoundEvents.ITEM_CROSSBOW_HIT);
        lv2.setShotFromCrossbow(true);
        int i = EnchantmentHelper.getLevel(Enchantments.PIERCING, arg3);
        if (i > 0) {
            lv2.setPierceLevel((byte)i);
        }
        return lv2;
    }

    public static void shootAll(World arg, LivingEntity arg2, Hand arg3, ItemStack arg4, float f, float g) {
        List<ItemStack> list = CrossbowItem.getProjectiles(arg4);
        float[] fs = CrossbowItem.getSoundPitches(arg2.getRandom());
        for (int i = 0; i < list.size(); ++i) {
            boolean bl;
            ItemStack lv = list.get(i);
            boolean bl2 = bl = arg2 instanceof PlayerEntity && ((PlayerEntity)arg2).abilities.creativeMode;
            if (lv.isEmpty()) continue;
            if (i == 0) {
                CrossbowItem.shoot(arg, arg2, arg3, arg4, lv, fs[i], bl, f, g, 0.0f);
                continue;
            }
            if (i == 1) {
                CrossbowItem.shoot(arg, arg2, arg3, arg4, lv, fs[i], bl, f, g, -10.0f);
                continue;
            }
            if (i != 2) continue;
            CrossbowItem.shoot(arg, arg2, arg3, arg4, lv, fs[i], bl, f, g, 10.0f);
        }
        CrossbowItem.postShoot(arg, arg2, arg4);
    }

    private static float[] getSoundPitches(Random random) {
        boolean bl = random.nextBoolean();
        return new float[]{1.0f, CrossbowItem.getSoundPitch(bl), CrossbowItem.getSoundPitch(!bl)};
    }

    private static float getSoundPitch(boolean bl) {
        float f = bl ? 0.63f : 0.43f;
        return 1.0f / (RANDOM.nextFloat() * 0.5f + 1.8f) + f;
    }

    private static void postShoot(World arg, LivingEntity arg2, ItemStack arg3) {
        if (arg2 instanceof ServerPlayerEntity) {
            ServerPlayerEntity lv = (ServerPlayerEntity)arg2;
            if (!arg.isClient) {
                Criteria.SHOT_CROSSBOW.trigger(lv, arg3);
            }
            lv.incrementStat(Stats.USED.getOrCreateStat(arg3.getItem()));
        }
        CrossbowItem.clearProjectiles(arg3);
    }

    @Override
    public void usageTick(World arg, LivingEntity arg2, ItemStack arg3, int i) {
        if (!arg.isClient) {
            int j = EnchantmentHelper.getLevel(Enchantments.QUICK_CHARGE, arg3);
            SoundEvent lv = this.getQuickChargeSound(j);
            SoundEvent lv2 = j == 0 ? SoundEvents.ITEM_CROSSBOW_LOADING_MIDDLE : null;
            float f = (float)(arg3.getMaxUseTime() - i) / (float)CrossbowItem.getPullTime(arg3);
            if (f < 0.2f) {
                this.charged = false;
                this.loaded = false;
            }
            if (f >= 0.2f && !this.charged) {
                this.charged = true;
                arg.playSound(null, arg2.getX(), arg2.getY(), arg2.getZ(), lv, SoundCategory.PLAYERS, 0.5f, 1.0f);
            }
            if (f >= 0.5f && lv2 != null && !this.loaded) {
                this.loaded = true;
                arg.playSound(null, arg2.getX(), arg2.getY(), arg2.getZ(), lv2, SoundCategory.PLAYERS, 0.5f, 1.0f);
            }
        }
    }

    @Override
    public int getMaxUseTime(ItemStack arg) {
        return CrossbowItem.getPullTime(arg) + 3;
    }

    public static int getPullTime(ItemStack arg) {
        int i = EnchantmentHelper.getLevel(Enchantments.QUICK_CHARGE, arg);
        return i == 0 ? 25 : 25 - 5 * i;
    }

    @Override
    public UseAction getUseAction(ItemStack arg) {
        return UseAction.CROSSBOW;
    }

    private SoundEvent getQuickChargeSound(int i) {
        switch (i) {
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

    private static float getPullProgress(int i, ItemStack arg) {
        float f = (float)i / (float)CrossbowItem.getPullTime(arg);
        if (f > 1.0f) {
            f = 1.0f;
        }
        return f;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void appendTooltip(ItemStack arg, @Nullable World arg2, List<Text> list, TooltipContext arg3) {
        List<ItemStack> list2 = CrossbowItem.getProjectiles(arg);
        if (!CrossbowItem.isCharged(arg) || list2.isEmpty()) {
            return;
        }
        ItemStack lv = list2.get(0);
        list.add(new TranslatableText("item.minecraft.crossbow.projectile").append(" ").append(lv.toHoverableText()));
        if (arg3.isAdvanced() && lv.getItem() == Items.FIREWORK_ROCKET) {
            ArrayList list3 = Lists.newArrayList();
            Items.FIREWORK_ROCKET.appendTooltip(lv, arg2, list3, arg3);
            if (!list3.isEmpty()) {
                for (int i = 0; i < list3.size(); ++i) {
                    list3.set(i, new LiteralText("  ").append((Text)list3.get(i)).formatted(Formatting.GRAY));
                }
                list.addAll(list3);
            }
        }
    }

    private static float getSpeed(ItemStack arg) {
        if (arg.getItem() == Items.CROSSBOW && CrossbowItem.hasProjectile(arg, Items.FIREWORK_ROCKET)) {
            return 1.6f;
        }
        return 3.15f;
    }

    @Override
    public int getRange() {
        return 8;
    }
}

