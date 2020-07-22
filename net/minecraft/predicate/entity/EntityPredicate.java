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
    public static final EntityPredicate ANY = new EntityPredicate(EntityTypePredicate.ANY, DistancePredicate.ANY, LocationPredicate.ANY, EntityEffectPredicate.EMPTY, NbtPredicate.ANY, EntityFlagsPredicate.ANY, EntityEquipmentPredicate.ANY, PlayerPredicate.ANY, FishingHookPredicate.ANY, null, null);
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

    private EntityPredicate(EntityTypePredicate arg, DistancePredicate arg2, LocationPredicate arg3, EntityEffectPredicate arg4, NbtPredicate arg5, EntityFlagsPredicate arg6, EntityEquipmentPredicate arg7, PlayerPredicate arg8, FishingHookPredicate arg9, @Nullable String string, @Nullable Identifier arg10) {
        this.type = arg;
        this.distance = arg2;
        this.location = arg3;
        this.effects = arg4;
        this.nbt = arg5;
        this.flags = arg6;
        this.equipment = arg7;
        this.player = arg8;
        this.fishingHook = arg9;
        this.vehicle = this;
        this.targetedEntity = this;
        this.team = string;
        this.catType = arg10;
    }

    private EntityPredicate(EntityTypePredicate type, DistancePredicate distance, LocationPredicate location, EntityEffectPredicate effects, NbtPredicate nbt, EntityFlagsPredicate flags, EntityEquipmentPredicate equipment, PlayerPredicate player, FishingHookPredicate fishingHook, EntityPredicate vehicle, EntityPredicate targetedEntity, @Nullable String team, @Nullable Identifier catType) {
        this.type = type;
        this.distance = distance;
        this.location = location;
        this.effects = effects;
        this.nbt = nbt;
        this.flags = flags;
        this.equipment = equipment;
        this.player = player;
        this.fishingHook = fishingHook;
        this.vehicle = vehicle;
        this.targetedEntity = targetedEntity;
        this.team = team;
        this.catType = catType;
    }

    public boolean test(ServerPlayerEntity player, @Nullable Entity entity) {
        return this.test(player.getServerWorld(), player.getPos(), entity);
    }

    public boolean test(ServerWorld world, @Nullable Vec3d pos, @Nullable Entity entity) {
        AbstractTeam lv;
        if (this == ANY) {
            return true;
        }
        if (entity == null) {
            return false;
        }
        if (!this.type.matches(entity.getType())) {
            return false;
        }
        if (pos == null ? this.distance != DistancePredicate.ANY : !this.distance.test(pos.x, pos.y, pos.z, entity.getX(), entity.getY(), entity.getZ())) {
            return false;
        }
        if (!this.location.test(world, entity.getX(), entity.getY(), entity.getZ())) {
            return false;
        }
        if (!this.effects.test(entity)) {
            return false;
        }
        if (!this.nbt.test(entity)) {
            return false;
        }
        if (!this.flags.test(entity)) {
            return false;
        }
        if (!this.equipment.test(entity)) {
            return false;
        }
        if (!this.player.test(entity)) {
            return false;
        }
        if (!this.fishingHook.test(entity)) {
            return false;
        }
        if (!this.vehicle.test(world, pos, entity.getVehicle())) {
            return false;
        }
        if (!this.targetedEntity.test(world, pos, entity instanceof MobEntity ? ((MobEntity)entity).getTarget() : null)) {
            return false;
        }
        if (!(this.team == null || (lv = entity.getScoreboardTeam()) != null && this.team.equals(lv.getName()))) {
            return false;
        }
        return this.catType == null || entity instanceof CatEntity && ((CatEntity)entity).getTexture().equals(this.catType);
    }

    public static EntityPredicate fromJson(@Nullable JsonElement json) {
        if (json == null || json.isJsonNull()) {
            return ANY;
        }
        JsonObject jsonObject = JsonHelper.asObject(json, "entity");
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

    public static LootContext createAdvancementEntityLootContext(ServerPlayerEntity player, Entity target) {
        return new LootContext.Builder(player.getServerWorld()).parameter(LootContextParameters.THIS_ENTITY, target).parameter(LootContextParameters.POSITION, target.getBlockPos()).parameter(LootContextParameters.ORIGIN, player.getPos()).random(player.getRandom()).build(LootContextTypes.ADVANCEMENT_ENTITY);
    }

    public static class Extended {
        public static final Extended EMPTY = new Extended(new LootCondition[0]);
        private final LootCondition[] conditions;
        private final Predicate<LootContext> combinedCondition;

        private Extended(LootCondition[] conditions) {
            this.conditions = conditions;
            this.combinedCondition = LootConditionTypes.joinAnd(conditions);
        }

        public static Extended create(LootCondition ... conditions) {
            return new Extended(conditions);
        }

        public static Extended getInJson(JsonObject root, String key, AdvancementEntityPredicateDeserializer predicateDeserializer) {
            JsonElement jsonElement = root.get(key);
            return Extended.fromJson(key, predicateDeserializer, jsonElement);
        }

        public static Extended[] requireInJson(JsonObject root, String key, AdvancementEntityPredicateDeserializer predicateDeserializer) {
            JsonElement jsonElement = root.get(key);
            if (jsonElement == null || jsonElement.isJsonNull()) {
                return new Extended[0];
            }
            JsonArray jsonArray = JsonHelper.asArray(jsonElement, key);
            Extended[] lvs = new Extended[jsonArray.size()];
            for (int i = 0; i < jsonArray.size(); ++i) {
                lvs[i] = Extended.fromJson(key + "[" + i + "]", predicateDeserializer, jsonArray.get(i));
            }
            return lvs;
        }

        private static Extended fromJson(String key, AdvancementEntityPredicateDeserializer predicateDeserializer, @Nullable JsonElement json) {
            if (json != null && json.isJsonArray()) {
                LootCondition[] lvs = predicateDeserializer.loadConditions(json.getAsJsonArray(), predicateDeserializer.getAdvancementId().toString() + "/" + key, LootContextTypes.ADVANCEMENT_ENTITY);
                return new Extended(lvs);
            }
            EntityPredicate lv = EntityPredicate.fromJson(json);
            return Extended.ofLegacy(lv);
        }

        public static Extended ofLegacy(EntityPredicate predicate) {
            if (predicate == ANY) {
                return EMPTY;
            }
            LootCondition lv = EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, predicate).build();
            return new Extended(new LootCondition[]{lv});
        }

        public boolean test(LootContext context) {
            return this.combinedCondition.test(context);
        }

        public JsonElement toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            if (this.conditions.length == 0) {
                return JsonNull.INSTANCE;
            }
            return predicateSerializer.conditionsToJson(this.conditions);
        }

        public static JsonElement toPredicatesJsonArray(Extended[] predicates, AdvancementEntityPredicateSerializer predicateSerializer) {
            if (predicates.length == 0) {
                return JsonNull.INSTANCE;
            }
            JsonArray jsonArray = new JsonArray();
            for (Extended lv : predicates) {
                jsonArray.add(lv.toJson(predicateSerializer));
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

        public Builder type(EntityType<?> type) {
            this.type = EntityTypePredicate.create(type);
            return this;
        }

        public Builder type(Tag<EntityType<?>> tag) {
            this.type = EntityTypePredicate.create(tag);
            return this;
        }

        public Builder type(Identifier catType) {
            this.catType = catType;
            return this;
        }

        public Builder type(EntityTypePredicate type) {
            this.type = type;
            return this;
        }

        public Builder distance(DistancePredicate distance) {
            this.distance = distance;
            return this;
        }

        public Builder location(LocationPredicate location) {
            this.location = location;
            return this;
        }

        public Builder effects(EntityEffectPredicate effects) {
            this.effects = effects;
            return this;
        }

        public Builder nbt(NbtPredicate nbt) {
            this.nbt = nbt;
            return this;
        }

        public Builder flags(EntityFlagsPredicate flags) {
            this.flags = flags;
            return this;
        }

        public Builder equipment(EntityEquipmentPredicate equipment) {
            this.equipment = equipment;
            return this;
        }

        public Builder player(PlayerPredicate player) {
            this.player = player;
            return this;
        }

        public Builder fishHook(FishingHookPredicate fishHook) {
            this.fishHook = fishHook;
            return this;
        }

        public Builder vehicle(EntityPredicate vehicle) {
            this.vehicle = vehicle;
            return this;
        }

        public Builder targetedEntity(EntityPredicate targetedEntity) {
            this.targetedEntity = targetedEntity;
            return this;
        }

        public Builder team(@Nullable String team) {
            this.team = team;
            return this;
        }

        public Builder catType(@Nullable Identifier catType) {
            this.catType = catType;
            return this;
        }

        public EntityPredicate build() {
            return new EntityPredicate(this.type, this.distance, this.location, this.effects, this.nbt, this.flags, this.equipment, this.player, this.fishHook, this.vehicle, this.targetedEntity, this.team, this.catType);
        }
    }
}

