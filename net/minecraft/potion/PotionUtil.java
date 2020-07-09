/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.util.Pair
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.potion;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class PotionUtil {
    private static final MutableText field_25817 = new TranslatableText("effect.none").formatted(Formatting.GRAY);

    public static List<StatusEffectInstance> getPotionEffects(ItemStack arg) {
        return PotionUtil.getPotionEffects(arg.getTag());
    }

    public static List<StatusEffectInstance> getPotionEffects(Potion arg, Collection<StatusEffectInstance> collection) {
        ArrayList list = Lists.newArrayList();
        list.addAll(arg.getEffects());
        list.addAll(collection);
        return list;
    }

    public static List<StatusEffectInstance> getPotionEffects(@Nullable CompoundTag arg) {
        ArrayList list = Lists.newArrayList();
        list.addAll(PotionUtil.getPotion(arg).getEffects());
        PotionUtil.getCustomPotionEffects(arg, list);
        return list;
    }

    public static List<StatusEffectInstance> getCustomPotionEffects(ItemStack arg) {
        return PotionUtil.getCustomPotionEffects(arg.getTag());
    }

    public static List<StatusEffectInstance> getCustomPotionEffects(@Nullable CompoundTag arg) {
        ArrayList list = Lists.newArrayList();
        PotionUtil.getCustomPotionEffects(arg, list);
        return list;
    }

    public static void getCustomPotionEffects(@Nullable CompoundTag arg, List<StatusEffectInstance> list) {
        if (arg != null && arg.contains("CustomPotionEffects", 9)) {
            ListTag lv = arg.getList("CustomPotionEffects", 10);
            for (int i = 0; i < lv.size(); ++i) {
                CompoundTag lv2 = lv.getCompound(i);
                StatusEffectInstance lv3 = StatusEffectInstance.fromTag(lv2);
                if (lv3 == null) continue;
                list.add(lv3);
            }
        }
    }

    public static int getColor(ItemStack arg) {
        CompoundTag lv = arg.getTag();
        if (lv != null && lv.contains("CustomPotionColor", 99)) {
            return lv.getInt("CustomPotionColor");
        }
        return PotionUtil.getPotion(arg) == Potions.EMPTY ? 0xF800F8 : PotionUtil.getColor(PotionUtil.getPotionEffects(arg));
    }

    public static int getColor(Potion arg) {
        return arg == Potions.EMPTY ? 0xF800F8 : PotionUtil.getColor(arg.getEffects());
    }

    public static int getColor(Collection<StatusEffectInstance> collection) {
        int i = 3694022;
        if (collection.isEmpty()) {
            return 3694022;
        }
        float f = 0.0f;
        float g = 0.0f;
        float h = 0.0f;
        int j = 0;
        for (StatusEffectInstance lv : collection) {
            if (!lv.shouldShowParticles()) continue;
            int k = lv.getEffectType().getColor();
            int l = lv.getAmplifier() + 1;
            f += (float)(l * (k >> 16 & 0xFF)) / 255.0f;
            g += (float)(l * (k >> 8 & 0xFF)) / 255.0f;
            h += (float)(l * (k >> 0 & 0xFF)) / 255.0f;
            j += l;
        }
        if (j == 0) {
            return 0;
        }
        f = f / (float)j * 255.0f;
        g = g / (float)j * 255.0f;
        h = h / (float)j * 255.0f;
        return (int)f << 16 | (int)g << 8 | (int)h;
    }

    public static Potion getPotion(ItemStack arg) {
        return PotionUtil.getPotion(arg.getTag());
    }

    public static Potion getPotion(@Nullable CompoundTag arg) {
        if (arg == null) {
            return Potions.EMPTY;
        }
        return Potion.byId(arg.getString("Potion"));
    }

    public static ItemStack setPotion(ItemStack arg, Potion arg2) {
        Identifier lv = Registry.POTION.getId(arg2);
        if (arg2 == Potions.EMPTY) {
            arg.removeSubTag("Potion");
        } else {
            arg.getOrCreateTag().putString("Potion", lv.toString());
        }
        return arg;
    }

    public static ItemStack setCustomPotionEffects(ItemStack arg, Collection<StatusEffectInstance> collection) {
        if (collection.isEmpty()) {
            return arg;
        }
        CompoundTag lv = arg.getOrCreateTag();
        ListTag lv2 = lv.getList("CustomPotionEffects", 9);
        for (StatusEffectInstance lv3 : collection) {
            lv2.add(lv3.toTag(new CompoundTag()));
        }
        lv.put("CustomPotionEffects", lv2);
        return arg;
    }

    @Environment(value=EnvType.CLIENT)
    public static void buildTooltip(ItemStack arg, List<Text> list, float f) {
        List<StatusEffectInstance> list2 = PotionUtil.getPotionEffects(arg);
        ArrayList list3 = Lists.newArrayList();
        if (list2.isEmpty()) {
            list.add(field_25817);
        } else {
            for (StatusEffectInstance lv : list2) {
                TranslatableText lv2 = new TranslatableText(lv.getTranslationKey());
                StatusEffect lv3 = lv.getEffectType();
                Map<EntityAttribute, EntityAttributeModifier> map = lv3.getAttributeModifiers();
                if (!map.isEmpty()) {
                    for (Map.Entry<EntityAttribute, EntityAttributeModifier> entry : map.entrySet()) {
                        EntityAttributeModifier lv4 = entry.getValue();
                        EntityAttributeModifier lv5 = new EntityAttributeModifier(lv4.getName(), lv3.adjustModifierAmount(lv.getAmplifier(), lv4), lv4.getOperation());
                        list3.add(new Pair((Object)entry.getKey(), (Object)lv5));
                    }
                }
                if (lv.getAmplifier() > 0) {
                    lv2 = new TranslatableText("potion.withAmplifier", lv2, new TranslatableText("potion.potency." + lv.getAmplifier()));
                }
                if (lv.getDuration() > 20) {
                    lv2 = new TranslatableText("potion.withDuration", lv2, StatusEffectUtil.durationToString(lv, f));
                }
                list.add(lv2.formatted(lv3.getType().getFormatting()));
            }
        }
        if (!list3.isEmpty()) {
            list.add(LiteralText.EMPTY);
            list.add(new TranslatableText("potion.whenDrank").formatted(Formatting.DARK_PURPLE));
            for (Pair pair : list3) {
                double g;
                EntityAttributeModifier lv6 = (EntityAttributeModifier)pair.getSecond();
                double d = lv6.getValue();
                if (lv6.getOperation() == EntityAttributeModifier.Operation.MULTIPLY_BASE || lv6.getOperation() == EntityAttributeModifier.Operation.MULTIPLY_TOTAL) {
                    double e = lv6.getValue() * 100.0;
                } else {
                    g = lv6.getValue();
                }
                if (d > 0.0) {
                    list.add(new TranslatableText("attribute.modifier.plus." + lv6.getOperation().getId(), ItemStack.MODIFIER_FORMAT.format(g), new TranslatableText(((EntityAttribute)pair.getFirst()).getTranslationKey())).formatted(Formatting.BLUE));
                    continue;
                }
                if (!(d < 0.0)) continue;
                list.add(new TranslatableText("attribute.modifier.take." + lv6.getOperation().getId(), ItemStack.MODIFIER_FORMAT.format(g *= -1.0), new TranslatableText(((EntityAttribute)pair.getFirst()).getTranslationKey())).formatted(Formatting.RED));
            }
        }
    }
}

