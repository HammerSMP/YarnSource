/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  javax.annotation.Nullable
 */
package net.minecraft.predicate.entity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.predicate.PlayerPredicate;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.predicate.entity.EntityEffectPredicate;
import net.minecraft.predicate.entity.EntityEquipmentPredicate;
import net.minecraft.predicate.entity.EntityFlagsPredicate;
import net.minecraft.predicate.entity.EntityTypePredicate;
import net.minecraft.predicate.entity.FishingHookPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Vec3d;

public class EntityPredicate {
    public static final EntityPredicate ANY = new EntityPredicate(EntityTypePredicate.ANY, DistancePredicate.ANY, LocationPredicate.ANY, EntityEffectPredicate.EMPTY, NbtPredicate.ANY, EntityFlagsPredicate.ANY, EntityEquipmentPredicate.ANY, PlayerPredicate.ANY, FishingHookPredicate.ANY, ANY, ANY, null, null);
    private final EntityTypePredicate type;
    private final DistancePredicate distance;
    private final LocationPredicate location;
    private final EntityEffectPredicate effects;
    private final NbtPredicate nbt;
    private final EntityFlagsPredicate flags;
    private final EntityEquipmentPredicate equipment;
    private final PlayerPredicate player;
    private final FishingHookPredicate fishingHook;
    private final EntityPredicate vehicle;
    private final EntityPredicate targetedEntity;
    @Nullable
    private final String team;
    @Nullable
    private final Identifier catType;

    private EntityPredicate(EntityTypePredicate arg, DistancePredicate arg2, LocationPredicate arg3, EntityEffectPredicate arg4, NbtPredicate arg5, EntityFlagsPredicate arg6, EntityEquipmentPredicate arg7, PlayerPredicate arg8, FishingHookPredicate arg9, EntityPredicate arg10, EntityPredicate arg11, @Nullable String string, @Nullable Identifier arg12) {
        this.type = arg;
        this.distance = arg2;
        this.location = arg3;
        this.effects = arg4;
        this.nbt = arg5;
        this.flags = arg6;
        this.equipment = arg7;
        this.player = arg8;
        this.fishingHook = arg9;
        this.vehicle = arg10;
        this.targetedEntity = arg11;
        this.team = string;
        this.catType = arg12;
    }

    public boolean test(ServerPlayerEntity arg, @Nullable Entity arg2) {
        return this.test(arg.getServerWorld(), arg.getPos(), arg2);
    }

    public boolean test(ServerWorld arg, @Nullable Vec3d arg2, @Nullable Entity arg3) {
        AbstractTeam lv;
        if (this == ANY) {
            return true;
        }
        if (arg3 == null) {
            return false;
        }
        if (!this.type.matches(arg3.getType())) {
            return false;
        }
        if (arg2 == null ? this.distance != DistancePredicate.ANY : !this.distance.test(arg2.x, arg2.y, arg2.z, arg3.getX(), arg3.getY(), arg3.getZ())) {
            return false;
        }
        if (!this.location.test(arg, arg3.getX(), arg3.getY(), arg3.getZ())) {
            return false;
        }
        if (!this.effects.test(arg3)) {
            return false;
        }
        if (!this.nbt.test(arg3)) {
            return false;
        }
        if (!this.flags.test(arg3)) {
            return false;
        }
        if (!this.equipment.test(arg3)) {
            return false;
        }
        if (!this.player.test(arg3)) {
            return false;
        }
        if (!this.fishingHook.test(arg3)) {
            return false;
        }
        if (!this.vehicle.test(arg, arg2, arg3.getVehicle())) {
            return false;
        }
        if (!this.targetedEntity.test(arg, arg2, arg3 instanceof MobEntity ? ((MobEntity)arg3).getTarget() : null)) {
            return false;
        }
        if (!(this.team == null || (lv = arg3.getScoreboardTeam()) != null && this.team.equals(lv.getName()))) {
            return false;
        }
        return this.catType == null || arg3 instanceof CatEntity && ((CatEntity)arg3).getTexture().equals(this.catType);
    }

