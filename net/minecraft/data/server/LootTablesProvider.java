/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Sets
 *  com.google.common.collect.Sets$SetView
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.mojang.datafixers.util.Pair
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.data.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.datafixers.util.Pair;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.server.BarterLootTableGenerator;
import net.minecraft.data.server.BlockLootTableGenerator;
import net.minecraft.data.server.ChestLootTableGenerator;
import net.minecraft.data.server.EntityLootTableGenerator;
import net.minecraft.data.server.FishingLootTableGenerator;
import net.minecraft.data.server.GiftLootTableGenerator;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootTablesProvider
implements DataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final DataGenerator root;
    private final List<Pair<Supplier<Consumer<BiConsumer<Identifier, LootTable.Builder>>>, LootContextType>> lootTypeGenerators = ImmutableList.of((Object)Pair.of(FishingLootTableGenerator::new, (Object)LootContextTypes.FISHING), (Object)Pair.of(ChestLootTableGenerator::new, (Object)LootContextTypes.CHEST), (Object)Pair.of(EntityLootTableGenerator::new, (Object)LootContextTypes.ENTITY), (Object)Pair.of(BlockLootTableGenerator::new, (Object)LootContextTypes.BLOCK), (Object)Pair.of(BarterLootTableGenerator::new, (Object)LootContextTypes.BARTER), (Object)Pair.of(GiftLootTableGenerator::new, (Object)LootContextTypes.GIFT));

    public LootTablesProvider(DataGenerator arg) {
        this.root = arg;
    }

    @Override
    public void run(DataCache arg4) {
        Path path = this.root.getOutput();
        HashMap map = Maps.newHashMap();
        this.lootTypeGenerators.forEach(pair -> ((Consumer)((Supplier)pair.getFirst()).get()).accept((arg, arg2) -> {
            if (map.put(arg, arg2.withType((LootContextType)pair.getSecond()).create()) != null) {
                throw new IllegalStateException("Duplicate loot table " + arg);
            }
        }));
        LootTableReporter lv = new LootTableReporter(LootContextTypes.GENERIC, arg -> null, map::get);
        Sets.SetView set = Sets.difference(LootTables.getAll(), map.keySet());
        for (Identifier lv2 : set) {
            lv.report("Missing built-in table: " + lv2);
        }
        map.forEach((arg2, arg3) -> LootManager.check(lv, arg2, arg3));
        Multimap<String, String> multimap = lv.getMessages();
        if (!multimap.isEmpty()) {
            multimap.forEach((string, string2) -> LOGGER.warn("Found validation problem in " + string + ": " + string2));
            throw new IllegalStateException("Failed to validate loot tables, see logs");
        }
        map.forEach((arg2, arg3) -> {
            Path path2 = LootTablesProvider.getOutput(path, arg2);
            try {
                DataProvider.writeToPath(GSON, arg4, LootManager.toJson(arg3), path2);
            }
            catch (IOException iOException) {
                LOGGER.error("Couldn't save loot table {}", (Object)path2, (Object)iOException);
            }
        });
    }

    private static Path getOutput(Path path, Identifier arg) {
        return path.resolve("data/" + arg.getNamespace() + "/loot_tables/" + arg.getPath() + ".json");
    }

    @Override
    public String getName() {
        return "LootTables";
    }
}

