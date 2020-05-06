/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.mutable.MutableFloat
 *  org.apache.commons.lang3.mutable.MutableInt
 */
package net.minecraft.enchantment;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.SweepingEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.WeightedPicker;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;

public class EnchantmentHelper {
    public static int getLevel(Enchantment arg, ItemStack arg2) {
        if (arg2.isEmpty()) {
            return 0;
        }
        Identifier lv = Registry.ENCHANTMENT.getId(arg);
        ListTag lv2 = arg2.getEnchantments();
        for (int i = 0; i < lv2.size(); ++i) {
            CompoundTag lv3 = lv2.getCompound(i);
            Identifier lv4 = Identifier.tryParse(lv3.getString("id"));
            if (lv4 == null || !lv4.equals(lv)) continue;
            return MathHelper.clamp(lv3.getInt("lvl"), 0, 255);
        }
        return 0;
    }

    public static Map<Enchantment, Integer> get(ItemStack arg) {
        ListTag lv = arg.getItem() == Items.ENCHANTED_BOOK ? EnchantedBookItem.getEnchantmentTag(arg) : arg.getEnchantments();
        return EnchantmentHelper.fromTag(lv);
    }

    public static Map<Enchantment, Integer> fromTag(ListTag arg) {
        LinkedHashMap map = Maps.newLinkedHashMap();
        for (int i = 0; i < arg.size(); ++i) {
            CompoundTag lv = arg.getCompound(i);
            Registry.ENCHANTMENT.getOrEmpty(Identifier.tryParse(lv.getString("id"))).ifPresent(arg2 -> map.put(arg2, lv.getInt("lvl")));
        }
        return map;
    }

    public static void set(Map<Enchantment, Integer> map, ItemStack arg) {
        ListTag lv = new ListTag();
        for (Map.Entry<Enchantment, Integer> entry : map.entrySet()) {
            Enchantment lv2 = entry.getKey();
            if (lv2 == null) continue;
            int i = entry.getValue();
            CompoundTag lv3 = new CompoundTag();
            lv3.putString("id", String.valueOf(Registry.ENCHANTMENT.getId(lv2)));
            lv3.putShort("lvl", (short)i);
            lv.add(lv3);
            if (arg.getItem() != Items.ENCHANTED_BOOK) continue;
            EnchantedBookItem.addEnchantment(arg, new EnchantmentLevelEntry(lv2, i));
        }
        if (lv.isEmpty()) {
            arg.removeSubTag("Enchantments");
        } else if (arg.getItem() != Items.ENCHANTED_BOOK) {
            arg.putSubTag("Enchantments", lv);
        }
    }

    private static void forEachEnchantment(Consumer arg, ItemStack arg22) {
        if (arg22.isEmpty()) {
            return;
        }
        ListTag lv = arg22.getEnchantments();
        for (int i = 0; i < lv.size(); ++i) {
            String string = lv.getCompound(i).getString("id");
            int j = lv.getCompound(i).getInt("lvl");
            Registry.ENCHANTMENT.getOrEmpty(Identifier.tryParse(string)).ifPresent(arg2 -> arg.accept((Enchantment)arg2, j));
        }
    }

    private static void forEachEnchantment(Consumer arg, Iterable<ItemStack> iterable) {
        for (ItemStack lv : iterable) {
            EnchantmentHelper.forEachEnchantment(arg, lv);
        }
    }

    public static int getProtectionAmount(Iterable<ItemStack> iterable, DamageSource arg) {
        MutableInt mutableInt = new MutableInt();
        EnchantmentHelper.forEachEnchantment((Enchantment arg2, int i) -> mutableInt.add(arg2.getProtectionAmount(i, arg)), iterable);
        return mutableInt.intValue();
    }

    public static float getAttackDamage(ItemStack arg, EntityGroup arg22) {
        MutableFloat mutableFloat = new MutableFloat();
        EnchantmentHelper.forEachEnchantment((Enchantment arg2, int i) -> mutableFloat.add(arg2.getAttackDamage(i, arg22)), arg);
        return mutableFloat.floatValue();
    }

    public static float getSweepingMultiplier(LivingEntity arg) {
        int i = EnchantmentHelper.getEquipmentLevel(Enchantments.SWEEPING, arg);
        if (i > 0) {
            return SweepingEnchantment.getMultiplier(i);
        }
        return 0.0f;
    }

