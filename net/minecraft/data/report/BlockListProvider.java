/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package net.minecraft.data.report;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Path;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

public class BlockListProvider
implements DataProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final DataGenerator root;

    public BlockListProvider(DataGenerator arg) {
        this.root = arg;
    }

    @Override
    public void run(DataCache arg) throws IOException {
        JsonObject jsonObject = new JsonObject();
        for (Block lv : Registry.BLOCK) {
            Identifier lv2 = Registry.BLOCK.getId(lv);
            JsonObject jsonObject2 = new JsonObject();
            StateManager<Block, BlockState> lv3 = lv.getStateManager();
            if (!lv3.getProperties().isEmpty()) {
                JsonObject jsonObject3 = new JsonObject();
                for (Property property : lv3.getProperties()) {
                    JsonArray jsonArray = new JsonArray();
                    for (Comparable comparable : property.getValues()) {
                        jsonArray.add(Util.getValueAsString(property, comparable));
                    }
                    jsonObject3.add(property.getName(), (JsonElement)jsonArray);
                }
                jsonObject2.add("properties", (JsonElement)jsonObject3);
            }
            JsonArray jsonArray2 = new JsonArray();
            for (BlockState blockState : lv3.getStates()) {
                JsonObject jsonObject4 = new JsonObject();
                JsonObject jsonObject5 = new JsonObject();
                for (Property<?> lv6 : lv3.getProperties()) {
                    jsonObject5.addProperty(lv6.getName(), Util.getValueAsString(lv6, blockState.get(lv6)));
                }
                if (jsonObject5.size() > 0) {
                    jsonObject4.add("properties", (JsonElement)jsonObject5);
                }
                jsonObject4.addProperty("id", (Number)Block.getRawIdFromState(blockState));
                if (blockState == lv.getDefaultState()) {
                    jsonObject4.addProperty("default", Boolean.valueOf(true));
                }
                jsonArray2.add((JsonElement)jsonObject4);
            }
            jsonObject2.add("states", (JsonElement)jsonArray2);
            jsonObject.add(lv2.toString(), (JsonElement)jsonObject2);
        }
        Path path = this.root.getOutput().resolve("reports/blocks.json");
        DataProvider.writeToPath(GSON, arg, (JsonElement)jsonObject, path);
    }

    @Override
    public String getName() {
        return "Block List";
    }
}

