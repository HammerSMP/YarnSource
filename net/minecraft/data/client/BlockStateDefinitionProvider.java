/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.data.client;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.model.BlockStateModelGenerator;
import net.minecraft.data.client.model.BlockStateSupplier;
import net.minecraft.data.client.model.ModelIds;
import net.minecraft.data.client.model.SimpleModelSupplier;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BlockStateDefinitionProvider
implements DataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final DataGenerator generator;

    public BlockStateDefinitionProvider(DataGenerator arg) {
        this.generator = arg;
    }

    @Override
    public void run(DataCache arg2) {
        Path path = this.generator.getOutput();
        HashMap map = Maps.newHashMap();
        Consumer<BlockStateSupplier> consumer = arg -> {
            Block lv = arg.getBlock();
            BlockStateSupplier lv2 = map.put(lv, arg);
            if (lv2 != null) {
                throw new IllegalStateException("Duplicate blockstate definition for " + lv);
            }
        };
        HashMap map2 = Maps.newHashMap();
        HashSet set = Sets.newHashSet();
        BiConsumer<Identifier, Supplier<JsonElement>> biConsumer = (arg, supplier) -> {
            Supplier supplier2 = map2.put(arg, supplier);
            if (supplier2 != null) {
                throw new IllegalStateException("Duplicate model definition for " + arg);
            }
        };
        Consumer<Item> consumer2 = set::add;
        new BlockStateModelGenerator(consumer, biConsumer, consumer2).register();
        new ItemModelGenerator(biConsumer).register();
        List list = Registry.BLOCK.stream().filter(arg -> !map.containsKey(arg)).collect(Collectors.toList());
        if (!list.isEmpty()) {
            throw new IllegalStateException("Missing blockstate definitions for: " + list);
        }
        Registry.BLOCK.forEach(arg -> {
            Item lv = Item.BLOCK_ITEMS.get(arg);
            if (lv != null) {
                if (set.contains(lv)) {
                    return;
                }
                Identifier lv2 = ModelIds.getItemModelId(lv);
                if (!map2.containsKey(lv2)) {
                    map2.put(lv2, new SimpleModelSupplier(ModelIds.getBlockModelId(arg)));
                }
            }
        });
        this.writeJsons(arg2, path, map, BlockStateDefinitionProvider::getBlockStateJsonPath);
        this.writeJsons(arg2, path, map2, BlockStateDefinitionProvider::getModelJsonPath);
    }

    private <T> void writeJsons(DataCache arg, Path path, Map<T, ? extends Supplier<JsonElement>> map, BiFunction<Path, T, Path> biFunction) {
        map.forEach((object, supplier) -> {
            Path path2 = (Path)biFunction.apply(path, object);
            try {
                DataProvider.writeToPath(GSON, arg, (JsonElement)supplier.get(), path2);
            }
            catch (Exception exception) {
                LOGGER.error("Couldn't save {}", (Object)path2, (Object)exception);
            }
        });
    }

    private static Path getBlockStateJsonPath(Path path, Block arg) {
        Identifier lv = Registry.BLOCK.getId(arg);
        return path.resolve("assets/" + lv.getNamespace() + "/blockstates/" + lv.getPath() + ".json");
    }

    private static Path getModelJsonPath(Path path, Identifier arg) {
        return path.resolve("assets/" + arg.getNamespace() + "/models/" + arg.getPath() + ".json");
    }

    @Override
    public String getName() {
        return "Block State Definitions";
    }
}