    public static void onUserDamaged(LivingEntity arg, Entity arg2) {
        Consumer lv = (arg3, i) -> arg3.onUserDamaged(arg, arg2, i);
        if (arg != null) {
            EnchantmentHelper.forEachEnchantment(lv, arg.getItemsEquipped());
        }
        if (arg2 instanceof PlayerEntity) {
            EnchantmentHelper.forEachEnchantment(lv, arg.getMainHandStack());
        }
    }

    public static void onTargetDamaged(LivingEntity arg, Entity arg2) {
        Consumer lv = (arg3, i) -> arg3.onTargetDamaged(arg, arg2, i);
        if (arg != null) {
            EnchantmentHelper.forEachEnchantment(lv, arg.getItemsEquipped());
        }
        if (arg instanceof PlayerEntity) {
            EnchantmentHelper.forEachEnchantment(lv, arg.getMainHandStack());
        }
    }

    public static int getEquipmentLevel(Enchantment arg, LivingEntity arg2) {
        Collection<ItemStack> iterable = arg.getEquipment(arg2).values();
        if (iterable == null) {
            return 0;
        }
        int i = 0;
        for (ItemStack lv : iterable) {
            int j = EnchantmentHelper.getLevel(arg, lv);
            if (j <= i) continue;
            i = j;
        }
        return i;
    }

    public static int getKnockback(LivingEntity arg) {
        return EnchantmentHelper.getEquipmentLevel(Enchantments.KNOCKBACK, arg);
    }

    public static int getFireAspect(LivingEntity arg) {
        return EnchantmentHelper.getEquipmentLevel(Enchantments.FIRE_ASPECT, arg);
    }

    public static int getRespiration(LivingEntity arg) {
        return EnchantmentHelper.getEquipmentLevel(Enchantments.RESPIRATION, arg);
    }

    public static int getDepthStrider(LivingEntity arg) {
        return EnchantmentHelper.getEquipmentLevel(Enchantments.DEPTH_STRIDER, arg);
    }

    public static int getEfficiency(LivingEntity arg) {
        return EnchantmentHelper.getEquipmentLevel(Enchantments.EFFICIENCY, arg);
    }

    public static int getLuckOfTheSea(ItemStack arg) {
        return EnchantmentHelper.getLevel(Enchantments.LUCK_OF_THE_SEA, arg);
    }

    public static int getLure(ItemStack arg) {
        return EnchantmentHelper.getLevel(Enchantments.LURE, arg);
    }

    public static int getLooting(LivingEntity arg) {
        return EnchantmentHelper.getEquipmentLevel(Enchantments.LOOTING, arg);
    }

    public static boolean hasAquaAffinity(LivingEntity arg) {
        return EnchantmentHelper.getEquipmentLevel(Enchantments.AQUA_AFFINITY, arg) > 0;
    }

    public static boolean hasFrostWalker(LivingEntity arg) {
        return EnchantmentHelper.getEquipmentLevel(Enchantments.FROST_WALKER, arg) > 0;
    }

    public static boolean hasSoulSpeed(LivingEntity arg) {
        return EnchantmentHelper.getEquipmentLevel(Enchantments.SOUL_SPEED, arg) > 0;
    }

    public static boolean hasBindingCurse(ItemStack arg) {
        return EnchantmentHelper.getLevel(Enchantments.BINDING_CURSE, arg) > 0;
    }

    public static boolean hasVanishingCurse(ItemStack arg) {
        return EnchantmentHelper.getLevel(Enchantments.VANISHING_CURSE, arg) > 0;
    }

    public static int getLoyalty(ItemStack arg) {
        return EnchantmentHelper.getLevel(Enchantments.LOYALTY, arg);
    }

    public static int getRiptide(ItemStack arg) {
        return EnchantmentHelper.getLevel(Enchantments.RIPTIDE, arg);
    }

    public static boolean hasChanneling(ItemStack arg) {
        return EnchantmentHelper.getLevel(Enchantments.CHANNELING, arg) > 0;
    }

    @Nullable
    public static Map.Entry<EquipmentSlot, ItemStack> chooseEquipmentWith(Enchantment arg2, LivingEntity arg22) {
        return EnchantmentHelper.chooseEquipmentWith(arg2, arg22, arg -> true);
    }

