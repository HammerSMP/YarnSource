/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.loot.function;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Locale;
import java.util.Set;
import net.minecraft.class_5339;
import net.minecraft.class_5341;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapIcon;
import net.minecraft.item.map.MapState;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctions;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.StructureFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExplorationMapLootFunction
extends ConditionalLootFunction {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final StructureFeature<?> field_25032 = StructureFeature.BURIED_TREASURE;
    public static final MapIcon.Type DEFAULT_DECORATION = MapIcon.Type.MANSION;
    private final StructureFeature<?> destination;
    private final MapIcon.Type decoration;
    private final byte zoom;
    private final int searchRadius;
    private final boolean skipExistingChunks;

    private ExplorationMapLootFunction(class_5341[] args, StructureFeature<?> arg, MapIcon.Type arg2, byte b, int i, boolean bl) {
        super(args);
        this.destination = arg;
        this.decoration = arg2;
        this.zoom = b;
        this.searchRadius = i;
        this.skipExistingChunks = bl;
    }

    @Override
    public class_5339 method_29321() {
        return LootFunctions.EXPLORATION_MAP;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(LootContextParameters.POSITION);
    }

    @Override
    public ItemStack process(ItemStack arg, LootContext arg2) {
        ServerWorld lv2;
        BlockPos lv3;
        if (arg.getItem() != Items.MAP) {
            return arg;
        }
        BlockPos lv = arg2.get(LootContextParameters.POSITION);
        if (lv != null && (lv3 = (lv2 = arg2.getWorld()).locateStructure(this.destination, lv, this.searchRadius, this.skipExistingChunks)) != null) {
            ItemStack lv4 = FilledMapItem.createMap(lv2, lv3.getX(), lv3.getZ(), this.zoom, true, true);
            FilledMapItem.fillExplorationMap(lv2, lv4);
            MapState.addDecorationsTag(lv4, lv3, "+", this.decoration);
            lv4.setCustomName(new TranslatableText("filled_map." + this.destination.getName().toLowerCase(Locale.ROOT)));
            return lv4;
        }
        return arg;
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Factory
    extends ConditionalLootFunction.Factory<ExplorationMapLootFunction> {
        @Override
        public void toJson(JsonObject jsonObject, ExplorationMapLootFunction arg, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, arg, jsonSerializationContext);
            if (!arg.destination.equals(field_25032)) {
                jsonObject.add("destination", jsonSerializationContext.serialize((Object)arg.destination.getName()));
            }
            if (arg.decoration != DEFAULT_DECORATION) {
                jsonObject.add("decoration", jsonSerializationContext.serialize((Object)arg.decoration.toString().toLowerCase(Locale.ROOT)));
            }
            if (arg.zoom != 2) {
                jsonObject.addProperty("zoom", (Number)arg.zoom);
            }
            if (arg.searchRadius != 50) {
                jsonObject.addProperty("search_radius", (Number)arg.searchRadius);
            }
            if (!arg.skipExistingChunks) {
                jsonObject.addProperty("skip_existing_chunks", Boolean.valueOf(arg.skipExistingChunks));
            }
        }

        @Override
        public ExplorationMapLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_5341[] args) {
            StructureFeature<?> lv = Factory.method_29039(jsonObject);
            String string = jsonObject.has("decoration") ? JsonHelper.getString(jsonObject, "decoration") : "mansion";
            MapIcon.Type lv2 = DEFAULT_DECORATION;
            try {
                lv2 = MapIcon.Type.valueOf(string.toUpperCase(Locale.ROOT));
            }
            catch (IllegalArgumentException illegalArgumentException) {
                LOGGER.error("Error while parsing loot table decoration entry. Found {}. Defaulting to " + (Object)((Object)DEFAULT_DECORATION), (Object)string);
            }
            byte b = JsonHelper.getByte(jsonObject, "zoom", (byte)2);
            int i = JsonHelper.getInt(jsonObject, "search_radius", 50);
            boolean bl = JsonHelper.getBoolean(jsonObject, "skip_existing_chunks", true);
            return new ExplorationMapLootFunction(args, lv, lv2, b, i, bl);
        }

        private static StructureFeature<?> method_29039(JsonObject jsonObject) {
            String string;
            StructureFeature lv;
            if (jsonObject.has("destination") && (lv = (StructureFeature)StructureFeature.STRUCTURES.get((Object)(string = JsonHelper.getString(jsonObject, "destination")).toLowerCase(Locale.ROOT))) != null) {
                return lv;
            }
            return field_25032;
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_5341[] args) {
            return this.fromJson(jsonObject, jsonDeserializationContext, args);
        }
    }

    public static class Builder
    extends ConditionalLootFunction.Builder<Builder> {
        private StructureFeature<?> destination = field_25032;
        private MapIcon.Type decoration = DEFAULT_DECORATION;
        private byte zoom = (byte)2;
        private int searchRadius = 50;
        private boolean skipExistingChunks = true;

        @Override
        protected Builder getThisBuilder() {
            return this;
        }

        public Builder withDestination(StructureFeature<?> arg) {
            this.destination = arg;
            return this;
        }

        public Builder withDecoration(MapIcon.Type arg) {
            this.decoration = arg;
            return this;
        }

        public Builder withZoom(byte b) {
            this.zoom = b;
            return this;
        }

        public Builder withSkipExistingChunks(boolean bl) {
            this.skipExistingChunks = bl;
            return this;
        }

        @Override
        public LootFunction build() {
            return new ExplorationMapLootFunction(this.getConditions(), this.destination, this.decoration, this.zoom, this.searchRadius, this.skipExistingChunks);
        }

        @Override
        protected /* synthetic */ ConditionalLootFunction.Builder getThisBuilder() {
            return this.getThisBuilder();
        }
    }
}

