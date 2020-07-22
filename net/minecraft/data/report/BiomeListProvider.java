/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.JsonOps
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.data.report;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BiomeListProvider
implements DataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final DataGenerator dataGenerator;

    public BiomeListProvider(DataGenerator dataGenerator) {
        this.dataGenerator = dataGenerator;
    }

    @Override
    public void run(DataCache cache) {
        Path path = this.dataGenerator.getOutput();
        for (Map.Entry<RegistryKey<Biome>, Biome> entry : BuiltinRegistries.BIOME.getEntries()) {
            Path path2 = BiomeListProvider.getPath(path, entry.getKey().getValue());
            Biome lv = entry.getValue();
            Function function = JsonOps.INSTANCE.withEncoder(Biome.field_24677);
            try {
                Optional optional = ((DataResult)function.apply(() -> lv)).result();
                if (optional.isPresent()) {
                    DataProvider.writeToPath(GSON, cache, (JsonElement)optional.get(), path2);
                    continue;
                }
                LOGGER.error("Couldn't serialize biome {}", (Object)path2);
            }
            catch (IOException iOException) {
                LOGGER.error("Couldn't save biome {}", (Object)path2, (Object)iOException);
            }
        }
    }

    private static Path getPath(Path path, Identifier arg) {
        return path.resolve("reports/biomes/" + arg.getPath() + ".json");
    }

    @Override
    public String getName() {
        return "Biomes";
    }
}

