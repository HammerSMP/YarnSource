/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Maps
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSyntaxException
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.recipe;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecipeManager
extends JsonDataLoader {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogManager.getLogger();
    private Map<RecipeType<?>, Map<Identifier, Recipe<?>>> recipes = ImmutableMap.of();
    private boolean errored;

    public RecipeManager() {
        super(GSON, "recipes");
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> map, ResourceManager arg2, Profiler arg22) {
        this.errored = false;
        HashMap map2 = Maps.newHashMap();
        for (Map.Entry<Identifier, JsonElement> entry2 : map.entrySet()) {
            Identifier lv = entry2.getKey();
            try {
                Recipe<?> lv2 = RecipeManager.deserialize(lv, JsonHelper.asObject(entry2.getValue(), "top element"));
                map2.computeIfAbsent(lv2.getType(), arg -> ImmutableMap.builder()).put((Object)lv, lv2);
            }
            catch (JsonParseException | IllegalArgumentException runtimeException) {
                LOGGER.error("Parsing error loading recipe {}", (Object)lv, (Object)runtimeException);
            }
        }
        this.recipes = (Map)map2.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry -> ((ImmutableMap.Builder)entry.getValue()).build()));
        LOGGER.info("Loaded {} recipes", (Object)map2.size());
    }

    public <C extends Inventory, T extends Recipe<C>> Optional<T> getFirstMatch(RecipeType<T> arg, C arg2, World arg3) {
        return this.getAllOfType(arg).values().stream().flatMap(arg4 -> Util.stream(arg.get(arg4, arg3, arg2))).findFirst();
    }

    public <C extends Inventory, T extends Recipe<C>> List<T> getAllMatches(RecipeType<T> arg2, C arg22, World arg3) {
        return this.getAllOfType(arg2).values().stream().flatMap(arg4 -> Util.stream(arg2.get(arg4, arg3, arg22))).sorted(Comparator.comparing(arg -> arg.getOutput().getTranslationKey())).collect(Collectors.toList());
    }

    private <C extends Inventory, T extends Recipe<C>> Map<Identifier, Recipe<C>> getAllOfType(RecipeType<T> arg) {
        return this.recipes.getOrDefault(arg, Collections.emptyMap());
    }

    public <C extends Inventory, T extends Recipe<C>> DefaultedList<ItemStack> getRemainingStacks(RecipeType<T> arg, C arg2, World arg3) {
        Optional<T> optional = this.getFirstMatch(arg, arg2, arg3);
        if (optional.isPresent()) {
            return ((Recipe)optional.get()).getRemainingStacks(arg2);
        }
        DefaultedList<ItemStack> lv = DefaultedList.ofSize(arg2.size(), ItemStack.EMPTY);
        for (int i = 0; i < lv.size(); ++i) {
            lv.set(i, arg2.getStack(i));
        }
        return lv;
    }

    public Optional<? extends Recipe<?>> get(Identifier arg) {
        return this.recipes.values().stream().map(map -> (Recipe)map.get(arg)).filter(Objects::nonNull).findFirst();
    }

    public Collection<Recipe<?>> values() {
        return this.recipes.values().stream().flatMap(map -> map.values().stream()).collect(Collectors.toSet());
    }

    public Stream<Identifier> keys() {
        return this.recipes.values().stream().flatMap(map -> map.keySet().stream());
    }

    public static Recipe<?> deserialize(Identifier arg, JsonObject jsonObject) {
        String string = JsonHelper.getString(jsonObject, "type");
        return Registry.RECIPE_SERIALIZER.getOrEmpty(new Identifier(string)).orElseThrow(() -> new JsonSyntaxException("Invalid or unsupported recipe type '" + string + "'")).read(arg, jsonObject);
    }

    @Environment(value=EnvType.CLIENT)
    public void setRecipes(Iterable<Recipe<?>> iterable) {
        this.errored = false;
        HashMap map = Maps.newHashMap();
        iterable.forEach(arg2 -> {
            Map map2 = map.computeIfAbsent(arg2.getType(), arg -> Maps.newHashMap());
            Recipe lv = map2.put(arg2.getId(), arg2);
            if (lv != null) {
                throw new IllegalStateException("Duplicate recipe ignored with ID " + arg2.getId());
            }
        });
        this.recipes = ImmutableMap.copyOf((Map)map);
    }
}

