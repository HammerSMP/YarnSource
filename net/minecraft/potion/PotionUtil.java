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

    public static List<StatusEffectInstance> getPotionEffects(ItemStack stack) {
        return PotionUtil.getPotionEffects(stack.getTag());
    }

    public static List<StatusEffectInstance> getPotionEffects(Potion potion, Collection<StatusEffectInstance> custom) {
        ArrayList list = Lists.newArrayList();
        list.addAll(potion.getEffects());
        list.addAll(custom);
        return list;
    }

    public static List<StatusEffectInstance> getPotionEffects(@Nullable CompoundTag tag) {
        ArrayList list = Lists.newArrayList();
        list.addAll(PotionUtil.getPotion(tag).getEffects());
        PotionUtil.getCustomPotionEffects(tag, list);
        return list;
    }

    public static List<StatusEffectInstance> getCustomPotionEffects(ItemStack stack) {
        return PotionUtil.getCustomPotionEffects(stack.getTag());
    }

    public static List<StatusEffectInstance> getCustomPotionEffects(@Nullable CompoundTag tag) {
        ArrayList list = Lists.newArrayList();
        PotionUtil.getCustomPotionEffects(tag, list);
        return list;
    }

    public static void getCustomPotionEffects(@Nullable CompoundTag tag, List<StatusEffectInstance> list) {
        if (tag != null && tag.contains("CustomPotionEffects", 9)) {
            ListTag lv = tag.getList("CustomPotionEffects", 10);
            for (int i = 0; i < lv.size(); ++i) {
                CompoundTag lv2 = lv.getCompound(i);
                StatusEffectInstance lv3 = StatusEffectInstance.fromTag(lv2);
                if (lv3 == null) continue;
                list.add(lv3);
            }
        }
    }

    public static int getColor(ItemStack stack) {
        CompoundTag lv = stack.getTag();
        if (lv != null && lv.contains("CustomPotionColor", 99)) {
            return lv.getInt("CustomPotionColor");
        }
        return PotionUtil.getPotion(stack) == Potions.EMPTY ? 0xF800F8 : PotionUtil.getColor(PotionUtil.getPotionEffects(stack));
    }

    public static int getColor(Potion potion) {
        return potion == Potions.EMPTY ? 0xF800F8 : PotionUtil.getColor(potion.getEffects());
    }

    public static int getColor(Collection<StatusEffectInstance> effects) {
        int i = 3694022;
        if (effects.isEmpty()) {
            return 3694022;
        }
        float f = 0.0f;
        float g = 0.0f;
        float h = 0.0f;
        int j = 0;
        for (StatusEffectInstance lv : effects) {
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

    public static Potion getPotion(ItemStack stack) {
        return PotionUtil.getPotion(stack.getTag());
    }

    public static Potion getPotion(@Nullable CompoundTag compound) {
        if (compound == null) {
            return Potions.EMPTY;
        }
        return Potion.byId(compound.getString("Potion"));
    }

    public static ItemStack setPotion(ItemStack stack, Potion potion) {
        Identifier lv = Registry.POTION.getId(potion);
        if (potion == Potions.EMPTY) {
            stack.removeSubTag("Potion");
        } else {
            stack.getOrCreateTag().putString("Potion", lv.toString());
        }
        return stack;
    }

    public static ItemStack setCustomPotionEffects(ItemStack stack, Collection<StatusEffectInstance> effects) {
        if (effects.isEmpty()) {
            return stack;
        }
        CompoundTag lv = stack.getOrCreateTag();
        ListTag lv2 = lv.getList("CustomPotionEffects", 9);
        for (StatusEffectInstance lv3 : effects) {
            lv2.add(lv3.toTag(new CompoundTag()));
        }
        lv.put("CustomPotionEffects", lv2);
        return stack;
    }

    @Environment(value=EnvType.CLIENT)
    public static void buildTooltip(ItemStack stack, List<Text> list, float f) {
        List<StatusEffectInstance> list2 = PotionUtil.getPotionEffects(stack);
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

