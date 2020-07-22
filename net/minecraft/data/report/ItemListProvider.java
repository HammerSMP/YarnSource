/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package net.minecraft.data.report;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Path;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;

public class ItemListProvider
implements DataProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final DataGenerator root;

    public ItemListProvider(DataGenerator arg) {
        this.root = arg;
    }

    @Override
    public void run(DataCache cache) throws IOException {
        JsonObject jsonObject = new JsonObject();
        Registry.REGISTRIES.getIds().forEach(arg -> jsonObject.add(arg.toString(), ItemListProvider.toJson(Registry.REGISTRIES.get((Identifier)arg))));
        Path path = this.root.getOutput().resolve("reports/registries.json");
        DataProvider.writeToPath(GSON, cache, (JsonElement)jsonObject, path);
    }

    private static <T> JsonElement toJson(Registry<T> arg) {
        JsonObject jsonObject = new JsonObject();
        if (arg instanceof DefaultedRegistry) {
            Identifier lv = ((DefaultedRegistry)arg).getDefaultId();
            jsonObject.addProperty("default", lv.toString());
        }
        int i = Registry.REGISTRIES.getRawId(arg);
        jsonObject.addProperty("protocol_id", (Number)i);
        JsonObject jsonObject2 = new JsonObject();
        for (Identifier lv2 : arg.getIds()) {
            T object = arg.get(lv2);
            int j = arg.getRawId(object);
            JsonObject jsonObject3 = new JsonObject();
            jsonObject3.addProperty("protocol_id", (Number)j);
            jsonObject2.add(lv2.toString(), (JsonElement)jsonObject3);
        }
        jsonObject.add("entries", (JsonElement)jsonObject2);
        return jsonObject;
    }

    @Override
    public String getName() {
        return "Registry Dump";
    }
}

