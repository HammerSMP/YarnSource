/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.enchantment;

import com.google.common.collect.Maps;
import java.util.EnumMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

public abstract class Enchantment {
    private final EquipmentSlot[] slotTypes;
    private final Rarity rarity;
    public final EnchantmentTarget type;
    @Nullable
    protected String translationKey;

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public static Enchantment byRawId(int i) {
        return (Enchantment)Registry.ENCHANTMENT.get(i);
    }

    protected Enchantment(Rarity arg, EnchantmentTarget arg2, EquipmentSlot[] args) {
        this.rarity = arg;
        this.type = arg2;
        this.slotTypes = args;
    }

    public Map<EquipmentSlot, ItemStack> getEquipment(LivingEntity arg) {
        EnumMap map = Maps.newEnumMap(EquipmentSlot.class);
        for (EquipmentSlot lv : this.slotTypes) {
            ItemStack lv2 = arg.getEquippedStack(lv);
            if (lv2.isEmpty()) continue;
            map.put(lv, lv2);
        }
        return map;
    }

    public Rarity getRarity() {
        return this.rarity;
    }

    public int getMinLevel() {
        return 1;
    }

    public int getMaxLevel() {
        return 1;
    }

    public int getMinPower(int i) {
        return 1 + i * 10;
    }

    public int getMaxPower(int i) {
        return this.getMinPower(i) + 5;
    }

    public int getProtectionAmount(int i, DamageSource arg) {
        return 0;
    }

    public float getAttackDamage(int i, EntityGroup arg) {
        return 0.0f;
    }

    public final boolean canCombine(Enchantment arg) {
        return this.canAccept(arg) && arg.canAccept(this);
    }

    protected boolean canAccept(Enchantment arg) {
        return this != arg;
    }

    protected String getOrCreateTranslationKey() {
        if (this.translationKey == null) {
            this.translationKey = Util.createTranslationKey("enchantment", Registry.ENCHANTMENT.getId(this));
        }
        return this.translationKey;
    }

    public String getTranslationKey() {
        return this.getOrCreateTranslationKey();
    }

    public Text getName(int i) {
        TranslatableText lv = new TranslatableText(this.getTranslationKey());
        if (this.isCursed()) {
            lv.formatted(Formatting.RED);
        } else {
            lv.formatted(Formatting.GRAY);
        }
        if (i != 1 || this.getMaxLevel() != 1) {
            lv.append(" ").append(new TranslatableText("enchantment.level." + i));
        }
        return lv;
    }

    public boolean isAcceptableItem(ItemStack arg) {
        return this.type.isAcceptableItem(arg.getItem());
    }

    public void onTargetDamaged(LivingEntity arg, Entity arg2, int i) {
    }

    public void onUserDamaged(LivingEntity arg, Entity arg2, int i) {
    }

    public boolean isTreasure() {
        return false;
    }

    public boolean isCursed() {
        return false;
    }

    public boolean isAvailableForEnchantedBookOffer() {
        return true;
    }

    public boolean isAvailableForRandomSelection() {
        return true;
    }

    public static enum Rarity {
        COMMON(10),
        UNCOMMON(5),
        RARE(2),
        VERY_RARE(1);

        private final int weight;

        private Rarity(int j) {
            this.weight = j;
        }

        public int getWeight() {
            return this.weight;
        }
    }
}