    @Nullable
    public static Map.Entry<EquipmentSlot, ItemStack> chooseEquipmentWith(Enchantment arg, LivingEntity arg2, Predicate<ItemStack> predicate) {
        Map<EquipmentSlot, ItemStack> map = arg.getEquipment(arg2);
        if (map.isEmpty()) {
            return null;
        }
        ArrayList list = Lists.newArrayList();
        for (Map.Entry<EquipmentSlot, ItemStack> entry : map.entrySet()) {
            ItemStack lv = entry.getValue();
            if (lv.isEmpty() || EnchantmentHelper.getLevel(arg, lv) <= 0 || !predicate.test(lv)) continue;
            list.add(entry);
        }
        return list.isEmpty() ? null : (Map.Entry)list.get(arg2.getRandom().nextInt(list.size()));
    }

    public static int calculateRequiredExperienceLevel(Random random, int i, int j, ItemStack arg) {
        Item lv = arg.getItem();
        int k = lv.getEnchantability();
        if (k <= 0) {
            return 0;
        }
        if (j > 15) {
            j = 15;
        }
        int l = random.nextInt(8) + 1 + (j >> 1) + random.nextInt(j + 1);
        if (i == 0) {
            return Math.max(l / 3, 1);
        }
        if (i == 1) {
            return l * 2 / 3 + 1;
        }
        return Math.max(l, j * 2);
    }

    public static ItemStack enchant(Random random, ItemStack arg, int i, boolean bl) {
        boolean bl2;
        List<EnchantmentLevelEntry> list = EnchantmentHelper.generateEnchantments(random, arg, i, bl);
        boolean bl3 = bl2 = arg.getItem() == Items.BOOK;
        if (bl2) {
            arg = new ItemStack(Items.ENCHANTED_BOOK);
        }
        for (EnchantmentLevelEntry lv : list) {
            if (bl2) {
                EnchantedBookItem.addEnchantment(arg, lv);
                continue;
            }
            arg.addEnchantment(lv.enchantment, lv.level);
        }
        return arg;
    }

    public static List<EnchantmentLevelEntry> generateEnchantments(Random random, ItemStack arg, int i, boolean bl) {
        ArrayList list = Lists.newArrayList();
        Item lv = arg.getItem();
        int j = lv.getEnchantability();
        if (j <= 0) {
            return list;
        }
        i += 1 + random.nextInt(j / 4 + 1) + random.nextInt(j / 4 + 1);
        float f = (random.nextFloat() + random.nextFloat() - 1.0f) * 0.15f;
        List<EnchantmentLevelEntry> list2 = EnchantmentHelper.getPossibleEntries(i = MathHelper.clamp(Math.round((float)i + (float)i * f), 1, Integer.MAX_VALUE), arg, bl);
        if (!list2.isEmpty()) {
            list.add(WeightedPicker.getRandom(random, list2));
            while (random.nextInt(50) <= i) {
                EnchantmentHelper.removeConflicts(list2, (EnchantmentLevelEntry)Util.getLast(list));
                if (list2.isEmpty()) break;
                list.add(WeightedPicker.getRandom(random, list2));
                i /= 2;
            }
        }
        return list;
    }

    public static void removeConflicts(List<EnchantmentLevelEntry> list, EnchantmentLevelEntry arg) {
        Iterator<EnchantmentLevelEntry> iterator = list.iterator();
        while (iterator.hasNext()) {
            if (arg.enchantment.canCombine(iterator.next().enchantment)) continue;
            iterator.remove();
        }
    }

    public static boolean isCompatible(Collection<Enchantment> collection, Enchantment arg) {
        for (Enchantment lv : collection) {
            if (lv.canCombine(arg)) continue;
            return false;
        }
        return true;
    }

    public static List<EnchantmentLevelEntry> getPossibleEntries(int i, ItemStack arg, boolean bl) {
        ArrayList list = Lists.newArrayList();
        Item lv = arg.getItem();
        boolean bl2 = arg.getItem() == Items.BOOK;
        block0: for (Enchantment lv2 : Registry.ENCHANTMENT) {
            if (lv2.isTreasure() && !bl || !lv2.isAvailableForRandomSelection() || !lv2.type.isAcceptableItem(lv) && !bl2) continue;
            for (int j = lv2.getMaximumLevel(); j > lv2.getMinimumLevel() - 1; --j) {
                if (i < lv2.getMinimumPower(j) || i > lv2.getMaximumPower(j)) continue;
                list.add(new EnchantmentLevelEntry(lv2, j));
                continue block0;
            }
        }
        return list;
    }

    @FunctionalInterface
    static interface Consumer {
        public void accept(Enchantment var1, int var2);
    }
}

