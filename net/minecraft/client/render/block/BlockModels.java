/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@Environment(value=EnvType.CLIENT)
public class BlockModels {
    private final Map<BlockState, BakedModel> models = Maps.newIdentityHashMap();
    private final BakedModelManager modelManager;

    public BlockModels(BakedModelManager arg) {
        this.modelManager = arg;
    }

    public Sprite getSprite(BlockState arg) {
        return this.getModel(arg).getSprite();
    }

    public BakedModel getModel(BlockState arg) {
        BakedModel lv = this.models.get(arg);
        if (lv == null) {
            lv = this.modelManager.getMissingModel();
        }
        return lv;
    }

    public BakedModelManager getModelManager() {
        return this.modelManager;
    }

    public void reload() {
        this.models.clear();
        for (Block lv : Registry.BLOCK) {
            lv.getStateManager().getStates().forEach(arg -> this.models.put((BlockState)arg, this.modelManager.getModel(BlockModels.getModelId(arg))));
        }
    }

    public static ModelIdentifier getModelId(BlockState arg) {
        return BlockModels.getModelId(Registry.BLOCK.getId(arg.getBlock()), arg);
    }

    public static ModelIdentifier getModelId(Identifier arg, BlockState arg2) {
        return new ModelIdentifier(arg, BlockModels.propertyMapToString(arg2.getEntries()));
    }

    public static String propertyMapToString(Map<Property<?>, Comparable<?>> map) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<Property<?>, Comparable<?>> entry : map.entrySet()) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append(',');
            }
            Property<?> lv = entry.getKey();
            stringBuilder.append(lv.getName());
            stringBuilder.append('=');
            stringBuilder.append(BlockModels.propertyValueToString(lv, entry.getValue()));
        }
        return stringBuilder.toString();
    }

    private static <T extends Comparable<T>> String propertyValueToString(Property<T> arg, Comparable<?> comparable) {
        return arg.name(comparable);
    }
}

