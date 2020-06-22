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

    public EntityEquipmentPredicate(ItemPredicate arg, ItemPredicate arg2, ItemPredicate arg3, ItemPredicate arg4, ItemPredicate arg5, ItemPredicate arg6) {
        this.head = arg;
        this.chest = arg2;
        this.legs = arg3;
        this.feet = arg4;
        this.mainhand = arg5;
        this.offhand = arg6;
    }

    public boolean test(@Nullable Entity arg) {
        if (this == ANY) {
            return true;
        }
        if (!(arg instanceof LivingEntity)) {
            return false;
        }
        LivingEntity lv = (LivingEntity)arg;
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

    public static EntityEquipmentPredicate fromJson(@Nullable JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return ANY;
        }
        JsonObject jsonObject = JsonHelper.asObject(jsonElement, "equipment");
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

        public Builder head(ItemPredicate arg) {
            this.head = arg;
            return this;
        }

        public Builder chest(ItemPredicate arg) {
            this.chest = arg;
            return this;
        }

        public Builder legs(ItemPredicate arg) {
            this.legs = arg;
            return this;
        }

        public Builder feet(ItemPredicate arg) {
            this.feet = arg;
            return this;
        }

        public EntityEquipmentPredicate build() {
            return new EntityEquipmentPredicate(this.head, this.chest, this.legs, this.feet, this.mainhand, this.offhand);
        }
    }
}

