/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSyntaxException
 *  javax.annotation.Nullable
 */
package net.minecraft.predicate.item;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class ItemPredicate {
    public static final ItemPredicate ANY = new ItemPredicate();
    @Nullable
    private final Tag<Item> tag;
    @Nullable
    private final Item item;
    private final NumberRange.IntRange count;
    private final NumberRange.IntRange durability;
    private final EnchantmentPredicate[] enchantments;
    private final EnchantmentPredicate[] storedEnchantments;
    @Nullable
    private final Potion potion;
    private final NbtPredicate nbt;

    public ItemPredicate() {
        this.tag = null;
        this.item = null;
        this.potion = null;
        this.count = NumberRange.IntRange.ANY;
        this.durability = NumberRange.IntRange.ANY;
        this.enchantments = EnchantmentPredicate.ARRAY_OF_ANY;
        this.storedEnchantments = EnchantmentPredicate.ARRAY_OF_ANY;
        this.nbt = NbtPredicate.ANY;
    }

    public ItemPredicate(@Nullable Tag<Item> tag, @Nullable Item item, NumberRange.IntRange count, NumberRange.IntRange durability, EnchantmentPredicate[] enchantments, EnchantmentPredicate[] storedEnchantments, @Nullable Potion potion, NbtPredicate nbt) {
        this.tag = tag;
        this.item = item;
        this.count = count;
        this.durability = durability;
        this.enchantments = enchantments;
        this.storedEnchantments = storedEnchantments;
        this.potion = potion;
        this.nbt = nbt;
    }

    public boolean test(ItemStack stack) {
        if (this == ANY) {
            return true;
        }
        if (this.tag != null && !this.tag.contains(stack.getItem())) {
            return false;
        }
        if (this.item != null && stack.getItem() != this.item) {
            return false;
        }
        if (!this.count.test(stack.getCount())) {
            return false;
        }
        if (!this.durability.isDummy() && !stack.isDamageable()) {
            return false;
        }
        if (!this.durability.test(stack.getMaxDamage() - stack.getDamage())) {
            return false;
        }
        if (!this.nbt.test(stack)) {
            return false;
        }
        if (this.enchantments.length > 0) {
            Map<Enchantment, Integer> map = EnchantmentHelper.fromTag(stack.getEnchantments());
            for (EnchantmentPredicate lv : this.enchantments) {
                if (lv.test(map)) continue;
                return false;
            }
        }
        if (this.storedEnchantments.length > 0) {
            Map<Enchantment, Integer> map2 = EnchantmentHelper.fromTag(EnchantedBookItem.getEnchantmentTag(stack));
            for (EnchantmentPredicate lv2 : this.storedEnchantments) {
                if (lv2.test(map2)) continue;
                return false;
            }
        }
        Potion lv3 = PotionUtil.getPotion(stack);
        return this.potion == null || this.potion == lv3;
    }

    public static ItemPredicate fromJson(@Nullable JsonElement el) {
        if (el == null || el.isJsonNull()) {
            return ANY;
        }
        JsonObject jsonObject = JsonHelper.asObject(el, "item");
        NumberRange.IntRange lv = NumberRange.IntRange.fromJson(jsonObject.get("count"));
        NumberRange.IntRange lv2 = NumberRange.IntRange.fromJson(jsonObject.get("durability"));
        if (jsonObject.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
        }
        NbtPredicate lv3 = NbtPredicate.fromJson(jsonObject.get("nbt"));
        Item lv4 = null;
        if (jsonObject.has("item")) {
            Identifier lv5 = new Identifier(JsonHelper.getString(jsonObject, "item"));
            lv4 = Registry.ITEM.getOrEmpty(lv5).orElseThrow(() -> new JsonSyntaxException("Unknown item id '" + lv5 + "'"));
        }
        Tag<Item> lv6 = null;
        if (jsonObject.has("tag")) {
            Identifier lv7 = new Identifier(JsonHelper.getString(jsonObject, "tag"));
            lv6 = ServerTagManagerHolder.getTagManager().getItems().getTag(lv7);
            if (lv6 == null) {
                throw new JsonSyntaxException("Unknown item tag '" + lv7 + "'");
            }
        }
        Potion lv8 = null;
        if (jsonObject.has("potion")) {
            Identifier lv9 = new Identifier(JsonHelper.getString(jsonObject, "potion"));
            lv8 = Registry.POTION.getOrEmpty(lv9).orElseThrow(() -> new JsonSyntaxException("Unknown potion '" + lv9 + "'"));
        }
        EnchantmentPredicate[] lvs = EnchantmentPredicate.deserializeAll(jsonObject.get("enchantments"));
        EnchantmentPredicate[] lvs2 = EnchantmentPredicate.deserializeAll(jsonObject.get("stored_enchantments"));
        return new ItemPredicate(lv6, lv4, lv, lv2, lvs, lvs2, lv8, lv3);
    }

    public JsonElement toJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        if (this.item != null) {
            jsonObject.addProperty("item", Registry.ITEM.getId(this.item).toString());
        }
        if (this.tag != null) {
            jsonObject.addProperty("tag", ServerTagManagerHolder.getTagManager().getItems().getTagId(this.tag).toString());
        }
        jsonObject.add("count", this.count.toJson());
        jsonObject.add("durability", this.durability.toJson());
        jsonObject.add("nbt", this.nbt.toJson());
        if (this.enchantments.length > 0) {
            JsonArray jsonArray = new JsonArray();
            for (EnchantmentPredicate lv : this.enchantments) {
                jsonArray.add(lv.serialize());
            }
            jsonObject.add("enchantments", (JsonElement)jsonArray);
        }
        if (this.storedEnchantments.length > 0) {
            JsonArray jsonArray2 = new JsonArray();
            for (EnchantmentPredicate lv2 : this.storedEnchantments) {
                jsonArray2.add(lv2.serialize());
            }
            jsonObject.add("stored_enchantments", (JsonElement)jsonArray2);
        }
        if (this.potion != null) {
            jsonObject.addProperty("potion", Registry.POTION.getId(this.potion).toString());
        }
        return jsonObject;
    }

    public static ItemPredicate[] deserializeAll(@Nullable JsonElement el) {
        if (el == null || el.isJsonNull()) {
            return new ItemPredicate[0];
        }
        JsonArray jsonArray = JsonHelper.asArray(el, "items");
        ItemPredicate[] lvs = new ItemPredicate[jsonArray.size()];
        for (int i = 0; i < lvs.length; ++i) {
            lvs[i] = ItemPredicate.fromJson(jsonArray.get(i));
        }
        return lvs;
    }

    public static class Builder {
        private final List<EnchantmentPredicate> enchantments = Lists.newArrayList();
        private final List<EnchantmentPredicate> storedEnchantments = Lists.newArrayList();
        @Nullable
        private Item item;
        @Nullable
        private Tag<Item> tag;
        private NumberRange.IntRange count = NumberRange.IntRange.ANY;
        private NumberRange.IntRange durability = NumberRange.IntRange.ANY;
        @Nullable
        private Potion potion;
        private NbtPredicate nbt = NbtPredicate.ANY;

        private Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public Builder item(ItemConvertible item) {
            this.item = item.asItem();
            return this;
        }

        public Builder tag(Tag<Item> tag) {
            this.tag = tag;
            return this;
        }

        public Builder nbt(CompoundTag nbt) {
            this.nbt = new NbtPredicate(nbt);
            return this;
        }

        public Builder enchantment(EnchantmentPredicate enchantment) {
            this.enchantments.add(enchantment);
            return this;
        }

        public ItemPredicate build() {
            return new ItemPredicate(this.tag, this.item, this.count, this.durability, this.enchantments.toArray(EnchantmentPredicate.ARRAY_OF_ANY), this.storedEnchantments.toArray(EnchantmentPredicate.ARRAY_OF_ANY), this.potion, this.nbt);
        }
    }
}

