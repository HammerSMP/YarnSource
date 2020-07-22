/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Sets
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.data.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.advancement.Advancement;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.server.AdventureTabAdvancementGenerator;
import net.minecraft.data.server.EndTabAdvancementGenerator;
import net.minecraft.data.server.HusbandryTabAdvancementGenerator;
import net.minecraft.data.server.NetherTabAdvancementGenerator;
import net.minecraft.data.server.StoryTabAdvancementGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdvancementsProvider
implements DataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final DataGenerator root;
    private final List<Consumer<Consumer<Advancement>>> tabGenerators = ImmutableList.of((Object)new EndTabAdvancementGenerator(), (Object)new HusbandryTabAdvancementGenerator(), (Object)new AdventureTabAdvancementGenerator(), (Object)new NetherTabAdvancementGenerator(), (Object)new StoryTabAdvancementGenerator());

    public AdvancementsProvider(DataGenerator root) {
        this.root = root;
    }

    @Override
    public void run(DataCache cache) throws IOException {
        Path path = this.root.getOutput();
        HashSet set = Sets.newHashSet();
        Consumer<Advancement> consumer = arg2 -> {
            if (!set.add(arg2.getId())) {
                throw new IllegalStateException("Duplicate advancement " + arg2.getId());
            }
            Path path2 = AdvancementsProvider.getOutput(path, arg2);
            try {
                DataProvider.writeToPath(GSON, cache, (JsonElement)arg2.createTask().toJson(), path2);
            }
            catch (IOException iOException) {
                LOGGER.error("Couldn't save advancement {}", (Object)path2, (Object)iOException);
            }
        };
        for (Consumer<Consumer<Advancement>> consumer2 : this.tabGenerators) {
            consumer2.accept(consumer);
        }
    }

    private static Path getOutput(Path rootOutput, Advancement advancement) {
        return rootOutput.resolve("data/" + advancement.getId().getNamespace() + "/advancements/" + advancement.getId().getPath() + ".json");
    }

    @Override
    public String getName() {
        return "Advancements";
    }
}

