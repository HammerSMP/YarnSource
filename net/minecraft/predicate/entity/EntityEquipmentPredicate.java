/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  javax.annotation.Nullable
 */
package net.minecraft.predicate.entity;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.util.JsonHelper;
import net.minecraft.village.raid.Raid;

public class EntityEquipmentPredicate {
    public static final EntityEquipmentPredicate ANY = new EntityEquipmentPredicate(ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY);
    public static final EntityEquipmentPredicate OMINOUS_BANNER_ON_HEAD = new EntityEquipmentPredicate(ItemPredicate.Builder.create().item(Items.WHITE_BANNER).nbt(Raid.getOminousBanner().getTag()).build(), ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY);
    private final ItemPredicate head;
    private final ItemPredicate chest;
    private final ItemPredicate legs;
    private final ItemPredicate feet;
    private final ItemPredicate mainhand;
    private final ItemPredicate offhand;

    public EntityEquipmentPredicate(ItemPredicate head, ItemPredicate chest, ItemPredicate legs, ItemPredicate feet, ItemPredicate mainhand, ItemPredicate offhand) {
        this.head = head;
        this.chest = chest;
        this.legs = legs;
        this.feet = feet;
        this.mainhand = mainhand;
        this.offhand = offhand;
    }

    public boolean test(@Nullable Entity entity) {
        if (this == ANY) {
            return true;
        }
        if (!(entity instanceof LivingEntity)) {
            return false;
        }
        LivingEntity lv = (LivingEntity)entity;
        if (!this.head.test(lv.getEquippedStack(EquipmentSlot.HEAD))) {
            return false;
        }
        if (!this.chest.test(lv.getEquippedStack(EquipmentSlot.CHEST))) {
            return false;
        }
        if (!this.legs.test(lv.getEquippedStack(EquipmentSlot.LEGS))) {
            return false;
        }
        if (!this.feet.test(lv.getEquippedStack(EquipmentSlot.FEET))) {
            return false;
        }
        if (!this.mainhand.test(lv.getEquippedStack(EquipmentSlot.MAINHAND))) {
            return false;
        }
        return this.offhand.test(lv.getEquippedStack(EquipmentSlot.OFFHAND));
    }

    public static EntityEquipmentPredicate fromJson(@Nullable JsonElement json) {
        if (json == null || json.isJsonNull()) {
            return ANY;
        }
        JsonObject jsonObject = JsonHelper.asObject(json, "equipment");
        ItemPredicate lv = ItemPredicate.fromJson(jsonObject.get("head"));
        ItemPredicate lv2 = ItemPredicate.fromJson(jsonObject.get("chest"));
        ItemPredicate lv3 = ItemPredicate.fromJson(jsonObject.get("legs"));
        ItemPredicate lv4 = ItemPredicate.fromJson(jsonObject.get("feet"));
        ItemPredicate lv5 = ItemPredicate.fromJson(jsonObject.get("mainhand"));
        ItemPredicate lv6 = ItemPredicate.fromJson(jsonObject.get("offhand"));
        return new EntityEquipmentPredicate(lv, lv2, lv3, lv4, lv5, lv6);
    }

    public JsonElement toJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("head", this.head.toJson());
        jsonObject.add("chest", this.chest.toJson());
        jsonObject.add("legs", this.legs.toJson());
        jsonObject.add("feet", this.feet.toJson());
        jsonObject.add("mainhand", this.mainhand.toJson());
        jsonObject.add("offhand", this.offhand.toJson());
        return jsonObject;
    }

    public static class Builder {
        private ItemPredicate head = ItemPredicate.ANY;
        private ItemPredicate chest = ItemPredicate.ANY;
        private ItemPredicate legs = ItemPredicate.ANY;
        private ItemPredicate feet = ItemPredicate.ANY;
        private ItemPredicate mainhand = ItemPredicate.ANY;
        private ItemPredicate offhand = ItemPredicate.ANY;

        public static Builder create() {
            return new Builder();
        }

        public Builder head(ItemPredicate head) {
            this.head = head;
            return this;
        }

        public Builder chest(ItemPredicate chest) {
            this.chest = chest;
            return this;
        }

        public Builder legs(ItemPredicate legs) {
            this.legs = legs;
            return this;
        }

        public Builder feet(ItemPredicate feet) {
            this.feet = feet;
            return this;
        }

        public EntityEquipmentPredicate build() {
            return new EntityEquipmentPredicate(this.head, this.chest, this.legs, this.feet, this.mainhand, this.offhand);
        }
    }
}