    public static EntityPredicate fromJson(@Nullable JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return ANY;
        }
        JsonObject jsonObject = JsonHelper.asObject(jsonElement, "entity");
        EntityTypePredicate lv = EntityTypePredicate.fromJson(jsonObject.get("type"));
        DistancePredicate lv2 = DistancePredicate.fromJson(jsonObject.get("distance"));
        LocationPredicate lv3 = LocationPredicate.fromJson(jsonObject.get("location"));
        EntityEffectPredicate lv4 = EntityEffectPredicate.fromJson(jsonObject.get("effects"));
        NbtPredicate lv5 = NbtPredicate.fromJson(jsonObject.get("nbt"));
        EntityFlagsPredicate lv6 = EntityFlagsPredicate.fromJson(jsonObject.get("flags"));
        EntityEquipmentPredicate lv7 = EntityEquipmentPredicate.fromJson(jsonObject.get("equipment"));
        PlayerPredicate lv8 = PlayerPredicate.fromJson(jsonObject.get("player"));
        FishingHookPredicate lv9 = FishingHookPredicate.fromJson(jsonObject.get("fishing_hook"));
        EntityPredicate lv10 = EntityPredicate.fromJson(jsonObject.get("vehicle"));
        EntityPredicate lv11 = EntityPredicate.fromJson(jsonObject.get("targeted_entity"));
        String string = JsonHelper.getString(jsonObject, "team", null);
        Identifier lv12 = jsonObject.has("catType") ? new Identifier(JsonHelper.getString(jsonObject, "catType")) : null;
        return new Builder().type(lv).distance(lv2).location(lv3).effects(lv4).nbt(lv5).flags(lv6).equipment(lv7).player(lv8).fishHook(lv9).team(string).vehicle(lv10).targetedEntity(lv11).catType(lv12).build();
    }

    public JsonElement toJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("type", this.type.toJson());
        jsonObject.add("distance", this.distance.toJson());
        jsonObject.add("location", this.location.toJson());
        jsonObject.add("effects", this.effects.toJson());
        jsonObject.add("nbt", this.nbt.toJson());
        jsonObject.add("flags", this.flags.toJson());
        jsonObject.add("equipment", this.equipment.toJson());
        jsonObject.add("player", this.player.toJson());
        jsonObject.add("fishing_hook", this.fishingHook.toJson());
        jsonObject.add("vehicle", this.vehicle.toJson());
        jsonObject.add("targeted_entity", this.targetedEntity.toJson());
        jsonObject.addProperty("team", this.team);
        if (this.catType != null) {
            jsonObject.addProperty("catType", this.catType.toString());
        }
        return jsonObject;
    }

    public static LootContext createAdvancementEntityLootContext(ServerPlayerEntity arg, Entity arg2) {
        return new LootContext.Builder(arg.getServerWorld()).parameter(LootContextParameters.THIS_ENTITY, arg2).parameter(LootContextParameters.POSITION, arg2.getBlockPos()).parameter(LootContextParameters.ORIGIN, arg.getPos()).random(arg.getRandom()).build(LootContextTypes.ADVANCEMENT_ENTITY);
    }

    public static class Extended {
        public static final Extended EMPTY = new Extended(new LootCondition[0]);
        private final LootCondition[] conditions;
        private final Predicate<LootContext> combinedCondition;

        private Extended(LootCondition[] args) {
            this.conditions = args;
            this.combinedCondition = LootConditionTypes.joinAnd(args);
        }

        public static Extended create(LootCondition ... args) {
            return new Extended(args);
        }

        public static Extended getInJson(JsonObject jsonObject, String string, AdvancementEntityPredicateDeserializer arg) {
            JsonElement jsonElement = jsonObject.get(string);
            return Extended.fromJson(string, arg, jsonElement);
        }

        public static Extended[] requireInJson(JsonObject jsonObject, String string, AdvancementEntityPredicateDeserializer arg) {
            JsonElement jsonElement = jsonObject.get(string);
            if (jsonElement == null || jsonElement.isJsonNull()) {
                return new Extended[0];
            }
            JsonArray jsonArray = JsonHelper.asArray(jsonElement, string);
            Extended[] lvs = new Extended[jsonArray.size()];
            for (int i = 0; i < jsonArray.size(); ++i) {
                lvs[i] = Extended.fromJson(string + "[" + i + "]", arg, jsonArray.get(i));
            }
            return lvs;
        }

        private static Extended fromJson(String string, AdvancementEntityPredicateDeserializer arg, @Nullable JsonElement jsonElement) {
            if (jsonElement != null && jsonElement.isJsonArray()) {
                LootCondition[] lvs = arg.loadConditions(jsonElement.getAsJsonArray(), arg.getAdvancementId().toString() + "/" + string, LootContextTypes.ADVANCEMENT_ENTITY);
                return new Extended(lvs);
            }
            EntityPredicate lv = EntityPredicate.fromJson(jsonElement);
            return Extended.ofLegacy(lv);
        }

        public static Extended ofLegacy(EntityPredicate arg) {
            if (arg == ANY) {
                return EMPTY;
            }
            LootCondition lv = EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, arg).build();
            return new Extended(new LootCondition[]{lv});
        }

        public boolean test(LootContext arg) {
            return this.combinedCondition.test(arg);
        }

        public JsonElement toJson(AdvancementEntityPredicateSerializer arg) {
            if (this.conditions.length == 0) {
                return JsonNull.INSTANCE;
            }
            return arg.conditionsToJson(this.conditions);
        }

        public static JsonElement toPredicatesJsonArray(Extended[] args, AdvancementEntityPredicateSerializer arg) {
            if (args.length == 0) {
                return JsonNull.INSTANCE;
            }
            JsonArray jsonArray = new JsonArray();
            for (Extended lv : args) {
                jsonArray.add(lv.toJson(arg));
            }
            return jsonArray;
        }
    }

    public static class Builder {
        private EntityTypePredicate type = EntityTypePredicate.ANY;
        private DistancePredicate distance = DistancePredicate.ANY;
        private LocationPredicate location = LocationPredicate.ANY;
        private EntityEffectPredicate effects = EntityEffectPredicate.EMPTY;
        private NbtPredicate nbt = NbtPredicate.ANY;
        private EntityFlagsPredicate flags = EntityFlagsPredicate.ANY;
        private EntityEquipmentPredicate equipment = EntityEquipmentPredicate.ANY;
        private PlayerPredicate player = PlayerPredicate.ANY;
        private FishingHookPredicate fishHook = FishingHookPredicate.ANY;
        private EntityPredicate vehicle = ANY;
        private EntityPredicate targetedEntity = ANY;
        private String team;
        private Identifier catType;

        public static Builder create() {
            return new Builder();
        }

        public Builder type(EntityType<?> arg) {
            this.type = EntityTypePredicate.create(arg);
            return this;
        }

        public Builder type(Tag<EntityType<?>> arg) {
            this.type = EntityTypePredicate.create(arg);
            return this;
        }

        public Builder type(Identifier arg) {
            this.catType = arg;
            return this;
        }

        public Builder type(EntityTypePredicate arg) {
            this.type = arg;
            return this;
        }

        public Builder distance(DistancePredicate arg) {
            this.distance = arg;
            return this;
        }

        public Builder location(LocationPredicate arg) {
            this.location = arg;
            return this;
        }

        public Builder effects(EntityEffectPredicate arg) {
            this.effects = arg;
            return this;
        }

        public Builder nbt(NbtPredicate arg) {
            this.nbt = arg;
            return this;
        }

        public Builder flags(EntityFlagsPredicate arg) {
            this.flags = arg;
            return this;
        }

        public Builder equipment(EntityEquipmentPredicate arg) {
            this.equipment = arg;
            return this;
        }

        public Builder player(PlayerPredicate arg) {
            this.player = arg;
            return this;
        }

        public Builder fishHook(FishingHookPredicate arg) {
            this.fishHook = arg;
            return this;
        }

        public Builder vehicle(EntityPredicate arg) {
            this.vehicle = arg;
            return this;
        }

        public Builder targetedEntity(EntityPredicate arg) {
            this.targetedEntity = arg;
            return this;
        }

        public Builder team(@Nullable String string) {
            this.team = string;
            return this;
        }

        public Builder catType(@Nullable Identifier arg) {
            this.catType = arg;
            return this;
        }

        public EntityPredicate build() {
            return new EntityPredicate(this.type, this.distance, this.location, this.effects, this.nbt, this.flags, this.equipment, this.player, this.fishHook, this.vehicle, this.targetedEntity, this.team, this.catType);
        }
    }
}

