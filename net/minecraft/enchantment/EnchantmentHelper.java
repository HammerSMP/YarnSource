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
    public static int getLevel(Enchantment enchantment, ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        Identifier lv = Registry.ENCHANTMENT.getId(enchantment);
        ListTag lv2 = stack.getEnchantments();
        for (int i = 0; i < lv2.size(); ++i) {
            CompoundTag lv3 = lv2.getCompound(i);
            Identifier lv4 = Identifier.tryParse(lv3.getString("id"));
            if (lv4 == null || !lv4.equals(lv)) continue;
            return MathHelper.clamp(lv3.getInt("lvl"), 0, 255);
        }
        return 0;
    }

    public static Map<Enchantment, Integer> get(ItemStack stack) {
        ListTag lv = stack.getItem() == Items.ENCHANTED_BOOK ? EnchantedBookItem.getEnchantmentTag(stack) : stack.getEnchantments();
        return EnchantmentHelper.fromTag(lv);
    }

    public static Map<Enchantment, Integer> fromTag(ListTag tag) {
        LinkedHashMap map = Maps.newLinkedHashMap();
        for (int i = 0; i < tag.size(); ++i) {
            CompoundTag lv = tag.getCompound(i);
            Registry.ENCHANTMENT.getOrEmpty(Identifier.tryParse(lv.getString("id"))).ifPresent(enchantment -> map.put(enchantment, lv.getInt("lvl")));
        }
        return map;
    }

    public static void set(Map<Enchantment, Integer> enchantments, ItemStack stack) {
        ListTag lv = new ListTag();
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            Enchantment lv2 = entry.getKey();
            if (lv2 == null) continue;
            int i = entry.getValue();
            CompoundTag lv3 = new CompoundTag();
            lv3.putString("id", String.valueOf(Registry.ENCHANTMENT.getId(lv2)));
            lv3.putShort("lvl", (short)i);
            lv.add(lv3);
            if (stack.getItem() != Items.ENCHANTED_BOOK) continue;
            EnchantedBookItem.addEnchantment(stack, new EnchantmentLevelEntry(lv2, i));
        }
        if (lv.isEmpty()) {
            stack.removeSubTag("Enchantments");
        } else if (stack.getItem() != Items.ENCHANTED_BOOK) {
            stack.putSubTag("Enchantments", lv);
        }
    }

    private static void forEachEnchantment(Consumer action, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        ListTag lv = stack.getEnchantments();
        for (int i = 0; i < lv.size(); ++i) {
            String string = lv.getCompound(i).getString("id");
            int j = lv.getCompound(i).getInt("lvl");
            Registry.ENCHANTMENT.getOrEmpty(Identifier.tryParse(string)).ifPresent(arg2 -> action.accept((Enchantment)arg2, j));
        }
    }

    private static void forEachEnchantment(Consumer action, Iterable<ItemStack> stacks) {
        for (ItemStack lv : stacks) {
            EnchantmentHelper.forEachEnchantment(action, lv);
        }
    }

    public static int getProtectionAmount(Iterable<ItemStack> equipment, DamageSource source) {
        MutableInt mutableInt = new MutableInt();
        EnchantmentHelper.forEachEnchantment((Enchantment arg2, int i) -> mutableInt.add(arg2.getProtectionAmount(i, source)), equipment);
        return mutableInt.intValue();
    }

    public static float getAttackDamage(ItemStack stack, EntityGroup group) {
        MutableFloat mutableFloat = new MutableFloat();
        EnchantmentHelper.forEachEnchantment((Enchantment arg2, int i) -> mutableFloat.add(arg2.getAttackDamage(i, group)), stack);
        return mutableFloat.floatValue();
    }

    public static float getSweepingMultiplier(LivingEntity entity) {
        int i = EnchantmentHelper.getEquipmentLevel(Enchantments.SWEEPING, entity);
        if (i > 0) {
            return SweepingEnchantment.getMultiplier(i);
        }
        return 0.0f;
    }

    public static void onUserDamaged(LivingEntity user, Entity attacker) {
        Consumer lv = (arg3, i) -> arg3.onUserDamaged(user, attacker, i);
        if (user != null) {
            EnchantmentHelper.forEachEnchantment(lv, user.getItemsEquipped());
        }
        if (attacker instanceof PlayerEntity) {
            EnchantmentHelper.forEachEnchantment(lv, user.getMainHandStack());
        }
    }

    public static void onTargetDamaged(LivingEntity user, Entity target) {
        Consumer lv = (arg3, i) -> arg3.onTargetDamaged(user, target, i);
        if (user != null) {
            EnchantmentHelper.forEachEnchantment(lv, user.getItemsEquipped());
        }
        if (user instanceof PlayerEntity) {
            EnchantmentHelper.forEachEnchantment(lv, user.getMainHandStack());
        }
    }

    public static int getEquipmentLevel(Enchantment enchantment, LivingEntity entity) {
        Collection<ItemStack> iterable = enchantment.getEquipment(entity).values();
        if (iterable == null) {
            return 0;
        }
        int i = 0;
        for (ItemStack lv : iterable) {
            int j = EnchantmentHelper.getLevel(enchantment, lv);
            if (j <= i) continue;
            i = j;
        }
        return i;
    }

    public static int getKnockback(LivingEntity entity) {
        return EnchantmentHelper.getEquipmentLevel(Enchantments.KNOCKBACK, entity);
    }

    public static int getFireAspect(LivingEntity entity) {
        return EnchantmentHelper.getEquipmentLevel(Enchantments.FIRE_ASPECT, entity);
    }

    public static int getRespiration(LivingEntity entity) {
        return EnchantmentHelper.getEquipmentLevel(Enchantments.RESPIRATION, entity);
    }

    public static int getDepthStrider(LivingEntity entity) {
        return EnchantmentHelper.getEquipmentLevel(Enchantments.DEPTH_STRIDER, entity);
    }

    public static int getEfficiency(LivingEntity entity) {
        return EnchantmentHelper.getEquipmentLevel(Enchantments.EFFICIENCY, entity);
    }

    public static int getLuckOfTheSea(ItemStack stack) {
        return EnchantmentHelper.getLevel(Enchantments.LUCK_OF_THE_SEA, stack);
    }

    public static int getLure(ItemStack stack) {
        return EnchantmentHelper.getLevel(Enchantments.LURE, stack);
    }

    public static int getLooting(LivingEntity entity) {
        return EnchantmentHelper.getEquipmentLevel(Enchantments.LOOTING, entity);
    }

    public static boolean hasAquaAffinity(LivingEntity entity) {
        return EnchantmentHelper.getEquipmentLevel(Enchantments.AQUA_AFFINITY, entity) > 0;
    }

    public static boolean hasFrostWalker(LivingEntity entity) {
        return EnchantmentHelper.getEquipmentLevel(Enchantments.FROST_WALKER, entity) > 0;
    }

    public static boolean hasSoulSpeed(LivingEntity entity) {
        return EnchantmentHelper.getEquipmentLevel(Enchantments.SOUL_SPEED, entity) > 0;
    }

    public static boolean hasBindingCurse(ItemStack stack) {
        return EnchantmentHelper.getLevel(Enchantments.BINDING_CURSE, stack) > 0;
    }

    public static boolean hasVanishingCurse(ItemStack stack) {
        return EnchantmentHelper.getLevel(Enchantments.VANISHING_CURSE, stack) > 0;
    }

    public static int getLoyalty(ItemStack stack) {
        return EnchantmentHelper.getLevel(Enchantments.LOYALTY, stack);
    }

    public static int getRiptide(ItemStack stack) {
        return EnchantmentHelper.getLevel(Enchantments.RIPTIDE, stack);
    }

    public static boolean hasChanneling(ItemStack stack) {
        return EnchantmentHelper.getLevel(Enchantments.CHANNELING, stack) > 0;
    }

    @Nullable
    public static Map.Entry<EquipmentSlot, ItemStack> chooseEquipmentWith(Enchantment enchantment, LivingEntity entity) {
        return EnchantmentHelper.chooseEquipmentWith(enchantment, entity, stack -> true);
    }

    @Nullable
    public static Map.Entry<EquipmentSlot, ItemStack> chooseEquipmentWith(Enchantment enchantment, LivingEntity entity, Predicate<ItemStack> condition) {
        Map<EquipmentSlot, ItemStack> map = enchantment.getEquipment(entity);
        if (map.isEmpty()) {
            return null;
        }
        ArrayList list = Lists.newArrayList();
        for (Map.Entry<EquipmentSlot, ItemStack> entry : map.entrySet()) {
            ItemStack lv = entry.getValue();
            if (lv.isEmpty() || EnchantmentHelper.getLevel(enchantment, lv) <= 0 || !condition.test(lv)) continue;
            list.add(entry);
        }
        return list.isEmpty() ? null : (Map.Entry)list.get(entity.getRandom().nextInt(list.size()));
    }

    public static int calculateRequiredExperienceLevel(Random random, int slotIndex, int bookshelfCount, ItemStack stack) {
        Item lv = stack.getItem();
        int k = lv.getEnchantability();
        if (k <= 0) {
            return 0;
        }
        if (bookshelfCount > 15) {
            bookshelfCount = 15;
        }
        int l = random.nextInt(8) + 1 + (bookshelfCount >> 1) + random.nextInt(bookshelfCount + 1);
        if (slotIndex == 0) {
            return Math.max(l / 3, 1);
        }
        if (slotIndex == 1) {
            return l * 2 / 3 + 1;
        }
        return Math.max(l, bookshelfCount * 2);
    }

    public static ItemStack enchant(Random random, ItemStack target, int level, boolean treasureAllowed) {
        boolean bl2;
        List<EnchantmentLevelEntry> list = EnchantmentHelper.generateEnchantments(random, target, level, treasureAllowed);
        boolean bl = bl2 = target.getItem() == Items.BOOK;
        if (bl2) {
            target = new ItemStack(Items.ENCHANTED_BOOK);
        }
        for (EnchantmentLevelEntry lv : list) {
            if (bl2) {
                EnchantedBookItem.addEnchantment(target, lv);
                continue;
            }
            target.addEnchantment(lv.enchantment, lv.level);
        }
        return target;
    }

    public static List<EnchantmentLevelEntry> generateEnchantments(Random random, ItemStack stack, int level, boolean treasureAllowed) {
        ArrayList list = Lists.newArrayList();
        Item lv = stack.getItem();
        int j = lv.getEnchantability();
        if (j <= 0) {
            return list;
        }
        level += 1 + random.nextInt(j / 4 + 1) + random.nextInt(j / 4 + 1);
        float f = (random.nextFloat() + random.nextFloat() - 1.0f) * 0.15f;
        List<EnchantmentLevelEntry> list2 = EnchantmentHelper.getPossibleEntries(level = MathHelper.clamp(Math.round((float)level + (float)level * f), 1, Integer.MAX_VALUE), stack, treasureAllowed);
        if (!list2.isEmpty()) {
            list.add(WeightedPicker.getRandom(random, list2));
            while (random.nextInt(50) <= level) {
                EnchantmentHelper.removeConflicts(list2, (EnchantmentLevelEntry)Util.getLast(list));
                if (list2.isEmpty()) break;
                list.add(WeightedPicker.getRandom(random, list2));
                level /= 2;
            }
        }
        return list;
    }

    public static void removeConflicts(List<EnchantmentLevelEntry> possibleEntries, EnchantmentLevelEntry pickedEntry) {
        Iterator<EnchantmentLevelEntry> iterator = possibleEntries.iterator();
        while (iterator.hasNext()) {
            if (pickedEntry.enchantment.canCombine(iterator.next().enchantment)) continue;
            iterator.remove();
        }
    }

    public static boolean isCompatible(Collection<Enchantment> existing, Enchantment candidate) {
        for (Enchantment lv : existing) {
            if (lv.canCombine(candidate)) continue;
            return false;
        }
        return true;
    }

    public static List<EnchantmentLevelEntry> getPossibleEntries(int power, ItemStack stack, boolean treasureAllowed) {
        ArrayList list = Lists.newArrayList();
        Item lv = stack.getItem();
        boolean bl2 = stack.getItem() == Items.BOOK;
        block0: for (Enchantment lv2 : Registry.ENCHANTMENT) {
            if (lv2.isTreasure() && !treasureAllowed || !lv2.isAvailableForRandomSelection() || !lv2.type.isAcceptableItem(lv) && !bl2) continue;
            for (int j = lv2.getMaxLevel(); j > lv2.getMinLevel() - 1; --j) {
                if (power < lv2.getMinPower(j) || power > lv2.getMaxPower(j)) continue;
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

